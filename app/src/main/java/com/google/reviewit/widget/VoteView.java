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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gerrit.extensions.common.LabelInfo;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.WidgetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static com.google.reviewit.util.LayoutUtil.matchAndWrapLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableRowLayout;
import static com.google.reviewit.util.LayoutUtil.wrapTableRowLayout;

public class VoteView extends TableLayout {
  private List<OnSelectListener> listeners = new ArrayList<>();

  public VoteView(Context context) {
    this(context, null);
  }

  public VoteView(Context context, AttributeSet attrs) {
    super(context, attrs);

    setLayoutParams(matchAndWrapLayout());
    setColumnShrinkable(2, true);
  }

  public void init(final Change change, Map<String, Integer> votes) {
    final WidgetUtil widgetUtil = new WidgetUtil(getContext());

    TreeSet<Integer> allVotingValues = new TreeSet<>();
    for (Map.Entry<String, LabelInfo> e : change.info.labels.entrySet()) {
      String labelName = e.getKey();
      for (String value : e.getValue().values.keySet()) {
        if (!change.info.permittedLabels.containsKey(labelName)
            || !change.info.permittedLabels.get(labelName).contains(value)) {
          continue;
        }
        allVotingValues.add(Integer.parseInt(value.trim()));
      }
    }

    TableRow tr = new TableRow(getContext());
    tr.setLayoutParams(matchAndWrapTableRowLayout());
    addView(tr, matchAndWrapTableLayout());
    View v = new View(getContext());
    v.setLayoutParams(matchAndWrapTableRowLayout());
    tr.addView(v);
    LinearLayout l = new LinearLayout(getContext());
    l.setLayoutParams(matchAndWrapTableRowLayout());
    l.setOrientation(HORIZONTAL);
    tr.addView(l);
    for (int value : allVotingValues) {
      TextView valueText = new TextView(getContext());
      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      params.weight = 1;
      valueText.setGravity(Gravity.CENTER_HORIZONTAL);
      valueText.setLayoutParams(params);
      valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
      valueText.setTypeface(Typeface.MONOSPACE);
      valueText.setText(FormatUtil.formatLabelValue(value));
      l.addView(valueText);
    }

    for (Map.Entry<String, LabelInfo> e : change.info.labels.entrySet()) {
      final String labelName = e.getKey();
      LabelInfo label = e.getValue();

      tr = new TableRow(getContext());
      tr.setLayoutParams(matchAndWrapTableRowLayout());

      TextView labelNameText = new TextView(getContext());
      TableRow.LayoutParams params = wrapTableRowLayout();
      params.setMargins(0, widgetUtil.dpToPx(5), 0, 0);
      labelNameText.setLayoutParams(params);
      labelNameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
      labelNameText.setText(labelName);
      tr.addView(labelNameText);

      RadioGroup rg = new RadioGroup(getContext());
      TableRow.LayoutParams rgParams = wrapTableRowLayout();
      rgParams.gravity = Gravity.CENTER_HORIZONTAL;
      rgParams.setMargins(widgetUtil.dpToPx(2), 0, widgetUtil.dpToPx(2), 0);
      rg.setLayoutParams(rgParams);
      rg.setOrientation(RadioGroup.HORIZONTAL);
      tr.addView(rg);

      final TextView infoText = new TextView(getContext());
      TableRow.LayoutParams infoTextParams = wrapTableRowLayout();
      infoTextParams.gravity = Gravity.CENTER_VERTICAL;
      infoText.setLayoutParams(infoTextParams);
      infoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
      tr.addView(infoText);

      for (final Map.Entry<String, String> valueEntry
          : label.values.entrySet()) {
        if (!change.info.permittedLabels.containsKey(labelName)
            || !change.info.permittedLabels.get(labelName)
                .contains(valueEntry.getKey())) {
          continue;
        }

        RadioButton rb = new RadioButton(getContext());
        rg.addView(rb);

        final int value = Integer.parseInt(valueEntry.getKey().trim());

        rb.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            infoText.setText(valueEntry.getValue());

            for (OnSelectListener l : listeners) {
              l.onSelect(labelName, value);
            }
          }
        });

        int defaultValue = votes.containsKey(labelName) ? votes.get
            (labelName) : (label.defaultValue != null ? label.defaultValue : 0);
        if (value == defaultValue) {
          rb.performClick();
        }
      }

      addView(tr, matchAndWrapTableLayout());
    }
  }

  public void addOnSelectListener(OnSelectListener l) {
    listeners.add(l);
  }

  public interface OnSelectListener {
    void onSelect(String label, int vote);
  }
}
