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

import android.text.util.Linkify;
import android.widget.TextView;

import com.google.reviewit.R;
import com.google.reviewit.app.ReviewItApp;

import java.util.regex.Pattern;

public class Linkifier {
  private static final Pattern PATTERN_CHANGE_ID =
      Pattern.compile("I[0-9a-f]{5,40}");
  private static final String PART_LINK = "(?:"
      + "[a-zA-Z0-9$_+!*'%;:@=?#/~-]"
      + "|&(?!lt;|gt;)"
      + "|[.,](?!(?:\\s|$))"
      + ")";
  private static final Pattern PATTERN_LINK = Pattern.compile(
      "https?://"
          + PART_LINK + "{2,}"
          + "(?:[(]" + PART_LINK + "*" + "[)])*"
          + PART_LINK + "*");
  private static final Pattern PATTERN_EMAIL =
      Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
          Pattern.CASE_INSENSITIVE);

  private final ReviewItApp app;

  public Linkifier(ReviewItApp app) {
    this.app = app;
  }

  public void linkifyCommitMessage(TextView commitMsg) {
    commitMsg.setLinksClickable(true);
    Linkify.addLinks(commitMsg, PATTERN_CHANGE_ID, getServerUrl() + "#/q/");
    Linkify.addLinks(commitMsg, PATTERN_LINK, "");
    Linkify.addLinks(commitMsg, PATTERN_EMAIL, "");
  }

  private String getServerUrl() {
    String serverUrl = app.getConfigManager().getServerConfig().url;
    return FormatUtil.ensureSlash(serverUrl);
  }
}
