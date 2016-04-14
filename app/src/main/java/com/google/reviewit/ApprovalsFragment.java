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
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.reviewit.app.Change;
import com.google.reviewit.widget.ApprovalEntry;
import com.google.reviewit.widget.ApprovalsHeader;

import static com.google.reviewit.util.LayoutUtil.matchAndFixedLayout;

public class ApprovalsFragment extends PageFragment {
  private Change change;

  @Override
  protected int getLayout() {
    return R.layout.content_approvals;
  }

  @Override
  public @StringRes int getTitle() {
    return R.string.approvals_title;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    ViewGroup approvalList = vg(R.id.approvalList);
    ApprovalsHeader approvalsHeader = new ApprovalsHeader(getContext());
    approvalsHeader.init(change);
    approvalList.addView(approvalsHeader);
    addSeparator(approvalList);
    for (AccountInfo account : change.getApprovalData().reviewers) {
      ApprovalEntry approvalEntry = new ApprovalEntry(getContext());
      approvalEntry.init(getApp(), change, account);
      approvalList.addView(approvalEntry);
      addSeparator(approvalList);
    }
  }

  public void setChange(Change change) {
    this.change = change;
  }

  private void addSeparator(ViewGroup viewGroup) {
    View separator = new View(getContext());
    separator.setLayoutParams(
        matchAndFixedLayout(widgetUtil.dpToPx(1)));
    separator.setBackgroundColor(widgetUtil.color(R.color.separator));
    viewGroup.addView(separator);
  }
}
