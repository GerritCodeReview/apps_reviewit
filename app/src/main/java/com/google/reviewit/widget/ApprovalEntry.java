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
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.common.ApprovalInfo;
import com.google.gerrit.extensions.common.LabelInfo;
import com.google.reviewit.R;
import com.google.reviewit.app.ApprovalData;
import com.google.reviewit.app.Change;
import com.google.reviewit.app.ReviewItApp;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.WidgetUtil;

import java.util.Map;

import static com.google.reviewit.util.LayoutUtil.fixedLinearLayout;

public class ApprovalEntry extends LinearLayout {
  private final WidgetUtil widgetUtil;

  public ApprovalEntry(Context context) {
    this(context, null, 0);
  }

  public ApprovalEntry(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ApprovalEntry(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    widgetUtil = new WidgetUtil(context);
    inflate(context, R.layout.approval_entry, this);
  }

  public void init(ReviewItApp app, Change change, AccountInfo account) {
    ((UserView) findViewById(R.id.user)).init(app, account);

    ApprovalData approvalData = change.getApprovalData();
    ViewGroup approvals = (ViewGroup) findViewById(R.id.approvals);

    for (Map.Entry<String, Map<Integer, ApprovalInfo>> e
        : approvalData.approvalsByLabel.entrySet()) {
      String labelName = e.getKey();
      LabelInfo label = approvalData.labels.get(labelName);
      View voteView =
          createVote(account, labelName, label, e.getValue(), approvalData);
      voteView.setPadding(
          widgetUtil.dpToPx(2), 0, widgetUtil.dpToPx(2), 0);
      approvals.addView(voteView);
    }
  }

  private View createVote(
      AccountInfo account,
      String labelName,
      LabelInfo label,
      Map<Integer, ApprovalInfo> approvalsByAccount,
      ApprovalData approvalData) {
    if (label.approved != null
        && label.approved._accountId.equals(account._accountId)) {
      return createMaxVote(labelName);
    } else if (label.rejected != null
        && label.rejected._accountId.equals(account._accountId)) {
      return createMinVote(labelName);
    } else {
      ApprovalInfo approval = approvalsByAccount.get(account._accountId);
      if (approval != null && approval.value != null) {
        if (approvalData.isMax(label, approval.value)) {
          return createMaxVote(labelName);
        } else if (approvalData.isMin(label, approval.value)) {
          return createMinVote(labelName);
        } else {
          return createNormalVote(labelName, approval.value);
        }
      } else {
        return createNormalVote(labelName, 0);
      }
    }
  }

  private View createNormalVote(String labelName, int value) {
    if ("Code-Review".equals(labelName)) {
      if (value == 1) {
        ImageView image = createImageView();
        image.setImageDrawable(
            widgetUtil.getDrawable(
                R.drawable.ic_sentiment_satisfied_black_18dp));
        image.setColorFilter(widgetUtil.color(R.color.votingPositive));
        return image;
      } else if (value == -1) {
        ImageView image = createImageView();
        image.setImageDrawable(
            widgetUtil.getDrawable(
                R.drawable.ic_sentiment_dissatisfied_black_18dp));
        image.setColorFilter(widgetUtil.color(R.color.votingNegative));
        return image;
      }
    }
    TextView text = widgetUtil.createTextView(
        FormatUtil.formatLabelValue(value), 18);
    text.setGravity(Gravity.CENTER_HORIZONTAL);
    text.setLayoutParams(
        fixedLinearLayout(widgetUtil.dpToPx(40), widgetUtil.dpToPx(25)));
    if (value > 0) {
      text.setTextColor(widgetUtil.color(R.color.votePositive));
    } else if (value < 0) {
      text.setTextColor(widgetUtil.color(R.color.voteNegative));
    } else {
      text.setText("");
    }
    return text;
  }

  private ImageView createMaxVote(String labelName) {
    ImageView image = createImageView();
    if ("Code-Review".equals(labelName)) {
      image.setImageDrawable(
          widgetUtil.getDrawable(
              R.drawable.ic_sentiment_very_satisfied_black_18dp));
      image.setColorFilter(widgetUtil.color(R.color.votingPositiveSelected));
    } else {
      image.setImageDrawable(
          widgetUtil.getDrawable(R.drawable.ic_done_black_18dp));
      image.setColorFilter(widgetUtil.color(R.color.votePositive));
    }
    return image;
  }

  private ImageView createMinVote(String labelName) {
    ImageView image = createImageView();
    if ("Code-Review".equals(labelName)) {
      image.setImageDrawable(
          widgetUtil.getDrawable(
              R.drawable.ic_sentiment_very_dissatisfied_black_18dp));
      image.setColorFilter(widgetUtil.color(R.color.votingNegativeSelected));
    } else {
      image.setImageDrawable(
          widgetUtil.getDrawable(R.drawable.ic_clear_black_18dp));
      image.setColorFilter(widgetUtil.color(R.color.voteNegative));
    }
    return image;
  }

  private ImageView createImageView() {
    ImageView imageView = new ImageView(getContext());
    imageView.setLayoutParams(
        fixedLinearLayout(widgetUtil.dpToPx(40), widgetUtil.dpToPx(25)));
    return imageView;
  }
}
