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

/**
 * User preferences.
 */
public class Preferences {
  /**
   * Whether the app introduction should be shown on app startup.
   */
  public final boolean showIntro;

  /**
   * Whether the background should be colored to indicate Verified votings
   * and change states.
   */
  public final boolean colorBackground;

  /**
   * Whether the number of patch sets should be shown.
   */
  public final boolean showPatchSets;

  /**
   * Whether the number of positive Code-Review votes should be shown.
   */
  public final boolean showPositiveCodeReviewVotes;

  /**
   * Whether the number of negative Code-Review votes should be shown.
   */
  public final boolean showNegativeCodeReviewVotes;

  /**
   * Whether the number of inline comments should be shown.
   */
  public final boolean showComments;

  /**
   * Whether the number of reviewers should be shown.
   */
  public final boolean showReviewers;

  /**
   * Font size for the commit message.
   * <code>-1</code> means that the font size should be as large as
   * possible without breaking lines.
   */
  public final int commitMessageFontSize;

  private Preferences(
      boolean showIntro, boolean colorBackground, boolean showPatchSets,
      boolean showPositiveCodeReviewVotes, boolean showNegativeCodeReviewVotes,
      boolean showComments, boolean showReviewers, int commitMessageFontSize) {
    this.showIntro = showIntro;
    this.colorBackground = colorBackground;
    this.showPatchSets = showPatchSets;
    this.showPositiveCodeReviewVotes = showPositiveCodeReviewVotes;
    this.showNegativeCodeReviewVotes = showNegativeCodeReviewVotes;
    this.showComments = showComments;
    this.showReviewers = showReviewers;
    this.commitMessageFontSize = commitMessageFontSize;
  }

  public boolean autoFontSizeForCommitMessage() {
    return Integer.valueOf(commitMessageFontSize).equals(-1);
  }

  public static class Builder {
    private boolean showIntro;
    private boolean colorBackground;
    private boolean showPatchSets;
    private boolean showPositiveCodeReviewVotes;
    private boolean showNegativeCodeReviewVotes;
    private boolean showComments;
    private boolean showReviewers;
    private int commitMessageFontSize;

    public Builder() {
    }

    public Builder(Preferences prefs) {
      this.showIntro = prefs.showIntro;
      this.colorBackground = prefs.colorBackground;
      this.showPatchSets = prefs.showPatchSets;
      this.showPositiveCodeReviewVotes = prefs.showPositiveCodeReviewVotes;
      this.showNegativeCodeReviewVotes = prefs.showNegativeCodeReviewVotes;
      this.showComments = prefs.showComments;
      this.showReviewers = prefs.showReviewers;
      this.commitMessageFontSize = prefs.commitMessageFontSize;
    }

    public Builder setShowIntro(boolean showIntro) {
      this.showIntro = showIntro;
      return this;
    }

    public Builder setColorBackground(boolean colorBackground) {
      this.colorBackground = colorBackground;
      return this;
    }

    public Builder setShowPatchSets(boolean showPatchSets) {
      this.showPatchSets = showPatchSets;
      return this;
    }

    public Builder setShowPositiveCodeReviewVotes(
        boolean showPositiveCodeReviewVotes) {
      this.showPositiveCodeReviewVotes = showPositiveCodeReviewVotes;
      return this;
    }

    public Builder setShowNegativeCodeReviewVotes(
        boolean showNegativeCodeReviewVotes) {
      this.showNegativeCodeReviewVotes = showNegativeCodeReviewVotes;
      return this;
    }

    public Builder setShowComments(boolean showComments) {
      this.showComments = showComments;
      return this;
    }

    public Builder setShowReviewers(boolean showReviewers) {
      this.showReviewers = showReviewers;
      return this;
    }

    public Builder setCommitMessageFontSize(int commitMessageFontSize) {
      this.commitMessageFontSize = commitMessageFontSize;
      return this;
    }

    public Preferences build() {
      return new Preferences(showIntro, colorBackground, showPatchSets,
          showPositiveCodeReviewVotes, showNegativeCodeReviewVotes,
          showComments, showReviewers, commitMessageFontSize);
    }
  }
}
