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
import android.view.View;
import android.widget.LinearLayout;

import com.google.reviewit.R;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.WidgetUtil;

import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setVisible;

public class ExpandableCommitMessageView extends LinearLayout {
  public ExpandableCommitMessageView(Context context) {
    this(context, null, 0);
  }

  public ExpandableCommitMessageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ExpandableCommitMessageView(
      Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    inflate(context, R.layout.expandable_commit_message, this);
  }

  public void init(final Change change) {
    WidgetUtil.setText(findViewById(R.id.subject), change.info.subject);

    findViewById(R.id.expandCommitMessage).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            setGone(findViewById(R.id.expandCommitMessage));
            setVisible(findViewById(R.id.collapseCommitMessage));
            WidgetUtil.setText(findViewById(R.id.subject),
                change.currentRevision().commit.message);
          }
    });

    findViewById(R.id.collapseCommitMessage).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            setGone(findViewById(R.id.collapseCommitMessage));
            setVisible(findViewById(R.id.expandCommitMessage));
            WidgetUtil.setText(findViewById(R.id.subject), change.info.subject);
          }
        });
  }
}
