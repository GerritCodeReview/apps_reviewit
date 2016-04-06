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
import android.widget.TextView;

import com.google.reviewit.app.Change;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.Linkifier;

public class CommitMessageFragment extends PageFragment {
  private Change change;

  @Override
  protected int getLayout() {
    return R.layout.content_commit_message;
  }

  @Override
  public @StringRes
  int getTitle() {
    return R.string.commit_message_title;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    tv(R.id.subject).setText(change.currentRevision().commit.subject);

    TextView commitMsg = tv(R.id.commitMessage);
    commitMsg.setText(FormatUtil.formatMessage(change));
    (new Linkifier(getApp())).linkifyCommitMessage(commitMsg);
  }

  public void setChange(Change change) {
    this.change = change;
  }
}
