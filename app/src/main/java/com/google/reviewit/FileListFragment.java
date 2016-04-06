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
import android.view.View;
import android.view.ViewGroup;

import com.google.gerrit.extensions.common.FileInfo;
import com.google.reviewit.app.Change;
import com.google.reviewit.widget.FileEntry;

import java.util.Map;

import static com.google.reviewit.util.LayoutUtil.matchAndFixedLayout;

public class FileListFragment extends PageFragment {
  private Change change;

  @Override
  protected int getLayout() {
    return R.layout.content_file_list;
  }

  @Override
  public @StringRes int getTitle() {
    return R.string.file_list_title;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    ViewGroup fileList = vg(R.id.fileList);
    for (Map.Entry<String, FileInfo> entry :
        change.currentRevision().files.entrySet()) {
      FileEntry fileEntry = new FileEntry(getContext());
      fileEntry.init((BaseFragment)getParentFragment(), entry.getKey(),
          entry.getValue());
      fileList.addView(fileEntry);
      addSeparator(fileList);
    }
  }

  public void setChange(Change change) {
    this.change = change;
  }

  private void addSeparator(ViewGroup viewGroup) {
    View separator = new View(getContext());
    separator.setLayoutParams(
        matchAndFixedLayout(widgetUtil.dpToPx(1)));
    separator.setBackgroundColor(widgetUtil.color(R.color.separator));
    viewGroup.addView(separator);
  }
}
