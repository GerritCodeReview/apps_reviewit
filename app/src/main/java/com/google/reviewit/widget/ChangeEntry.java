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
import android.widget.LinearLayout;

import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.reviewit.R;
import com.google.reviewit.app.Change;
import com.google.reviewit.app.ReviewItApp;
import com.google.reviewit.util.WidgetUtil;

public class ChangeEntry extends LinearLayout {
  public ChangeEntry(Context context) {
    this(context, null, 0);
  }

  public ChangeEntry(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ChangeEntry(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    inflate(context, R.layout.change_entry, this);
  }

  public void init(ReviewItApp app, Change change) {
    ((ProjectBranchTopicAgeView)findViewById(R.id.projectBranchTopicAge))
        .init(change);
    ((UserView)findViewById(R.id.owner)).init(app, change.info.owner);

    ChangeInfo info = change.info;
    WidgetUtil.setText(findViewById(R.id.subject), info.subject);

    ((CodeReviewVotes)findViewById(R.id.codeReviewVotes)).init(change);
  }
}
