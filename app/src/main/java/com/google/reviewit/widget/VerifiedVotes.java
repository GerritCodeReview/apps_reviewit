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

import static com.google.reviewit.util.LayoutUtil.matchLayout;
import static com.google.reviewit.util.WidgetUtil.setInvisible;

public class VerifiedVotes extends LinearLayout {
  private final WidgetUtil widgetUtil;

  public VerifiedVotes(Context context) {
    this(context, null, 0);
  }

  public VerifiedVotes(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VerifiedVotes(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.widgetUtil = new WidgetUtil(getContext());

    setOrientation(VERTICAL);
  }

  public void init(Change change) {
    List<ApprovalInfo> codeReviewApprovals =
        (change.info.labels != null
            && change.info.labels.get("Verified") != null)
            ? change.info.labels.get("Verified").all : null;

    if (codeReviewApprovals == null) {
      setLayoutParams(new LinearLayout.LayoutParams(
          widgetUtil.dpToPx(48), widgetUtil.dpToPx(48)));
      return;
    }

    Set<Integer> values = new HashSet<>();
    for (ApprovalInfo approval : codeReviewApprovals) {
      values.add(approval.value);
    }

    values.remove(0);

    if (values.contains(-1)) {
      ImageView icon = createVoteIcon(-1);
      icon.setLayoutParams(matchLayout());
      addView(icon);
    } else if (values.contains(1)) {
      ImageView icon = createVoteIcon(1);
      icon.setLayoutParams(matchLayout());
      addView(icon);
    } else {
      ImageView icon = createVoteIcon(0);
      icon.setLayoutParams(matchLayout());
      setInvisible(icon);
      addView(icon);
    }
  }

  private ImageView createVoteIcon(int verifiedVote) {
    ImageView icon = new ImageView(getContext());
    @DrawableRes int iconRes;
    if (verifiedVote == -1) {
      icon.setColorFilter(widgetUtil.color(R.color.voteNegative));
      iconRes = R.drawable.ic_clear_black_48dp;
    } else if (verifiedVote == 0) {
      iconRes = R.drawable.ic_exposure_zero_black_48dp;
    } else {
      icon.setColorFilter(widgetUtil.color(R.color.votePositive));
      iconRes = R.drawable.ic_done_black_48dp;
    }
    icon.setImageDrawable(widgetUtil.getDrawable(iconRes));
    return icon;
  }
}
