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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.reviewit.R;
import com.google.reviewit.app.ReviewItApp;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.WidgetUtil;

public class UserView extends LinearLayout {
  public UserView(Context context) {
    this(context, null, 0);
  }

  public UserView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public UserView(
      Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    inflate(context, R.layout.user, this);

    WidgetUtil widgetUtil = new WidgetUtil(context);
    TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
        R.styleable.UserView, 0, 0);
    int avatarSize = (int) widgetUtil.toDimension(
        a.getString(R.styleable.UserView_avatarSize), -1);
    if (avatarSize > 0) {
      findViewById(R.id.avatar).setLayoutParams(new LayoutParams(avatarSize,
          avatarSize));
    }

    float textSize = widgetUtil.toDimension(
        a.getString(R.styleable.UserView_textSize), -1);
    if (textSize > 0) {
      ((TextView)findViewById(R.id.userName)).setTextSize(
          TypedValue.COMPLEX_UNIT_PX, textSize);
    }
  }

  public void init(ReviewItApp app, AccountInfo account) {
    WidgetUtil.displayAvatar(app, account,
        (ImageView) findViewById(R.id.avatar));
    WidgetUtil.setText(findViewById(R.id.userName), FormatUtil.format(account));
  }
}
