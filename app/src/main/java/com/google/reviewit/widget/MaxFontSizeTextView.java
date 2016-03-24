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
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.google.reviewit.R;
import com.google.reviewit.util.WidgetUtil;

/**
 * TextView that automatically sets the font size as large as possible
 * without additional line breaks.
 */
public class MaxFontSizeTextView extends TextView {
  private static final float THRESHOLD = 0.5f;
  private static final float MIN_TEXT_SIZE_DEFAULT = 2;
  private static final float MAX_TEXT_SIZE_DEFAULT = 100;

  private float minTextSize;
  private float maxTextSize;
  private final int maxLineLength;
  private Paint paint;
  private String text;

  public MaxFontSizeTextView(Context context) {
    this(context, null);
  }

  public MaxFontSizeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    WidgetUtil widgetUtil = new WidgetUtil(context);
    TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
        R.styleable.MaxFontSizeTextView, 0, 0);
    maxLineLength = a.getInteger(R.styleable
        .MaxFontSizeTextView_maxLineLength, 0);
    minTextSize = widgetUtil.toDimension(a.getString(
        R.styleable.MaxFontSizeTextView_minTextSize), MIN_TEXT_SIZE_DEFAULT);
    maxTextSize = widgetUtil.toDimension(a.getString(
        R.styleable.MaxFontSizeTextView_maxTextSize), MAX_TEXT_SIZE_DEFAULT);

    paint = new Paint();

    if (a.getBoolean(R.styleable.MaxFontSizeTextView_ellipsize, false)) {
      addOnLayoutChangeListener(new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int
            bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
          Layout layout = getLayout();
          if (layout == null) {
            return;
          }
          // TODO the computation doesn't work when the min font size
          // is larger than the computed max font or when some
          // lines exceed maxLineLength, because then there are
          // additional line breaks
          int lastVisibleLine = layout.getLineForVertical(getHeight());
          if (getLineCount() > lastVisibleLine + 1) {
            int lines = 1;
            StringBuilder b = new StringBuilder();
            for (String line : text.split("\n")) {
              if (lines == lastVisibleLine) {
                b.append("...");
                setText(b.toString());
                return;
              }
              b.append(line);
              b.append("\n");
              lines++;
            }
          }
        }
      });
    }
  }

  public void setMinTextSize(float minTextSize) {
    this.minTextSize = minTextSize;
  }

  public void setMaxTextSize(float maxTextSize) {
    this.maxTextSize = maxTextSize;
  }

  private void refitText(CharSequence text, int textWidth) {
    if (textWidth <= 0) {
      return;
    }
    int width = textWidth - getPaddingLeft() - getPaddingRight();

    paint.set(getPaint());

    // TODO works only for monospace font
    String longestLine = null;
    for (String line : text.toString().split("\n")) {
      if (longestLine != null && line.length() <= longestLine.length()) {
        continue;
      }
      longestLine = line;
    }
    if (longestLine == null) {
      return;
    }

    if (Float.valueOf(minTextSize).equals(maxTextSize)) {
      setTextSize(TypedValue.COMPLEX_UNIT_PX, minTextSize);
      return;
    }

    if (maxLineLength > 0 && longestLine.length() > maxLineLength) {
      longestLine = longestLine.substring(0, maxLineLength);
    }

    float high = maxTextSize;
    float low = minTextSize;
    while ((high - low) > THRESHOLD) {
      float size = (high + low) / 2;
      paint.setTextSize(size);
      if (paint.measureText(longestLine) >= width) {
        // font size too big
        high = size;
      } else {
        // font size too small
        low = size;
      }
    }
    setTextSize(TypedValue.COMPLEX_UNIT_PX, low);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
    refitText(getText(), parentWidth);
    setMeasuredDimension(parentWidth, getMeasuredHeight());
  }

  @Override
  protected void onTextChanged(
      CharSequence text, int start, int before, int after) {
    this.text = text.toString();
    refitText(text, this.getWidth());
  }

  @Override
  protected void onSizeChanged(
      int width, int height, int oldWidth, int oldHeight) {
    if (width != oldWidth) {
      refitText(getText(), width);
    }
  }
}
