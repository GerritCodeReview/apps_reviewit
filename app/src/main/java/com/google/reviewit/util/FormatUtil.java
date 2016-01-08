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

import android.content.Context;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.reviewit.app.Change;

import java.text.DecimalFormat;
import java.util.Date;

public class FormatUtil {
  public static String formatBytes(long bytes) {
    return formatBytes(bytes, false);
  }

  private static String formatBytes(long bytes, boolean abs) {
    bytes = abs ? Math.abs(bytes) : bytes;

    if (bytes == 0) {
      return abs ? "0 B" : "+/- 0 B";
    }

    if (Math.abs(bytes) < 1024) {
      return (bytes > 0 && !abs ? "+" : "") + bytes + " B";
    }

    int exp = (int) (Math.log(Math.abs(bytes)) / Math.log(1024));
    return (bytes > 0 && !abs ? "+" : "")
        + new DecimalFormat("#.0").format(bytes / Math.pow(1024, exp))
        + " KMGTPE".charAt(exp - 1) + "iB";
  }

  public static String ensureSlash(String in) {
    if (in != null && !in.endsWith("/")) {
      return in + "/";
    }
    return in;
  }

  public static String formatLabelName(String labelName) {
    StringBuilder abbrev = new StringBuilder();
    if (labelName != null) {
      for (String t : labelName.split("-")) {
        abbrev.append(t.substring(0, 1).toUpperCase());
      }
    }
    return abbrev.toString();
  }

  public static String formatLabelValue(int value) {
    if (value < 0) {
      return Integer.toString(value);
    } else if (value == 0) {
      return "\u00B10";
    } else {
      return "+" + Integer.toString(value);
    }
  }

  public static String format(AccountInfo account) {
    if (account.name != null) {
      return account.name;
    } else if (account.email != null) {
      return account.email;
    } else if (account.username != null) {
      return account.username;
    } else {
      return format(account._accountId);
    }
  }

  public static String format(int i) {
    return Integer.toString(i);
  }

  public static String formatMessage(Change change) {
    String message = change.currentRevision().commit.message;
    message = message.substring(change.info.subject.length());
    return message.trim();
  }

  public static String formatDate(Context context, Date date) {
    return (new RelativeDateFormatter(context)).format(date);
  }
}
