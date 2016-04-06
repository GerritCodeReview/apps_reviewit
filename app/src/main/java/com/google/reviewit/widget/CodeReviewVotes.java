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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gerrit.extensions.common.ApprovalInfo;
import com.google.reviewit.R;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.WidgetUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.reviewit.util.LayoutUtil.fixedLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapLayout;
import static com.google.reviewit.util.LayoutUtil.matchLayout;
import static com.google.reviewit.util.WidgetUtil.setInvisible;

public class CodeReviewVotes extends LinearLayout {

  private final WidgetUtil widgetUtil;

  public CodeReviewVotes(Context context) {
    this(context, null, 0);
  }

  public CodeReviewVotes(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CodeReviewVotes(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.widgetUtil = new WidgetUtil(getContext());

    setOrientation(VERTICAL);
  }

  public void init(Change change) {
    List<ApprovalInfo> codeReviewApprovals =
        (change.info.labels != null
            && change.info.labels.get("Code-Review") != null)
          ? change.info.labels.get("Code-Review").all : null;

    if (codeReviewApprovals == null) {
      setLayoutParams(new LinearLayout.LayoutParams(
          widgetUtil.dpToPx(48), widgetUtil.dpToPx(48)));
      return;
    }

    Set<Integer> values = new HashSet<>();
    for (ApprovalInfo approval : codeReviewApprovals) {
      if (approval.value != null) {
        values.add(approval.value);
      }
    }

    values.remove(0);

    if (values.isEmpty()) {
      ImageView icon = createVoteIcon(0);
      icon.setLayoutParams(matchLayout());
      setInvisible(icon);
      addView(icon);
      return;
    }

    if (values.size() == 1) {
      ImageView icon = createVoteIcon(values.iterator().next());
      icon.setLayoutParams(matchLayout());
      addView(icon);
    } else {
      LinearLayout plusIcons = new LinearLayout(getContext());
      plusIcons.setLayoutParams(matchAndWrapLayout());
      addView(plusIcons);

      ImageView iconPlus2 = createVoteIcon(2);
      iconPlus2.setLayoutParams(
          fixedLayout(widgetUtil.dpToPx(24), widgetUtil.dpToPx(24)));
      if (!values.contains(2)) {
        setInvisible(iconPlus2);
      }
      plusIcons.addView(iconPlus2);

      ImageView iconPlus1 = createVoteIcon(1);
      iconPlus1.setLayoutParams(
          fixedLayout(widgetUtil.dpToPx(24), widgetUtil.dpToPx(24)));
      if (!values.contains(1)) {
        setInvisible(iconPlus1);
      }
      plusIcons.addView(iconPlus1);

      LinearLayout minusIcons = new LinearLayout(getContext());
      minusIcons.setLayoutParams(matchAndWrapLayout());
      addView(minusIcons);

      ImageView iconMinus2 = createVoteIcon(-2);
      iconMinus2.setLayoutParams(
          fixedLayout(widgetUtil.dpToPx(24), widgetUtil.dpToPx(24)));
      if (!values.contains(-2)) {
        setInvisible(iconMinus2);
      }
      minusIcons.addView(iconMinus2);

      ImageView iconMinus1 = createVoteIcon(-1);
      iconMinus1.setLayoutParams(
          fixedLayout(widgetUtil.dpToPx(24), widgetUtil.dpToPx(24)));
      if (!values.contains(-1)) {
        setInvisible(iconMinus1);
      }
      minusIcons.addView(iconMinus1);
    }
  }

  private ImageView createVoteIcon(int codeReviewVote) {
    ImageView icon = new ImageView(getContext());
    @DrawableRes int iconRes;
    if (codeReviewVote <= -2) {
      icon.setColorFilter(widgetUtil.color(R.color.votingNegativeSelected));
      iconRes = R.drawable.ic_sentiment_very_dissatisfied_white_48dp;
    } else if (codeReviewVote == -1) {
      icon.setColorFilter(widgetUtil.color(R.color.votingNegative));
      iconRes = R.drawable.ic_sentiment_dissatisfied_white_48dp;
    } else if (codeReviewVote == 0) {
      icon.setColorFilter(widgetUtil.color(R.color.votingNeutral));
      iconRes = R.drawable.ic_sentiment_neutral_white_48dp;
    } else if (codeReviewVote == 1) {
      icon.setColorFilter(widgetUtil.color(R.color.votingPositive));
      iconRes = R.drawable.ic_sentiment_satisfied_white_48dp;
    } else {
      icon.setColorFilter(widgetUtil.color(R.color.votingPositiveSelected));
      iconRes = R.drawable.ic_sentiment_very_satisfied_white_48dp;
    }
    icon.setImageDrawable(widgetUtil.getDrawable(iconRes));
    return icon;
  }
}
