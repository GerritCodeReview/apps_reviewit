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
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.reviewit.app.PreferenceManager;
import com.google.reviewit.app.Preferences;
import com.google.reviewit.util.WidgetUtil;

/**
 * Fragment to show the app preferences.
 */
public class PreferencesFragment extends BaseFragment {
  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_preferences;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    init();
    displayPrefs(getApp().getPrefs());
  }

  private void init() {
    ((CheckBox) v(R.id.autoFontSize)).setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(
          CompoundButton buttonView, boolean isChecked) {
        onAutoFontChecked(isChecked);
      }
    });

    v(R.id.savePreferences).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        savePreferences();
      }
    });
  }

  private void onAutoFontChecked(boolean isChecked) {
    EditText fontSizeInput = ((EditText) v(R.id.fontSizeInput));
    fontSizeInput.setEnabled(!isChecked);
    TextView fontSize = tv(R.id.fontSize);
    if (isChecked) {
      fontSizeInput.setInputType(InputType.TYPE_NULL);
      fontSize.setTextColor(widgetUtil.color(R.color.text_disabeled));
    } else {
      fontSizeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
      fontSize.setTextColor(widgetUtil.color(R.color.text));
    }
  }

  private void savePreferences() {
    PreferenceManager prefManager = getApp().getPrefManager();
    prefManager.setPreferences(new Preferences.Builder()
        .setShowIntro(prefManager.getPreferences().showIntro)
        .setColorBackground(isChecked(R.id.colorBackgroundPref))
        .setShowPatchSets(isChecked(R.id.showPatchSetsPref))
        .setShowPositiveCodeReviewVotes(
            isChecked(R.id.showPositiveCodeReviewVotesPref))
        .setShowNegativeCodeReviewVotes(
            isChecked(R.id.showNegativeCodeReviewVotesPref))
        .setShowComments(isChecked(R.id.showCommentsPref))
        .setShowReviewers(isChecked(R.id.showReviewersPref))
        .setCommitMessageFontSize(isChecked(R.id.autoFontSize)
            ? -1
            : Integer.valueOf(textOf(R.id.fontSizeInput)))
        .build());
    display(SortChangesFragment.class);
  }

  private void displayPrefs(Preferences prefs) {
    setChecked(R.id.colorBackgroundPref, prefs.colorBackground);
    setChecked(R.id.showPatchSetsPref, prefs.showPatchSets);
    setChecked(R.id.showPositiveCodeReviewVotesPref,
        prefs.showPositiveCodeReviewVotes);
    setChecked(R.id.showNegativeCodeReviewVotesPref,
        prefs.showNegativeCodeReviewVotes);
    setChecked(R.id.showCommentsPref, prefs.showComments);
    setChecked(R.id.showReviewersPref, prefs.showReviewers);

    boolean autoFontSize = prefs.autoFontSizeForCommitMessage();
    WidgetUtil.setText(v(R.id.fontSizeInput),
        Integer.toString(autoFontSize
            ? widgetUtil.spToPx(11)
            : prefs.commitMessageFontSize));
    ((CheckBox) v(R.id.autoFontSize)).setChecked(autoFontSize);
    onAutoFontChecked(autoFontSize);
  }

  private void setChecked(@IdRes int id, boolean checked) {
    ((CheckBox) v(id)).setChecked(checked);
  }

  private boolean isChecked(@IdRes int id) {
    return ((CheckBox) v(id)).isChecked();
  }
}
