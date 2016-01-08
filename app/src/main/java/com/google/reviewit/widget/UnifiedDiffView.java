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
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gerrit.extensions.common.DiffInfo;
import com.google.reviewit.R;
import com.google.reviewit.util.WidgetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableRowLayout;
import static com.google.reviewit.util.LayoutUtil.matchLayout;
import static com.google.reviewit.util.LayoutUtil.wrapTableRowLayout;

/**
 * View that shows the unified diff for one file.
 */
public class UnifiedDiffView extends TableLayout {
  private static final String TAG = UnifiedDiffView.class.getName();

  private static int CONTEXT_LINES = 10;

  private enum LineChangeType {
    ADD('+'), DELETE('-'), NO_CHANGE(' ');

    private final char prefix;

    LineChangeType(char prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return Character.toString(prefix);
    }
  }

  private final WidgetUtil widgetUtil;
  private OnSizeChangedListener onSizeChangedListener;

  public UnifiedDiffView(Context context, DiffInfo diff) {
    this(context, null, diff);
  }

  public UnifiedDiffView(Context context, AttributeSet attrs, DiffInfo diff) {
    super(context, attrs);

    this.widgetUtil = new WidgetUtil(context);

    setLayoutParams(matchLayout());
    setColumnStretchable(3, true);
    setColumnShrinkable(3, true);

    int a = 0;
    int b = 0;
    for (int i = 0; i < diff.content.size(); i++) {
      DiffInfo.ContentEntry content = diff.content.get(i);
      if (content.common != null && content.common) {
        a += addDeletedLines(
            a, content.a, getIntraline(content.a, content.b, content.editA));
        b += addAddedLines(
            b, content.b, getIntraline(content.b, content.a, content.editB));
      } else {
        a += addDeletedLines(
            a, content.a, getIntraline(content.a, content.b, content.editA));
        b += addAddedLines(
            b, content.b, getIntraline(content.b, content.a, content.editB));
      }

      int count = addUnchangedLines(a, b, content.ab, CONTEXT_LINES, i == 0,
          i == diff.content.size() - 1);
      a += count;
      b += count;

      if (content.skip != null) {
        a += content.skip;
        b += content.skip;
      }
    }
  }

  private List<List<Integer>> getIntraline(List<String> lines, List<String>
      otherLines, List<List<Integer>> intraline) {
    if (lines == null) {
      return null;
    }

    if (intraline != null || otherLines != null) {
      return intraline;
    }

    int length = 0;
    for (String line : lines) {
      length += line.length() + 1; // +1 for newline character
    }

    // blocks which are completely deleted or newly added should be highlighted
    List<Integer> list = new ArrayList<>();
    list.add(0); // skip nothing
    list.add(length); // mark all
    intraline = new ArrayList<>();
    intraline.add(list);
    return intraline;
  }

  public void setOnSizeChangedListener(
      OnSizeChangedListener onSizeChangedListener) {
    this.onSizeChangedListener = onSizeChangedListener;
  }

  private int addUnchangedLines(int a, int b, List<String> lines, int
      contextLines, boolean isFirst, boolean isLast) {
    if (lines == null) {
      return 0;
    }

    if (isFirst) {
      if (lines.size() < contextLines + 1) {
        addUnchangedLines(a, b, lines);
      } else {
        int skipped = lines.size() - contextLines;
        addSkippedRow(a, b, lines.subList(0, skipped), true, isLast);
        a += skipped;
        b += skipped;
        for (int i = skipped; i < lines.size(); i++) {
          addRow(++a, ++b, LineChangeType.NO_CHANGE, lines.get(i), null);
        }
      }
    } else if (isLast) {
      if (lines.size() < contextLines + 1) {
        addUnchangedLines(a, b, lines);
      } else {
        for (int i = 0; i < contextLines; i++) {
          addRow(++a, ++b, LineChangeType.NO_CHANGE, lines.get(i), null);
        }
        addSkippedRow(
            a, b, lines.subList(contextLines, lines.size()), false, true);
      }
    } else {
      if (lines.size() < 2 * contextLines + 1) {
        addUnchangedLines(a, b, lines);
      } else {
        for (int i = 0; i < contextLines; i++) {
          addRow(++a, ++b, LineChangeType.NO_CHANGE, lines.get(i), null);
        }
        int skipped = lines.size() - 2 * contextLines;
        addSkippedRow(
            a, b, lines.subList(contextLines, lines.size() - contextLines),
            false, false);
        a += skipped;
        b += skipped;
        for (int i = contextLines + skipped; i < lines.size(); i++) {
          addRow(++a, ++b, LineChangeType.NO_CHANGE, lines.get(i), null);
        }
      }
    }

    return lines.size();
  }

  private int addUnchangedLines(int a, int b, List<String> lines) {
    return addUnchangedLines(a, b, lines, -1);
  }

  private int addUnchangedLines(
      int a, int b, List<String> lines, int position) {
    if (lines == null) {
      return 0;
    }

    for (int i = 0; i < lines.size(); i++) {
      addRow(++a, ++b, LineChangeType.NO_CHANGE, lines.get(i), null, position);
      if (position != -1) {
        position++;
      }
    }

    return lines.size();
  }

  private int addAddedLines(int b, List<String> lines, List<List<Integer>>
      intraline) {
    if (lines == null) {
      return 0;
    }

    Map<Integer, Intraline> intralineByLine = intralineByLine(lines, intraline);
    for (int i = 0; i < lines.size(); i++) {
      addRow(-1, ++b, LineChangeType.ADD, lines.get(i), intralineByLine.get(i));
    }
    return lines.size();
  }

  private int addDeletedLines(
      int a, List<String> lines, List<List<Integer>> intraline) {
    if (lines == null) {
      return 0;
    }

    Map<Integer, Intraline> intralineByLine = intralineByLine(lines, intraline);
    for (int i = 0; i < lines.size(); i++) {
      addRow(++a, -1, LineChangeType.DELETE, lines.get(i),
          intralineByLine.get(i));
    }
    return lines.size();
  }

  private Map<Integer, Intraline> intralineByLine(
      List<String> lines, List<List<Integer>> intraline) {
    if (intraline == null) {
      return Collections.emptyMap();
    }

    int line = 0;
    int posInLine = 0;
    Map<Integer, Intraline> byLine = new HashMap<>();
    for (List<Integer> intralineEntry : intraline) {
      int skip = intralineEntry.get(0);
      int mark = intralineEntry.get(1);
      while (mark > 0) {
        int lineLength = lines.get(line).length();
        // +1 for newline character
        while (skip >= lineLength + 1 - posInLine) {
          skip -= (lineLength + 1 - posInLine);
          line++;
          posInLine = 0;
          lineLength = lines.get(line).length();
        }
        if (skip + mark <= lineLength - posInLine) {
          Intraline i = byLine.get(line);
          if (i == null) {
            i = new Intraline(lineLength);
            byLine.put(line, i);
          }
          i.addRange(posInLine + skip, posInLine + skip + mark);
          posInLine += skip + mark;
          mark = 0;
          skip = 0;
        } else {
          Intraline i = byLine.get(line);
          if (i == null) {
            i = new Intraline(lineLength);
            byLine.put(line, i);
          }
          i.addRange(posInLine + skip, lineLength);
          i.setHighlightNewline(true);
          // +1 for newline character
          mark -= (lineLength + 1) - posInLine - skip;
          skip = 0;
          line++;
          posInLine = 0;
        }
      }
    }

    return byLine;
  }

  private void addRow(
      int a, int b, LineChangeType changeType, String line,
      Intraline intraline) {
    addRow(a, b, changeType, line, intraline, -1);
  }

  private void addRow(
      int a, int b, LineChangeType changeType, String line, Intraline intraline,
      int position) {
    TableRow tr = new TableRow(getContext());
    tr.setLayoutParams(matchAndWrapTableRowLayout());

    String lineNumberA = a > 0 ? Integer.toString(a) : "";
    TextView lineNumberAText = createTextView(lineNumberA, 7);
    lineNumberAText.setPadding(
        widgetUtil.dpToPx(2), 0, widgetUtil.dpToPx(2), 0);
    lineNumberAText.setTextColor(widgetUtil.color(R.color.lineNumbers));
    tr.addView(lineNumberAText);

    String lineNumberB = b > 0 ? Integer.toString(b) : "";
    TextView lineNumberBText = createTextView(lineNumberB, 7);
    lineNumberBText.setPadding(
        widgetUtil.dpToPx(2), 0, widgetUtil.dpToPx(2), 0);
    lineNumberBText.setTextColor(widgetUtil.color(R.color.lineNumbers));
    tr.addView(lineNumberBText);

    TextView changeTypeText = createTextView(changeType.getPrefix(), 7);
    changeTypeText.setPadding(
        widgetUtil.dpToPx(2), 0, widgetUtil.dpToPx(2), 0);
    switch (changeType) {
      case ADD:
        changeTypeText.setTextColor(
            widgetUtil.color(R.color.lineAddedIntraline));
        changeTypeText.setBackgroundColor(widgetUtil.color(R.color.lineAdded));
        break;
      case DELETE:
        changeTypeText.setTextColor(
            widgetUtil.color(R.color.lineDeletedIntraline));
        changeTypeText.setBackgroundColor(
            widgetUtil.color(R.color.lineDeleted));
        break;
      case NO_CHANGE:
      default:
        break;
    }
    tr.addView(changeTypeText);

    TextView lineText =
        createLineWithIntralineDiffs(line, changeType, intraline, 7);
    tr.addView(lineText);

    if (position == -1) {
      addView(tr, matchAndWrapTableLayout());
    } else {
      addView(tr, position, matchAndWrapTableLayout());
    }
  }

  private void addSkippedRow(
      int a, int b, List<String> skippedLines, boolean isFirst,
      boolean isLast) {
    final TableRow tr = new TableRow(getContext());
    tr.setLayoutParams(matchAndWrapTableRowLayout());

    tr.setTag(R.id.UNIFIED_DIFF_A, a);
    tr.setTag(R.id.UNIFIED_DIFF_B, b);
    tr.setTag(R.id.UNIFIED_DIFF_LINES, skippedLines);

    final LinearLayout l = new LinearLayout(getContext());
    l.setOrientation(HORIZONTAL);
    TableRow.LayoutParams params = matchAndWrapTableRowLayout();
    params.span = 4;
    l.setLayoutParams(params);
    l.setBackgroundColor(widgetUtil.color(R.color.lineSkipped));

    LinearLayout spacerLeft = new LinearLayout(getContext());
    params = wrapTableRowLayout();
    params.weight = 1;
    spacerLeft.setLayoutParams(params);
    l.addView(spacerLeft);

    final Button expandBeforeButton = createHyperlinkButton(
        getContext().getString(R.string.expand_before, CONTEXT_LINES));
    final Button expandAllButton = createHyperlinkButton(
        getContext().getString(
            R.string.skipped_common_lines, skippedLines.size()));
    final Button expandAfterButton = createHyperlinkButton(
        getContext().getString(R.string.expand_after, CONTEXT_LINES));

    if (!isFirst && skippedLines.size() > 2 * CONTEXT_LINES) {
      expandBeforeButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          int row = indexOfChild(tr);
          int a = (int) tr.getTag(R.id.UNIFIED_DIFF_A);
          int b = (int) tr.getTag(R.id.UNIFIED_DIFF_B);
          List<String> lines =
              (List<String>) tr.getTag(R.id.UNIFIED_DIFF_LINES);
          List<String> linesToShow = lines.subList(0, CONTEXT_LINES);
          List<String> skippedLines =
              lines.subList(CONTEXT_LINES, lines.size());
          addUnchangedLines(a, b, linesToShow, row);
          tr.setTag(R.id.UNIFIED_DIFF_A, a + CONTEXT_LINES);
          tr.setTag(R.id.UNIFIED_DIFF_B, b + CONTEXT_LINES);
          tr.setTag(R.id.UNIFIED_DIFF_LINES, skippedLines);
          expandAllButton.setText(getContext()
              .getString(R.string.skipped_common_lines, skippedLines.size()));
          if (skippedLines.size() <= 2 * CONTEXT_LINES) {
            l.removeView(expandBeforeButton);
            l.removeView(expandAfterButton);
          }
          if (onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChanged();
          }
        }
      });
      l.addView(expandBeforeButton);
    }

    expandAllButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int row = indexOfChild(tr);
        int a = (int) tr.getTag(R.id.UNIFIED_DIFF_A);
        int b = (int) tr.getTag(R.id.UNIFIED_DIFF_B);
        List<String> lines = (List<String>) tr.getTag(R.id.UNIFIED_DIFF_LINES);
        removeView(tr);
        addUnchangedLines(a, b, lines, row);
        if (onSizeChangedListener != null) {
          onSizeChangedListener.onSizeChanged();
        }
      }
    });
    l.addView(expandAllButton);

    if (!isLast && skippedLines.size() > 2 * CONTEXT_LINES) {
      expandAfterButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          int row = indexOfChild(tr);
          int a = (int) tr.getTag(R.id.UNIFIED_DIFF_A);
          int b = (int) tr.getTag(R.id.UNIFIED_DIFF_B);
          List<String> lines =
              (List<String>) tr.getTag(R.id.UNIFIED_DIFF_LINES);
          int skip = lines.size() - CONTEXT_LINES;
          List<String> linesToShow = lines.subList(skip, lines.size());
          List<String> skippedLines = lines.subList(0, skip);
          addUnchangedLines(a + skip, b + skip, linesToShow, row + 1);
          tr.setTag(R.id.UNIFIED_DIFF_LINES, skippedLines);
          expandAllButton.setText(getContext()
              .getString(R.string.skipped_common_lines, skippedLines.size()));
          if (skippedLines.size() <= 2 * CONTEXT_LINES) {
            l.removeView(expandBeforeButton);
            l.removeView(expandAfterButton);
          }
          if (onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChanged();
          }
        }
      });
      l.addView(expandAfterButton);
    }

    LinearLayout spacerRight = new LinearLayout(getContext());
    params = wrapTableRowLayout();
    params.weight = 1;
    spacerRight.setLayoutParams(params);
    l.addView(spacerRight);

    tr.addView(l);

    addView(tr, matchAndWrapTableLayout());
  }

  private Button createHyperlinkButton(String text) {
    Button b = new Button(getContext());
    TableRow.LayoutParams params = wrapTableRowLayout();
    params.topMargin = -1 * widgetUtil.dpToPx(17);
    params.bottomMargin = params.topMargin;
    params.rightMargin = -1 * widgetUtil.dpToPx(13);
    params.leftMargin = params.rightMargin;
    b.setLayoutParams(params);
    b.setTextColor(widgetUtil.color(R.color.hyperlink));
    b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
    b.setTypeface(Typeface.MONOSPACE);
    b.setPaintFlags(b.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    b.setBackgroundColor(widgetUtil.color(R.color.transparent));
    b.setAllCaps(false);
    b.setText(text);
    return b;
  }

  private TextView createLineWithIntralineDiffs(
      String line, LineChangeType changeType, Intraline intraline,
      int textSizeSp) {
    SpannableStringBuilder b = new SpannableStringBuilder(line);

    boolean invert = intraline != null && intraline.isHighlightNewline();
    int lineBackgroundColorId = -1;
    switch (changeType) {
      case ADD:
        lineBackgroundColorId = invert
            ? R.color.lineAddedIntraline
            : R.color.lineAdded;
        break;
      case DELETE:
        lineBackgroundColorId = invert
            ? R.color.lineDeletedIntraline
            : R.color.lineDeleted;
        break;
      case NO_CHANGE:
      default:
        break;
    }


    if (intraline != null) {
      try {
        List<Range> ranges = invert
            ? intraline.getInvertedRanges()
            : intraline.getRanges();
        for (Range range : ranges) {
          switch (changeType) {
            case ADD:
              BackgroundColorSpan addedSpan =
                  new BackgroundColorSpan(widgetUtil.color(
                      invert
                          ? R.color.lineAdded
                          : R.color.lineAddedIntraline));
              b.setSpan(addedSpan, range.from, range.to,
                  Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
              break;
            case DELETE:
              BackgroundColorSpan deletedSpan =
                  new BackgroundColorSpan(widgetUtil.color(
                      invert
                          ? R.color.lineDeleted
                          : R.color.lineDeletedIntraline));
              b.setSpan(deletedSpan, range.from, range.to,
                  Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
              break;
            case NO_CHANGE:
            default:
              break;
          }
        }
      } catch (Throwable t) {
        Log.e(TAG, "Failed to render intraline diff " + intraline
            + " for line '" + line + "'", t);
      }
    }

    TextView textView = new TextView(getContext());
    textView.setLayoutParams(wrapTableRowLayout());
    textView.setText(b);
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
    textView.setTypeface(Typeface.MONOSPACE);

    if (lineBackgroundColorId != -1) {
      textView.setBackgroundColor(widgetUtil.color(lineBackgroundColorId));
    }

    return textView;
  }

  private TextView createTextView(String text, int textSizeSp) {
    TextView textView = new TextView(getContext());
    textView.setLayoutParams(wrapTableRowLayout());
    textView.setText(text);
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
    textView.setTypeface(Typeface.MONOSPACE);
    return textView;
  }

  public interface OnSizeChangedListener {
    void onSizeChanged();
  }

  private static class Range {
    final int from;
    final int to;

    Range(int from, int to) {
      this.from = from;
      this.to = to;
    }
  }

  private static class Intraline {
    private final int lineLength;
    private final List<Range> ranges;

    private boolean highlightNewline;

    Intraline(int lineLength) {
      this.lineLength = lineLength;
      this.ranges = new ArrayList<>();
    }

    void addRange(int from, int to) {
      checkArgument(from <= to, "invalid range: {from: %s, to: %s}", from, to);
      ranges.add(new Range(from, to));
    }

    public List<Range> getRanges() {
      return ranges;
    }

    public void setHighlightNewline(boolean highlightNewline) {
      this.highlightNewline = highlightNewline;
    }

    public boolean isHighlightNewline() {
      return highlightNewline;
    }

    private List<Range> getInvertedRanges() {
      if (ranges.isEmpty()) {
        return ranges;
      }

      List<Range> invertedRanges = new ArrayList<>(ranges.size() + 1);
      int from = 0;
      for (Range range : ranges) {
        if (from != range.from) {
          invertedRanges.add(new Range(from, range.from));
        }
        from = range.to;
      }
      if (from != lineLength) {
        invertedRanges.add(new Range(from, lineLength));
      }
      return invertedRanges;
    }

    public String toString() {
      StringBuilder b = new StringBuilder();
      b.append("{lineLength: ")
          .append(lineLength)
          .append(", ranges[");
      for (int i = 0; i < ranges.size(); i++) {
        Range range = ranges.get(i);
        b.append("{from: ")
            .append(range.from)
            .append(", to:")
            .append(range.to)
            .append("}");
        if (i < ranges.size() - 1) {
          b.append(", ");
        }
      }
      b.append("]}");

      return b.toString();
    }
  }
}
