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

package com.google.reviewit;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.ObservableAsynTask;
import com.google.reviewit.util.TaskObserver;
import com.google.reviewit.util.WidgetUtil;
import com.google.reviewit.widget.ApprovalsView;
import com.google.reviewit.widget.VoteView;
import com.urswolfer.gerrit.client.rest.http.HttpStatusException;

import java.util.HashMap;
import java.util.Map;

import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setVisible;

/**
 * Fragment to post a review
 */
public class PostReviewFragment extends BaseFragment {
  private static final String VOTE =
      "com.google.reviewit.PostReviewFragment.VOTE";

  private Map<String, Integer> selectedValues = new HashMap<>();

  public static PostReviewFragment create(int vote) {
    PostReviewFragment fragment = new PostReviewFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(VOTE, vote);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_post_review;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    TaskObserver.enableProgressBar(getWindow());

    // do not show keyboard by default
    getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    int vote = getArguments().getInt(VOTE);
    update(vote);
    Change change = getApp().getActionHandler().getCurrentChange();

    setTitle(getString(R.string.detailed_change_title, change.info._number));
    init(change);
    WidgetUtil.setText(v(R.id.subject), change.info.subject);
    initLabels(change, vote);
    ((ApprovalsView) v(R.id.approvals)).displayApprovals(getApp(),
        change.info, this);
  }

  private void init(final Change change) {
    v(R.id.expandCommitMessage).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setGone(v(R.id.expandCommitMessage));
        setVisible(v(R.id.collapseCommitMessage));
        WidgetUtil.setText(v(R.id.subject),
            change.currentRevision().commit.message);
      }
    });

    v(R.id.collapseCommitMessage).setOnClickListener(
        new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setGone(v(R.id.collapseCommitMessage));
        setVisible(v(R.id.expandCommitMessage));
        WidgetUtil.setText(v(R.id.subject), change.info.subject);
      }
    });

    v(R.id.postReviewButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View v) {
        v.setEnabled(false);
        v.setBackgroundColor(widgetUtil.color(R.color.buttonDisabled));
        final String msg = textOf(R.id.changeMessageInput).trim();
        new ObservableAsynTask<Change, Void, String>() {
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
                return getString(R.string.review_error, se.getStatusCode(),
                    se.getStatusText());
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
              display(SortChangesFragment.class);
            }
          }
        }.execute(change);
      }
    });

    v(R.id.postReviewAndSubmitButton).setOnClickListener(
        new View.OnClickListener() {
      @Override
      public void onClick(final View v) {
        v.setEnabled(false);
        v.setBackgroundColor(widgetUtil.color(R.color.buttonDisabled));
        final String msg = textOf(R.id.changeMessageInput).trim();
        new ObservableAsynTask<Change, Void, String>() {
          @Override
          protected String doInBackground(Change... changes) {
            Change change = changes[0];
            try {
              change.postReview(msg, selectedValues);
            } catch (RestApiException e) {
              if (e instanceof HttpStatusException) {
                HttpStatusException se = (HttpStatusException) e;
                return getString(R.string.review_error, se.getStatusCode(),
                    se.getStatusText());
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
                return getString(R.string.submit_error, se.getStatusCode(),
                    se.getStatusText());
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
              display(SortChangesFragment.class);
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

    ViewGroup voteInput = vg(R.id.voteInput);
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
    setGone(v(R.id.postReviewAndSubmitButton));

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
      setVisible(v(R.id.postReviewAndSubmitButton));
    }
    ((ImageView) v(R.id.emoticon)).setImageDrawable(
        widgetUtil.getDrawable(iconRes));
  }
}
