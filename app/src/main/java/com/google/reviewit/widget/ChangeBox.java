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
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.R;
import com.google.reviewit.app.Change;
import com.google.reviewit.app.Preferences;
import com.google.reviewit.app.ReviewItApp;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.ObservableAsynTask;
import com.google.reviewit.util.WidgetUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChangeBox extends RelativeLayout {
  private static final String TAG = ChangeBox.class.getName();

  private final WidgetUtil widgetUtil;

  public ChangeBox(Context context) {
    this(context, null);
  }

  public ChangeBox(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.widgetUtil = new WidgetUtil(context);

    inflate(context, R.layout.change_box, this);
    RelativeLayout.LayoutParams layoutParams =
        new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    int activityHorizontalMargin = widgetUtil.getDimension(
        R.dimen.activity_horizontal_margin);
    int activityVerticalMargin = widgetUtil.getDimension(
        R.dimen.activity_vertical_margin);
    int reviewitBottomAreaHeight = widgetUtil.getDimension(
        R.dimen.reviewit_bottom_area_height);
    layoutParams.setMargins(activityHorizontalMargin, activityVerticalMargin,
        activityHorizontalMargin, reviewitBottomAreaHeight);
    setLayoutParams(layoutParams);

    if (getId() < 0) {
      setId(R.id.changeBox);
    }

    GradientDrawable gd = new GradientDrawable();
    gd.setColor(widgetUtil.color(R.color.commitMessage));
    gd.setCornerRadius(widgetUtil.dpToPx(20));
    gd.setStroke(1, widgetUtil.color(R.color.commitMessage4));
    WidgetUtil.setBackground(this, gd);
  }

  public void display(ReviewItApp app, Change change) {
    configureInfo(app);

    ChangeInfo info = change.info;
    ((ProjectBranchTopicAgeView)v(R.id.projectBranchTopicAge)).init(change);
    ((UserView)v(R.id.owner)).init(app, info.owner);
    WidgetUtil.setText(v(R.id.subject), info.subject);
    WidgetUtil.setText(v(R.id.commitMessage),
        FormatUtil.formatMessage(change));
    WidgetUtil.setText(v(R.id.patchsets),
        FormatUtil.format(change.currentRevision()._number));
    setInlineCommentCount(app, change);
    Change.Voters codeReviewVoters = change.voters("Code-Review");
    WidgetUtil.setText(this, R.id.likes,
        FormatUtil.format(codeReviewVoters.likers.size()));
    WidgetUtil.setText(this, R.id.dislikes,
        FormatUtil.format(codeReviewVoters.dislikers.size()));
    Collection<AccountInfo> reviewers = change.reviewers(true);
    WidgetUtil.setText(v(R.id.reviewers),
        FormatUtil.format(reviewers.size()));
    setCommitMessageFont(app);
    colorIcons(app, codeReviewVoters, reviewers);
  }

  private void configureInfo(ReviewItApp app) {
    Preferences prefs = app.getPrefs();
    WidgetUtil.setVisible(prefs.showPatchSets,
        v(R.id.patchsets, R.id.patchSetIcon));
    WidgetUtil.setVisible(prefs.showPositiveCodeReviewVotes,
        v(R.id.likes, R.id.positiveReviewIcon));
    WidgetUtil.setVisible(prefs.showNegativeCodeReviewVotes,
        v(R.id.dislikes, R.id.negativeReviewIcon));
    WidgetUtil.setVisible(prefs.showComments,
        v(R.id.comments, R.id.commentIcon));
    WidgetUtil.setVisible(prefs.showReviewers,
        v(R.id.reviewers, R.id.reviewerIcon));
  }

  private void setCommitMessageFont(ReviewItApp app) {
    Preferences prefs = app.getPrefs();
    if (!prefs.autoFontSizeForCommitMessage()) {
      MaxFontSizeTextView commitMessage =
          ((MaxFontSizeTextView) v(R.id.commitMessage));
      commitMessage.setMinTextSize(prefs.commitMessageFontSize);
      commitMessage.setMaxTextSize(prefs.commitMessageFontSize);
    }
  }

  /**
   * Highlight icons for the current user by a different color.
   */
  private void colorIcons(
      ReviewItApp app, Change.Voters codeReviewVoters,
      Collection<AccountInfo> reviewers) {
    colorIcon(R.id.positiveReviewIcon,
        containsSelf(app, codeReviewVoters.likers)
            ? R.color.iconHighlightedGreen
            : R.color.icon);
    colorIcon(R.id.negativeReviewIcon,
        containsSelf(app, codeReviewVoters.dislikers)
            ? R.color.iconHighlightedRed
            : R.color.icon);
    colorIcon(R.id.commentIcon, R.color.icon);
    colorIcon(R.id.reviewerIcon,
        containsSelf(app, reviewers)
            ? R.color.iconHighlightedGreen
            : R.color.icon);
  }

  private void colorIcon(@IdRes int id, @ColorRes int colorId) {
    ((ImageView) v(id)).setColorFilter(widgetUtil.color(colorId));
  }

  private void setInlineCommentCount(final ReviewItApp app, Change change) {
    new ObservableAsynTask<Change, Void, Integer>() {
      private TextView comments;

      @Override
      protected void preExecute() {
        super.preExecute();
        comments = (TextView) v(R.id.comments);
      }

      @Override
      protected Integer doInBackground(Change... changes) {
        Change change = changes[0];
        try {
          return change.getInlineCommentCount();
        } catch (RestApiException e) {
          Log.e(TAG, "Failed to count inline comments of change "
              + change.info._number, e);
          return null;
        }
      }

      @Override
      protected void postExecute(Integer count) {
        if (count == null) {
          return;
        }
        comments.setText(FormatUtil.format(count));
        if (app.getPrefs().showComments) {
          WidgetUtil.setVisible(comments);
        }
      }
    }.executeOnExecutor(app.getExecutor(), change);
  }

  private boolean containsSelf(
      ReviewItApp app, Collection<AccountInfo> accounts) {
    AccountInfo self = app.getSelf();
    if (self == null) {
      return false;
    }
    for (AccountInfo account : accounts) {
      if (self._accountId.equals(account._accountId)) {
        return true;
      }
    }
    return false;
  }

  private View v(@IdRes int id) {
    return findViewById(id);
  }

  private View[] v(@IdRes int id, @IdRes int... moreIds) {
    List<View> views = new ArrayList<>(moreIds.length + 1);
    views.add(v(id));
    for (@IdRes int yaId : moreIds) {
      views.add(v(yaId));
    }
    return views.toArray(new View[views.size()]);
  }
}
