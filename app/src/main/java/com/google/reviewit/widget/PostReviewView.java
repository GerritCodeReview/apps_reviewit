// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.reviewit.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.BaseFragment;
import com.google.reviewit.R;
import com.google.reviewit.app.Change;
import com.google.reviewit.app.ReviewItApp;
import com.google.reviewit.util.ObservableAsyncTask;
import com.google.reviewit.util.WidgetUtil;
import com.urswolfer.gerrit.client.rest.http.HttpStatusException;

import java.util.HashMap;
import java.util.Map;

import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setVisible;

public class PostReviewView extends LinearLayout {
  private final WidgetUtil widgetUtil;
  private final Map<String, Integer> selectedValues = new HashMap<>();

  public PostReviewView(Context context) {
    this(context, null, 0);
  }

  public PostReviewView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PostReviewView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.widgetUtil = new WidgetUtil(getContext());
    inflate(context, R.layout.post_review, this);
  }

  public void init(ReviewItApp app, BaseFragment origin, int vote,
                   Class<? extends BaseFragment> target) {
    update(vote);
    Change change = app.getCurrentChange();

    init(origin, change, target);
    initLabels(change, vote);
    ((ApprovalsView) findViewById(R.id.approvals)).displayApprovals(app,
        change.info, origin);
  }

  private void init(
      final BaseFragment origin, final Change change,
      final Class<? extends BaseFragment> target) {
    ((ExpandableCommitMessageView)findViewById(R.id.commitMessage))
        .init(change);

    findViewById(R.id.postReviewButton).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View v) {
            v.setEnabled(false);
            v.setBackgroundColor(widgetUtil.color(R.color.buttonDisabled));
            final String msg = ((TextView) findViewById(R.id.changeMessageInput))
                .getText().toString().trim();
            new ObservableAsyncTask<Change, Void, String>() {
              @Override
              protected String doInBackground(Change... changes) {
                Change change = changes[0];
                try {
                  change.postReview(msg, selectedValues);
                  change.reload();
                  return null;
                } catch (RestApiException e) {
                  if (e instanceof HttpStatusException) {
                    HttpStatusException se = (HttpStatusException) e;
                    return origin.getString(R.string.review_error,
                        se.getStatusCode(), se.getStatusText());
                  } else {
                    return e.getMessage();
                  }
                }
              }

              @Override
              protected void postExecute(String errorMsg) {
                if (errorMsg != null) {
                  v.setEnabled(true);
                  v.setBackgroundColor(widgetUtil.color(R.color.button));
                  widgetUtil.showError(errorMsg);
                } else {
                  origin.display(target);
                }
              }
            }.execute(change);
          }
        });

    findViewById(R.id.postReviewAndSubmitButton).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View v) {
            v.setEnabled(false);
            v.setBackgroundColor(widgetUtil.color(R.color.buttonDisabled));
            final String msg = ((TextView) findViewById(R.id.changeMessageInput))
                .getText().toString().trim();
            new ObservableAsyncTask<Change, Void, String>() {
              @Override
              protected String doInBackground(Change... changes) {
                Change change = changes[0];
                try {
                  change.postReview(msg, selectedValues);
                } catch (RestApiException e) {
                  if (e instanceof HttpStatusException) {
                    HttpStatusException se = (HttpStatusException) e;
                    return origin.getString(R.string.review_error,
                        se.getStatusCode(), se.getStatusText());
                  } else {
                    return e.getMessage();
                  }
                }

                try {
                  change.submit();
                  change.reload();
                  return null;
                } catch (RestApiException e) {
                  if (e instanceof HttpStatusException) {
                    HttpStatusException se = (HttpStatusException) e;
                    return origin.getString(R.string.submit_error,
                        se.getStatusCode(), se.getStatusText());
                  } else {
                    return e.getMessage();
                  }
                }
              }

              @Override
              protected void postExecute(String errorMsg) {
                if (errorMsg != null) {
                  v.setEnabled(true);
                  v.setBackgroundColor(widgetUtil.color(R.color.button));
                  widgetUtil.showError(errorMsg);
                } else {
                  origin.display(target);
                }
              }
            }.execute(change);
          }
        });
  }

  private void initLabels(Change change, int codeReviewVote) {
    selectedValues.put("Code-Review", codeReviewVote);

    Map<String, Integer> votes = new HashMap<>();
    votes.put("Code-Review", codeReviewVote);

    ViewGroup voteInput = (ViewGroup)findViewById(R.id.voteInput);
    VoteView voteView = new VoteView(getContext());
    voteView.init(change, votes);
    voteInput.addView(voteView);

    voteView.addOnSelectListener(new VoteView.OnSelectListener() {
      @Override
      public void onSelect(String label, int vote) {
        selectedValues.put(label, vote);
        if ("Code-Review".equals(label)) {
          update(vote);
        }
      }
    });
  }

  private void update(int codeReviewVote) {
    setGone(findViewById(R.id.postReviewAndSubmitButton));

    @DrawableRes int iconRes;
    if (codeReviewVote <= -2) {
      iconRes = R.drawable.ic_sentiment_very_dissatisfied_white_48dp;
    } else if (codeReviewVote == -1) {
      iconRes = R.drawable.ic_sentiment_dissatisfied_white_48dp;
    } else if (codeReviewVote == 0) {
      iconRes = R.drawable.ic_sentiment_neutral_white_48dp;
    } else if (codeReviewVote == 1) {
      iconRes = R.drawable.ic_sentiment_satisfied_white_48dp;
    } else {
      iconRes = R.drawable.ic_sentiment_very_satisfied_white_48dp;
      setVisible(findViewById(R.id.postReviewAndSubmitButton));
    }
    ((ImageView) findViewById(R.id.emoticon)).setImageDrawable(
        widgetUtil.getDrawable(iconRes));
  }
}
