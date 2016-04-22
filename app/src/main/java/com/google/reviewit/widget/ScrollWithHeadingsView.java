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
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.reviewit.R;
import com.google.reviewit.util.LayoutUtil;
import com.google.reviewit.util.WidgetUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.reviewit.util.LayoutUtil.matchAndWrapLayout;
import static com.google.reviewit.util.LayoutUtil.matchLayout;

/**
 * Scroll view that displays a list of views that each have a heading.
 * While scrolling the heading of the current view is fixed at the top.
 */
public class ScrollWithHeadingsView extends RelativeLayout {
  private static final
  @ColorRes
  int[] BACKGROUND_COLORS = new int[] {
      R.color.heading1, R.color.heading2, R.color.heading3, R.color.heading4,
      R.color.heading5, R.color.heading6, R.color.heading7, R.color.heading8,
      R.color.heading9, R.color.heading10, R.color.heading11, R.color.heading12};

  private final ScrollView scroll;
  private final LinearLayout scrollContent;
  private final List<Content> contents = new ArrayList<>();
  private final ZoomHandler zoomHandler;

  private Window window;
  private int nextBackgroundColor = 0;
  private ViewTreeObserver.OnScrollChangedListener onScrollListener;

  public ScrollWithHeadingsView(Context context) {
    this(context, null);
  }

  public ScrollWithHeadingsView(Context context, AttributeSet attrs) {
    super(context, attrs);

    scroll = new ScrollView(context);
    scroll.setLayoutParams(matchLayout());
    addView(scroll);

    scrollContent = new LinearLayout(context);
    scrollContent.setOrientation(LinearLayout.VERTICAL);
    scrollContent.setLayoutParams(matchLayout());
    scroll.addView(scrollContent);

    zoomHandler = new ZoomHandler(this);
  }

  public ZoomHandler getZoomHandler() {
    return zoomHandler;
  }

  public void setWindow(Window window) {
    this.window = window;
  }

  public void setContent(Iterator<Content> contentProvider) {
    Content prevContent = null;
    Content nextContent = contentProvider.hasNext()
        ? contentProvider.next()
        : null;
    while (nextContent != null) {
      final Content content = nextContent;
      contents.add(content);
      nextContent = contentProvider.hasNext()
          ? contentProvider.next()
          : null;
      final boolean setWindowColor = prevContent == null;
      post(new Runnable() {
        @Override
        public void run() {
          addHeading(content);
          if (setWindowColor) {
            setWindowColor(content.getHeading());
          }
          scrollContent.addView(content.getContent());
        }
      });
      prevContent = content;
    }

    onScrollListener = new ViewTreeObserver.OnScrollChangedListener() {
      @Override
      public void onScrollChanged() {
        Iterator<Content> it = contents.iterator();
        Content prevContent = null;
        Content nextContent = it.hasNext() ? it.next() : null;
        while (nextContent != null) {
          Content content = nextContent;
          nextContent = it.hasNext() ? it.next() : null;
          ScrollWithHeadingsView.this.onScrollChanged(
              content, prevContent, nextContent);
          prevContent = content;
        }
      }
    };
    scroll.getViewTreeObserver().addOnScrollChangedListener(onScrollListener);

    LayoutUtil.addOneTimeOnGlobalLayoutListener(scroll,
        new LayoutUtil.OneTimeOnGlobalLayoutListener() {
      @Override
      public void onFirstGlobalLayout() {
        relayout();
      }
    });
  }

  private void clear() {
    for (Content content : contents) {
      removeView(content.getHeading().getView());
      scrollContent.removeView(content.getContent());
    }
    contents.clear();
    scroll.getViewTreeObserver()
        .removeOnScrollChangedListener(onScrollListener);
    onScrollListener = null;
  }

  public void relayout() {
    // hack to refresh the view, this ensures that the headers are positioned
    // correctly
    scroll.setScrollY(scroll.getScrollY() + 1);
    scroll.setScrollY(scroll.getScrollY() - 1);
  }

  private void addHeading(final Content content) {
    final Heading heading = content.getHeading();
    heading.setBackgroundColor(getNextBackgroundColor());
    addView(heading.getView());

    final View contentView = content.getContent();
    heading.addOnHeightListener(new LayoutUtil.OnHeightListener() {
      @Override
      public void onHeight(int height) {
        contentView.setPadding(contentView.getPaddingLeft(),
            contentView.getPaddingTop() + height,
            contentView.getPaddingRight(),
            contentView.getPaddingBottom());
      }
    });

    LayoutUtil.addOneTimeOnGlobalLayoutListener(contentView,
        new LayoutUtil.OneTimeOnGlobalLayoutListener() {
          @Override
          public void onFirstGlobalLayout() {
            heading.getView().setTranslationY(
                contentView.getTop() - scroll.getScrollY());
          }
        });
  }

  private void onScrollChanged(
      Content content, final Content prevContent, final Content nextContent) {
    final View contentView = content.getContent();
    final Heading heading = content.getHeading();
    final Heading prevHeading =
        prevContent != null
            ? prevContent.getHeading()
            : null;
    final Heading nextHeading =
        nextContent != null
            ? nextContent.getHeading()
            : null;
    if (scroll.getScrollY() >= contentView.getTop()) {
      int scrollYRelativeToContentView =
          scroll.getScrollY() - contentView.getTop();
      if (scrollYRelativeToContentView < heading.getSquishableHeight()) {
        fixHeadingAtTop(heading, scrollYRelativeToContentView);
      } else {
        if (nextHeading != null
            && nextHeading.getView().getTranslationY() > 0) {
          // the position of nextHeading is only updated after this
          // onScrollChanged callback, but we need its correct position
          // now, hence update its position now
          nextHeading.getView().setTranslationY(
              nextContent.getContent().getTop() - scroll.getScrollY());
        }
        if ((nextHeading != null
            && nextHeading.getView().getTranslationY() <= 0)
            || (prevHeading != null
                && prevHeading.getView().getTranslationY() >= 0)) {
          heading.squish(heading.getSquishableHeight());
          heading.getView().setTranslationY(
              contentView.getTop() - scroll.getScrollY());
        } else {
          fixHeadingAtTop(heading, heading.getSquishableHeight());
        }
      }
    } else {
      heading.squish(0);
      heading.getView().setTranslationY(
          contentView.getTop() - scroll.getScrollY());

      if (prevHeading != null) {
        if (prevHeading.getView().getHeight()
            > heading.getView().getTranslationY()) {
          prevHeading.getView().setTranslationY(
              heading.getView().getTranslationY()
                  - prevHeading.getView().getHeight());
        }
      }
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    clear();
  }

  private void fixHeadingAtTop(Heading heading, int heightToSquish) {
    heading.squish(heightToSquish);
    heading.getView().setTranslationY(0);
    setWindowColor(heading);
  }

  private void setWindowColor(Heading heading) {
    if (window != null
        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (heading.getView().isAttachedToWindow()) {
        window.setStatusBarColor(heading.getBackgroundColor());
      }
    }
  }

  private
  @ColorRes
  int getNextBackgroundColor() {
    if (nextBackgroundColor >= BACKGROUND_COLORS.length) {
      nextBackgroundColor = 0;
    }
    return BACKGROUND_COLORS[nextBackgroundColor++];
  }

  public void setNextBackgroundColor(int nextBackgroundColor) {
    if (nextBackgroundColor < 0) {
      return;
    }

    while (nextBackgroundColor > BACKGROUND_COLORS.length) {
      nextBackgroundColor -= BACKGROUND_COLORS.length;
    }

    this.nextBackgroundColor = nextBackgroundColor;
  }

  public static class Content {
    private final Heading heading;
    private final View content;

    public Content(Heading heading, View content) {
      this.heading = heading;
      this.content = content;
    }

    public Heading getHeading() {
      return heading;
    }

    public View getContent() {
      return content;
    }
  }

  public interface Heading {
    /**
     * Get the view that implements the heading.
     */
    View getView();

    /**
     * The max height that the heading view can be squished.
     */
    int getSquishableHeight();

    /**
     * Squishes the heading view.
     */
    void squish(int heightToSquish);

    /**
     * Set the background color of the heading view.
     */
    void setBackgroundColor(@ColorRes int color);

    /**
     * Get the background color of the heading view.
     */
    @ColorInt
    int getBackgroundColor();

    /**
     * Register callback that is invoked once when the height of the
     * heading is known.
     */
    void addOnHeightListener(LayoutUtil.OnHeightListener onHeightListener);
  }

  public static class TextHeading implements Heading {
    protected final Context context;
    protected final WidgetUtil widgetUtil;
    protected final View headingView;
    protected final int headingPaddingMin;
    protected final int headingPaddingMax;

    public TextHeading(Context context, String headingText) {
      this.context = context;
      this.widgetUtil = new WidgetUtil(context);
      this.headingPaddingMin = widgetUtil.spToPx(12);
      this.headingPaddingMax = widgetUtil.spToPx(50);
      this.headingView = createHeadingView(headingText);
    }

    private View createHeadingView(String headingText) {
      MaxFontSizeTextView headingView = new MaxFontSizeTextView(context);
      headingView.setLayoutParams(matchAndWrapLayout());
      headingView.setText(headingText);
      headingView.setTextColor(widgetUtil.color(R.color.headingFont));
      headingView.setMinTextSize(widgetUtil.spToPx(10));
      headingView.setMaxTextSize(widgetUtil.spToPx(30));
      headingView.setGravity(Gravity.CENTER_HORIZONTAL);
      headingView.setPadding(5, headingPaddingMax, 5, headingPaddingMax);
      headingView.setIncludeFontPadding(false);
      return headingView;
    }

    @Override
    public View getView() {
      return headingView;
    }

    @Override
    public int getSquishableHeight() {
      return 2 * (headingPaddingMax - headingPaddingMin);
    }

    @Override
    public void squish(int heightToSquish) {
      if (heightToSquish > getSquishableHeight()) {
        heightToSquish = getSquishableHeight();
      }

      int paddingTop = headingPaddingMax - heightToSquish / 2;
      int paddingBottom = 2 * headingPaddingMax - heightToSquish - paddingTop;
      headingView.setPadding(headingView.getPaddingLeft(), paddingTop,
          headingView.getPaddingRight(), paddingBottom);
    }

    @Override
    public void setBackgroundColor(@ColorRes int color) {
      headingView.setBackgroundColor(widgetUtil.color(color));
    }

    @Override
    public
    @ColorInt
    int getBackgroundColor() {
      return ((ColorDrawable) headingView.getBackground()).getColor();
    }

    @Override
    public void addOnHeightListener(
        LayoutUtil.OnHeightListener onHeightListener) {
      LayoutUtil.addOnHeightListener(headingView, onHeightListener);
    }
  }

  public static class HeadingWithDetails extends TextHeading {
    private final RelativeLayout layout;
    private final TextView detailsTop;
    private final TextView detailsBottom;
    private int height;

    // TODO make top/bottom heading smaller if there are no top/bottom
    // details
    public HeadingWithDetails(
        Context context, String headingText, String detailsTopText,
        String detailsBottomText) {
      super(context, headingText);

      LayoutUtil.addOneTimeOnGlobalLayoutListener(headingView,
          new LayoutUtil.OneTimeOnGlobalLayoutListener() {
            @Override
            public void onFirstGlobalLayout() {
              height = headingView.getHeight();
              layout.getLayoutParams().height = height;
            }
          });

      layout = new RelativeLayout(context);
      layout.setLayoutParams(matchAndWrapLayout());

      detailsTop = createDetailsView(detailsTopText);
      layout.addView(detailsTop);

      layout.addView(headingView);

      detailsBottom = createDetailsView(detailsBottomText);
      layout.addView(gravityBottom(detailsBottom));
    }

    private TextView createDetailsView(String detailsText) {
      final MaxFontSizeTextView detailsView =
          new MaxFontSizeTextView(context);
      detailsView.setLayoutParams(matchAndWrapLayout());
      detailsView.setText(detailsText);
      detailsView.setTextColor(widgetUtil.color(R.color.headingFont));
      detailsView.setMinTextSize(widgetUtil.spToPx(5));
      detailsView.setMaxTextSize(widgetUtil.spToPx(15));
      detailsView.setGravity(Gravity.CENTER_HORIZONTAL);

      LayoutUtil.addOnHeightListener(detailsView,
          new LayoutUtil.OnHeightListener() {
        @Override
        public void onHeight(int height) {
          // +8 to center it correctly, don't understand why it's not
          // correct otherwise
          int padding = (headingPaddingMax - height) / 2 + 8;
          detailsView.setPadding(5, padding, 5, padding);
        }
      });

      return detailsView;
    }

    private View gravityBottom(View view) {
      LinearLayout l = new LinearLayout(context);
      l.setOrientation(LinearLayout.VERTICAL);
      l.setLayoutParams(matchLayout());
      l.setGravity(Gravity.CENTER_HORIZONTAL);
      View spacer = new View(context);
      spacer.setLayoutParams(new LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
      l.addView(spacer);
      l.addView(view);
      return l;
    }

    @Override
    public View getView() {
      return layout;
    }

    @Override
    public void squish(int heightToSquish) {
      super.squish(heightToSquish);

      float paddingSquish = heightToSquish / 2;
      if (paddingSquish > headingPaddingMax / 3) {
        detailsTop.setAlpha(0f);
        detailsBottom.setAlpha(0f);
      } else if (paddingSquish > 0) {
        float alpha = 1 - paddingSquish / (headingPaddingMax / 3);
        detailsTop.setAlpha(alpha);
        detailsBottom.setAlpha(alpha);
      } else {
        detailsTop.setAlpha(1f);
        detailsBottom.setAlpha(1f);
      }

      layout.getLayoutParams().height = height - heightToSquish;

    }

    public void setBackgroundColor(@ColorRes int color) {
      layout.setBackgroundColor(widgetUtil.color(color));
    }

    @Override
    public @ColorInt int getBackgroundColor() {
      return ((ColorDrawable) layout.getBackground()).getColor();
    }
  }
}
