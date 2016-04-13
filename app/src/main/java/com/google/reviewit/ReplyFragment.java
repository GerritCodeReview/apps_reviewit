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

import com.google.gerrit.extensions.common.ApprovalInfo;
import com.google.reviewit.app.Change;
import com.google.reviewit.widget.PostReviewView;

import java.util.List;

public class ReplyFragment extends PageFragment {
  private Change change;

  @Override
  protected int getLayout() {
    return R.layout.content_reply;
  }

  @Override
  public @StringRes
  int getTitle() {
    return R.string.reply_title;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    List<ApprovalInfo> codeReviewApprovals =
        (change.info.labels != null
            && change.info.labels.get("Code-Review") != null)
            ? change.info.labels.get("Code-Review").all : null;
    int vote = 0;
    if (codeReviewApprovals != null) {
      for (ApprovalInfo approval : codeReviewApprovals) {
        if (approval._accountId.equals(getApp().getSelf()._accountId)) {
          vote = approval.value;
          break;
        }
      }
    }

    ((PostReviewView) v(R.id.postReview)).initForDisplayInTab(
        getApp(), this, vote, PagedChangeDetailsFragment.class);
  }

  public void setChange(Change change) {
    this.change = change;
  }
}
