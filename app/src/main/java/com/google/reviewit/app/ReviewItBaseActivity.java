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

package com.google.reviewit.app;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.reviewit.R;
import com.google.reviewit.util.WidgetUtil;

/**
 * Base for all activities.
 */
public class ReviewItBaseActivity extends AppCompatActivity {
  protected WidgetUtil widgetUtil;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setBackgroundDrawable(getDrawable(R.drawable.app_screen));
    widgetUtil = new WidgetUtil(this);
  }

  protected ReviewItApp getApp() {
    return ((ReviewItApp) getApplication());
  }

  protected int color(@ColorRes int id) {
    // TODO use getResources().getColor(id, null) with API 23
    return getResources().getColor(id);
  }

  protected int dpToPx(int dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        getResources().getDisplayMetrics());
  }

  protected int spToPx(int sp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
        getResources().getDisplayMetrics());
  }

  protected String textOf(@IdRes int id) {
    View view = findViewById(id);
    if (view instanceof TextView) {
      return ((TextView) view).getText().toString();
    } else {
      throw new IllegalStateException("unexpected view type "
          + view.getClass().getName() + " for id " + id);
    }
  }

  protected void setOnClickListener(
      @IdRes int id, View.OnClickListener listener) {
    findViewById(id).setOnClickListener(listener);
  }

  protected ViewGroup findViewGroupById(int id) {
    return (ViewGroup) findViewById(id);
  }

  protected void setGone(@IdRes int... ids) {
    for (int id : ids) {
      findViewById(id).setVisibility(View.GONE);
    }
  }

  protected void setVisible(@IdRes int... ids) {
    for (int id : ids) {
      findViewById(id).setVisibility(View.VISIBLE);
    }
  }

  protected void setInvisible(@IdRes int... ids) {
    for (int id : ids) {
      findViewById(id).setVisibility(View.INVISIBLE);
    }
  }

  protected String createLink(String url, String text) {
    return "<a href=\"" + url + "\">" + text + "</a>";
  }
}
