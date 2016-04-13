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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.common.ApprovalInfo;
import com.google.gerrit.extensions.common.LabelInfo;
import com.google.reviewit.AddReviewerFragment;
import com.google.reviewit.AvatarTask;
import com.google.reviewit.BaseFragment;
import com.google.reviewit.R;
import com.google.reviewit.app.ApprovalData;
import com.google.reviewit.app.Change;
import com.google.reviewit.app.ReviewItApp;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.WidgetUtil;

import java.util.Map;

import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableRowLayout;
import static com.google.reviewit.util.LayoutUtil.wrapTableRowLayout;

public class ApprovalsView extends TableLayout {
  private final WidgetUtil widgetUtil;

  public ApprovalsView(Context context) {
    this(context, null);
  }

  public ApprovalsView(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.widgetUtil = new WidgetUtil(context);

    setColumnStretchable(1, true);
    setColumnShrinkable(1, true);
    WidgetUtil.setBackground(this,
        widgetUtil.getDrawable(R.drawable.info_table));
  }

  public void displayApprovals(
      ReviewItApp app, Change change, final BaseFragment origin) {
    TableRow headerRow = new TableRow(getContext());
    headerRow.setLayoutParams(matchAndWrapTableRowLayout());
    LinearLayout l = new LinearLayout(getContext());
    l.setLayoutParams(wrapTableRowLayout(2));
    ImageView addReviewerButton = new ImageView(getContext());
    ViewGroup.MarginLayoutParams params =
        new ViewGroup.MarginLayoutParams(
            widgetUtil.dpToPx(24), widgetUtil.dpToPx(24));
    params.bottomMargin = widgetUtil.dpToPx(3);
    addReviewerButton.setLayoutParams(params);
    addReviewerButton.setClickable(true);
    addReviewerButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        AddReviewerFragment f = AddReviewerFragment.create(origin.getClass());
        if (origin.getArguments() != null) {
          f.getArguments().putAll(origin.getArguments());
        }
        origin.display(f, false);
      }
    });

    WidgetUtil.setBackground(addReviewerButton,
        widgetUtil.getDrawable(R.drawable.ic_person_add_black_18dp));
    l.addView(addReviewerButton);
    l.addView(WidgetUtil.setBottomMargin(
        widgetUtil.createTextView(
            origin.getString(R.string.approvals), 20), widgetUtil.dpToPx(5)));
    headerRow.addView(l);
    addView(headerRow, matchAndWrapTableLayout());


    ApprovalData approvalData = change.getApprovalData();
    for (String labelName : approvalData.labels.keySet()) {
      TextView labelNameText = widgetUtil.createTextView(
          FormatUtil.formatLabelName(labelName), 16);
      labelNameText.setPadding(
          widgetUtil.dpToPx(2), 0, widgetUtil.dpToPx(2), 0);
      headerRow.addView(center(labelNameText));
    }

    for (AccountInfo account : approvalData.reviewers) {
      addApprovalRow(app, account, approvalData);
    }
  }

  public void clear() {
    removeAllViews();
  }

  private void addApprovalRow(
      ReviewItApp app, AccountInfo account, ApprovalData approvalData) {
    TableRow tr = new TableRow(getContext());
    tr.setLayoutParams(matchAndWrapTableRowLayout());
    int textSizeSp = 14;

    tr.addView(bottomMargin(widgetUtil.tableRowRightMargin(
        createAvatar(app, account), 4), 5));
    tr.addView(bottomMargin(widgetUtil.tableRowRightMargin(
        widgetUtil.createTextView(
            FormatUtil.format(account), textSizeSp), 4), 5));

    for (Map.Entry<String, Map<Integer, ApprovalInfo>> e
        : approvalData.approvalsByLabel.entrySet()) {
      String labelName = e.getKey();
      LabelInfo label = approvalData.labels.get(labelName);

      if (label.approved != null
          && label.approved._accountId.equals(account._accountId)) {
        tr.addView(createPositiveVoteIcon());
      } else if (label.rejected != null
          && label.rejected._accountId.equals(account._accountId)) {
        tr.addView(createNegativeVoteIcon());
      } else {
        ApprovalInfo approval = e.getValue().get(account._accountId);
        if (approval != null && approval.value != null) {
          if (approvalData.isMax(label, approval.value)) {
            tr.addView(createPositiveVoteIcon());
          } else if (approvalData.isMin(label, approval.value)) {
            tr.addView(createNegativeVoteIcon());
          } else {
            TextView text = widgetUtil.createTextView(
                FormatUtil.formatLabelValue(approval.value), textSizeSp);
            tr.addView(bottomMargin(center(text), 5));
            if (approval.value > 0) {
              text.setTextColor(widgetUtil.color(R.color.votePositive));
            } else if (approval.value < 0) {
              text.setTextColor(widgetUtil.color(R.color.voteNegative));
            } else {
              text.setText("0");
            }
          }
        } else {
          tr.addView(bottomMargin(center(
              widgetUtil.createTextView("0", textSizeSp)), 5));
        }
      }
    }
    addView(tr, matchAndWrapTableLayout());
  }

  private ImageView createAvatar(ReviewItApp app, AccountInfo account) {
    ImageView avatar = createImageView();
    TableRow.LayoutParams layoutParams =
        (TableRow.LayoutParams) avatar.getLayoutParams();
    layoutParams.width = widgetUtil.dpToPx(24);
    layoutParams.height = widgetUtil.dpToPx(24);
    avatar.setLayoutParams(layoutParams);
    new AvatarTask(app, avatar).executeOnExecutor(app.getExecutor(), account);
    return avatar;
  }

  private ImageView createPositiveVoteIcon() {
    ImageView image = createImageView();
    image.setImageDrawable(
        widgetUtil.getDrawable(R.drawable.ic_done_black_18dp));
    image.setColorFilter(widgetUtil.color(R.color.votePositive));
    center(image);
    bottomMargin(image, 5);
    return image;
  }

  private ImageView createNegativeVoteIcon() {
    ImageView image = createImageView();
    image.setImageDrawable(widgetUtil.getDrawable(
        R.drawable.ic_clear_black_18dp));
    image.setColorFilter(widgetUtil.color(R.color.voteNegative));
    center(image);
    bottomMargin(image, 5);
    return image;
  }

  private View bottomMargin(View view, int bottomMarginDp) {
    TableRow.LayoutParams layoutParams =
        (TableRow.LayoutParams) view.getLayoutParams();
    layoutParams.bottomMargin = widgetUtil.dpToPx(bottomMarginDp);
    view.setLayoutParams(layoutParams);
    return view;
  }

  private ImageView createImageView() {
    ImageView imageView = new ImageView(getContext());
    imageView.setLayoutParams(wrapTableRowLayout());
    return imageView;
  }

  private View center(View view) {
    TableRow.LayoutParams layoutParams =
        (TableRow.LayoutParams) view.getLayoutParams();
    layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
    view.setLayoutParams(layoutParams);
    return view;
  }
}
