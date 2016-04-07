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

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
  private static final String KEY_SHOW_INTRO = "SHOW_INTRO";
  private static final String KEY_START_SCREEN = "START_SCREEN";
  private static final String KEY_COLOR_BACKGROUND = "COLOR_BACKGROUND";
  private final static String KEY_SHOW_PATCH_SETS = "SHOW_PATCH_SETS";
  private final static String KEY_SHOW_POSITIVE_CODE_REVIEW_VOTES =
      "SHOW_POSITIVE_CODE_REVIEW_VOTES";
  private final static String KEY_SHOW_NEGATIVE_CODE_REVIEW_VOTES =
      "SHOW_NEGATIVE_CODE_REVIEW_VOTES";
  private final static String KEY_SHOW_COMMENTS = "SHOW_COMMENTS";
  private final static String KEY_SHOW_REVIEWERS = "SHOW_REVIEWERS";
  private final static String KEY_COMMIT_MESSAGE_FONT_SIZE =
      "COMMIT_MESSAGE_FONT_SIZE";

  private final SharedPreferences prefs;

  /**
   * Cached preferences.
   */
  private Preferences preferences;

  PreferenceManager(Context context) {
    this.prefs = android.preference.PreferenceManager
        .getDefaultSharedPreferences(context);
  }

  public Preferences getPreferences() {
    if (preferences == null) {
      preferences = new Preferences.Builder()
          .setShowIntro(prefs.getBoolean(KEY_SHOW_INTRO, true))
          .setStartScreen(Preferences.StartScreen.fromString(
              prefs.getString(KEY_START_SCREEN, null)))
          .setColorBackground(prefs.getBoolean(KEY_COLOR_BACKGROUND, true))
          .setShowPatchSets(prefs.getBoolean(KEY_SHOW_PATCH_SETS, true))
          .setShowPositiveCodeReviewVotes(
              prefs.getBoolean(KEY_SHOW_POSITIVE_CODE_REVIEW_VOTES, true))
          .setShowNegativeCodeReviewVotes(
              prefs.getBoolean(KEY_SHOW_NEGATIVE_CODE_REVIEW_VOTES, true))
          .setShowComments(prefs.getBoolean(KEY_SHOW_COMMENTS, true))
          .setShowReviewers(prefs.getBoolean(KEY_SHOW_REVIEWERS, true))
          .setCommitMessageFontSize(
              prefs.getInt(KEY_COMMIT_MESSAGE_FONT_SIZE, -1))
          .build();
    }
    return preferences;
  }

  public void setPreferences(Preferences preferences) {
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(KEY_SHOW_INTRO, preferences.showIntro);
    editor.putString(KEY_START_SCREEN,
        preferences.startScreen != null
            ? preferences.startScreen.name()
            : null);
    editor.putBoolean(KEY_COLOR_BACKGROUND, preferences.colorBackground);
    editor.putBoolean(KEY_SHOW_PATCH_SETS, preferences.showPatchSets);
    editor.putBoolean(KEY_SHOW_POSITIVE_CODE_REVIEW_VOTES,
        preferences.showPositiveCodeReviewVotes);
    editor.putBoolean(KEY_SHOW_NEGATIVE_CODE_REVIEW_VOTES,
        preferences.showNegativeCodeReviewVotes);
    editor.putBoolean(KEY_SHOW_COMMENTS, preferences.showComments);
    editor.putBoolean(KEY_SHOW_REVIEWERS, preferences.showReviewers);
    editor.putInt(KEY_COMMIT_MESSAGE_FONT_SIZE,
        preferences.commitMessageFontSize);
    editor.apply();

    this.preferences = preferences;
  }
}
