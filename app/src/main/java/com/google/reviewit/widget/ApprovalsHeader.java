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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.reviewit.R;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.WidgetUtil;

import static com.google.reviewit.util.LayoutUtil.fixedLinearLayout;

public class ApprovalsHeader extends LinearLayout {
  private final WidgetUtil widgetUtil;

  public ApprovalsHeader(Context context) {
    this(context, null, 0);
  }

  public ApprovalsHeader(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ApprovalsHeader(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    widgetUtil = new WidgetUtil(context);
    inflate(context, R.layout.approvals_header, this);
  }

  public void init(Change change) {
    for (String labelName : change.getApprovalData().labels.keySet()) {
      TextView text = widgetUtil.createTextView(
          FormatUtil.formatLabelName(labelName), 18);
      text.setTypeface(null, Typeface.BOLD);
      text.setGravity(Gravity.CENTER_HORIZONTAL);
      text.setLayoutParams(
          fixedLinearLayout(widgetUtil.dpToPx(40), widgetUtil.dpToPx(25)));
      text.setPadding(
          widgetUtil.dpToPx(2), 0, widgetUtil.dpToPx(2), 0);
      ((ViewGroup) findViewById(R.id.labels)).addView(text);
    }
  }
}
