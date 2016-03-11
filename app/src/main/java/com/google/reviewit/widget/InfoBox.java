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
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.reviewit.BaseFragment;
import com.google.reviewit.R;
import com.google.reviewit.util.WidgetUtil;

import java.util.List;

import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setVisible;

public abstract class InfoBox<T> extends RelativeLayout {
  private static final int PAGE_SIZE = 10;

  protected final WidgetUtil widgetUtil;
  protected BaseFragment fragment;

  public InfoBox(Context context) {
    this(context, null, 0);
  }

  public InfoBox(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public InfoBox(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    this.widgetUtil = new WidgetUtil(context);

    inflate(context, R.layout.info_box, this);

    init();
  }

  private void init() {
    ((TextView)findViewById(R.id.info_title)).setText(getTitle());

    if (getIcon() > 0) {
      ImageView action = (ImageView)findViewById(R.id.info_action);
      action.setImageDrawable(widgetUtil.getDrawable(getIcon()));
      setVisible(action);
      action.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onAction(fragment);
            }
          });
    }

    WidgetUtil.underline((TextView) findViewById(R.id.show_more));
    WidgetUtil.underline((TextView) findViewById(R.id.show_all));
  }

  protected abstract @StringRes int getTitle();

  protected @DrawableRes int getIcon() {
    return -1;
  }

  protected void onAction(BaseFragment fragment) {
  }

  protected void display(BaseFragment fragment, List<T> entries) {
    this.fragment = fragment;
    displayFiles(entries, 1, false);
  }

  private void displayFiles(
      final List<T> entries, final int page, boolean showAll) {
    TableLayout tl = (TableLayout) findViewById(R.id.info_table);
    int count = 0;
    for (T e : entries) {
      count++;
      if (count <= (page - 1) * PAGE_SIZE) {
        continue;
      }
      if (!showAll && count > page * PAGE_SIZE) {
        break;
      }
      addRow(tl, e);
    }
    if (!showAll && entries.size() > page * PAGE_SIZE) {
      WidgetUtil.setText(findViewById(R.id.show_all),
          fragment.getString(R.string.show_all, entries.size() - page *
              PAGE_SIZE));
      setVisible(findViewById(R.id.info_buttons));
      findViewById(R.id.show_all).setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              displayFiles(entries, page + 1, true);
          }
        });
      if (entries.size() > (page + 1) * PAGE_SIZE) {
        WidgetUtil.setText(findViewById(R.id.show_more),
            fragment.getString(R.string.show_more, PAGE_SIZE));
        setVisible(findViewById(R.id.show_more_area),
            findViewById(R.id.show_more));
        findViewById(R.id.show_more).setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                displayFiles(entries, page + 1, false);
              }
          });
      } else {
        setGone(findViewById(R.id.show_more_area),
            findViewById(R.id.show_more));
      }
    } else {
      setGone(findViewById(R.id.info_buttons));
    }
  }

  protected abstract void addRow(TableLayout tl, T entry);
}
