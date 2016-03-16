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

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TableLayout;
import android.widget.TableRow;

public class LayoutUtil {
  public static ViewGroup.LayoutParams matchLayout() {
    return new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
  }

  public static ViewGroup.LayoutParams matchAndWrapLayout() {
    return new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
  }

  public static ViewGroup.LayoutParams matchAndFixedLayout(int height) {
    return new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, height);
  }

  public static TableLayout.LayoutParams matchAndWrapTableLayout() {
    return new TableLayout.LayoutParams(
        TableLayout.LayoutParams.MATCH_PARENT,
        TableLayout.LayoutParams.WRAP_CONTENT);
  }

  public static TableRow.LayoutParams matchAndWrapTableRowLayout() {
    return new TableRow.LayoutParams(
        TableRow.LayoutParams.MATCH_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT);
  }

  public static TableRow.LayoutParams wrapTableRowLayout() {
    return new TableRow.LayoutParams(
        TableRow.LayoutParams.WRAP_CONTENT,
        TableRow.LayoutParams.WRAP_CONTENT);
  }

  public static TableRow.LayoutParams wrapTableRowLayout(int span) {
    TableRow.LayoutParams params = new TableRow.LayoutParams(
        TableRow.LayoutParams.WRAP_CONTENT,
        TableRow.LayoutParams.WRAP_CONTENT);
    params.span = span;
    return params;
  }

  public static void addOneTimeOnGlobalLayoutListener(final View view, final
  OneTimeOnGlobalLayoutListener oneTimeOnGlobalLayoutListener) {
    view.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        oneTimeOnGlobalLayoutListener.onFirstGlobalLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
          view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
      }
    });
  }

  /**
   * <code>OnGlobalLayoutListener</code> which is invoked only once.
   */
  public interface OneTimeOnGlobalLayoutListener {
    void onFirstGlobalLayout();
  }

  public static void addOnHeightListener(final View view, final
  OnHeightListener onHeightListener) {
    addOneTimeOnGlobalLayoutListener(view, new OneTimeOnGlobalLayoutListener() {
      @Override
      public void onFirstGlobalLayout() {
        onHeightListener.onHeight(view.getHeight());
      }
    });
  }

  /**
   * Listener that is invoked once the height of the widget is known.
   */
  public interface OnHeightListener {
    void onHeight(int height);
  }
}
