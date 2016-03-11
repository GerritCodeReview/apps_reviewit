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
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gerrit.extensions.common.FileInfo;
import com.google.reviewit.BaseFragment;
import com.google.reviewit.R;
import com.google.reviewit.UnifiedDiffFragment;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.FormatUtil;

import java.util.ArrayList;
import java.util.Map;

import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableRowLayout;

public class FileBox extends InfoBox<Map.Entry<String, FileInfo>> {
  public FileBox(Context context) {
    super(context);
  }

  public FileBox(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public FileBox(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected @StringRes int getTitle() {
    return R.string.files;
  }

  @Override
  protected @DrawableRes int getIcon() {
    return R.drawable.ic_feedback_black_18dp;
  }

  @Override
  protected void onAction(BaseFragment fragment) {

    fragment.display(UnifiedDiffFragment.class);
  }

  public void display(BaseFragment fragment, Change change) {
    display(fragment,
        new ArrayList<>(change.currentRevision().files.entrySet()));
  }

  @Override
  protected void addRow(TableLayout tl, Map.Entry<String, FileInfo> entry) {
    final String path = entry.getKey();
    FileInfo file = entry.getValue();

    TableRow tr = new TableRow(getContext());
    tr.setLayoutParams(matchAndWrapTableRowLayout());

    tr.addView(widgetUtil.tableRowRightMargin(widgetUtil.createTextView(
        file.status != null ? Character.toString(file.status) : "M", 11), 4));

    TextView pathText = widgetUtil.createTextView(path, 11);
    pathText.setTextColor(widgetUtil.color(R.color.hyperlink));
    pathText.setPaintFlags(
        pathText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    pathText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        fragment.display(UnifiedDiffFragment.create(path));
      }
    });
    tr.addView(widgetUtil.tableRowRightMargin(pathText, 4));

    tr.addView(widgetUtil.createTextView(
        FormatUtil.formatBytes(file.size), 11));

    // TODO show further file infos

    tl.addView(tr, matchAndWrapTableLayout());
  }
}
