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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.reviewit.AvatarTask;
import com.google.reviewit.R;
import com.google.reviewit.app.ReviewItApp;

import static com.google.reviewit.util.LayoutUtil.wrapTableRowLayout;

public class WidgetUtil {
  private final Context context;

  public WidgetUtil(Context context) {
    this.context = context;
  }

  public @ColorInt int color(@ColorRes int id) {
    // TODO use getResources().getColor(id, null) with API 23
    return context.getResources().getColor(id);
  }

  public int spToPx(int sp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
        context.getResources().getDisplayMetrics());
  }

  public int dpToPx(int dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        context.getResources().getDisplayMetrics());
  }

  public Drawable getDrawable(@DrawableRes int id) {
    return context.getResources().getDrawable(id);
  }

  public View tableRowRightMargin(View view, int rightMarginDp) {
    TableRow.LayoutParams layoutParams =
        (TableRow.LayoutParams) view.getLayoutParams();
    layoutParams.rightMargin = dpToPx(rightMarginDp);
    view.setLayoutParams(layoutParams);
    return view;
  }

  public TextView createTextView(String text, int textSizeSp) {
    return createTextView(text, textSizeSp, false);
  }

  public TextView createTextView(String text, int textSizeSp, boolean isHtlm) {
    TextView textView = new TextView(context);
    textView.setLayoutParams(wrapTableRowLayout());
    textView.setText(isHtlm ? Html.fromHtml(text) : text);
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
    if (isHtlm) {
      textView.setLinkTextColor(color(R.color.hyperlink));
      textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    return textView;
  }

  public static void underline(TextView textView) {
    textView.setPaintFlags(textView.getPaintFlags()
        | Paint.UNDERLINE_TEXT_FLAG);
  }

  public void showError(@StringRes int id) {
    showError(context.getString(id));
  }

  public void showError(String msg) {
    new AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.error_title))
        .setMessage(msg)
        .setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            // do nothing
          }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
  }

  public int getDimension(@DimenRes int id) {
    return (int) context.getResources().getDimension(id);
  }

  public float toDimension(String value, float defaultValue) {
    if (value == null) {
      return defaultValue;
    }
    try {
      if (value.endsWith("sp")) {
        value = value.substring(0, value.length() - 2).trim();
        return spToPx(Integer.valueOf(value));
      } else if (value.endsWith("dp")) {
        value = value.substring(0, value.length() - 2).trim();
        return dpToPx(Integer.valueOf(value));
      } else if (value.endsWith("px")) {
        value = value.substring(0, value.length() - 2).trim();
        return Integer.valueOf(value);
      } else {
        return Integer.valueOf(value);
      }
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void setBackgroundColor(View view, @ColorRes int colorId) {
    view.setBackgroundColor(color(colorId));
  }

  public static void setBackground(View view, Drawable drawable) {
    view.setBackground(drawable);
  }

  public static void setXY(View view, int x, int y) {
    view.setX(x);
    view.setY(y);
  }

  public static void setRightMargin(View view, int margin) {
    ViewGroup.MarginLayoutParams layoutParams =
        (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    layoutParams.rightMargin = margin;
  }

  public static View setBottomMargin(View view, int margin) {
    ViewGroup.MarginLayoutParams layoutParams =
        (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    layoutParams.bottomMargin = margin;
    return view;
  }

  public static void setText(View view, String text) {
    if (view instanceof TextView) {
      ((TextView) view).setText(text);
    } else {
      throw new IllegalStateException("unexpected view type "
          + view.getClass().getName() + " for id " + view.getId());
    }
  }

  public static void setText(View base, @IdRes int id, String text) {
    View view = base.findViewById(id);
    if (view instanceof TextView) {
      ((TextView) view).setText(text);
    } else {
      throw new IllegalStateException("unexpected view type "
          + view.getClass().getName() + " for id " + id);
    }
  }

  public static void displayAvatar(
      ReviewItApp app, AccountInfo account, ImageView avatar) {
    new AvatarTask(app, avatar).executeOnExecutor(app.getExecutor(), account);
  }

  public static void setGone(View... views) {
    for (View view : views) {
      view.setVisibility(View.GONE);
    }
  }

  public static void setInvisible(View... views) {
    for (View view : views) {
      view.setVisibility(View.INVISIBLE);
    }
  }

  public static void setVisible(View... views) {
    for (View view : views) {
      view.setVisibility(View.VISIBLE);
    }
  }

  public static void setVisible(boolean visible, View... views) {
    if (visible) {
      setVisible(views);
    } else {
      setGone(views);
    }
  }
}
