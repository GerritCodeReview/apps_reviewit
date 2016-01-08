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

package com.google.reviewit.util;

import android.view.View;

import com.google.gerrit.extensions.client.ChangeStatus;
import com.google.reviewit.R;
import com.google.reviewit.app.Change;

public class ChangeUtil {
  /**
   * Set the background color depending on the Verified votes and the
   * change state.
   */
  public static void colorBackground(View root, Change change) {
    WidgetUtil widgetUtil = new WidgetUtil(root.getContext());
    Change.Voters verifiedVoters = change.voters("Verified");
    if (ChangeStatus.ABANDONED.equals(change.info.status)) {
      widgetUtil.setBackgroundColor(root, R.color.abandoned);
    } else if (ChangeStatus.MERGED.equals(change.info.status)) {
      widgetUtil.setBackgroundColor(root, R.color.merged);
    } else if (verifiedVoters.dislikers.size() > 0) {
      widgetUtil.setBackgroundColor(root, R.color.verifiedMinusOne);
    } else if (change.info.mergeable != null && !change.info.mergeable) {
      widgetUtil.setBackgroundColor(root, R.color.nonMergable);
    } else if (verifiedVoters.likers.size() > 0) {
      widgetUtil.setBackgroundColor(root, R.color.verifiedPlusOne);
    } else {
      widgetUtil.setBackgroundColor(root, R.color.noState);
    }
  }
}
