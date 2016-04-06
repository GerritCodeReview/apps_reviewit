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
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gerrit.extensions.common.FileInfo;
import com.google.reviewit.BaseFragment;
import com.google.reviewit.R;
import com.google.reviewit.UnifiedDiffFragment;
import com.google.reviewit.util.FormatUtil;

public class FileEntry extends LinearLayout {
  public FileEntry(Context context) {
    this(context, null, 0);
  }

  public FileEntry(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FileEntry(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);

    inflate(context, R.layout.file_entry, this);
  }

  public void init(
      final BaseFragment fragment, final String path, FileInfo file) {
    TextView pathText = ((TextView) findViewById(R.id.path));
    pathText.setText(path);
    pathText.setPaintFlags(
        pathText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    pathText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        fragment.display(UnifiedDiffFragment.create(path));
      }
    });

    ((TextView)findViewById(R.id.fileStatus))
        .setText(file.status != null ? Character.toString(file.status) : "M");

    ((TextView)findViewById(R.id.fileSize))
        .setText(FormatUtil.formatBytes(file.size));
  }
}
