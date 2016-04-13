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
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.reviewit.R;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.WidgetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapLayout;
import static com.google.reviewit.util.LayoutUtil.matchLayout;

/**
 * View to select a Code-Review vote.
 */
public class SelectCodeReviewView extends LinearLayout {
  private final WidgetUtil widgetUtil;
  private final List<VotingButton> votingButtons = new ArrayList<>();
  private final ImageView emoticon;

  private Change change;
  private OnVoteListener onVoteListener = null;

  public SelectCodeReviewView(Context context) {
    this(context, null);
  }

  public SelectCodeReviewView(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.widgetUtil = new WidgetUtil(context);

    setOrientation(VERTICAL);

    RelativeLayout votingPanel = new RelativeLayout(context);
    votingPanel.setLayoutParams(new RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, widgetUtil.dpToPx(200)));
    addView(votingPanel);

    VotingButton voteMinus1 = new VotingButton(context,
        Background.Corner.LEFT_TOP,
        R.drawable.ic_exposure_neg_1_white_48dp,
        R.drawable.ic_sentiment_dissatisfied_white_48dp,
        R.color.votingNegative,
        R.color.votingNegativeSelected,
        -1);
    VotingButton voteMinus2 = new VotingButton(context,
        Background.Corner.LEFT_BOTTOM,
        R.drawable.ic_exposure_neg_2_white_48dp,
        R.drawable.ic_sentiment_very_dissatisfied_white_48dp,
        R.color.votingNegative,
        R.color.votingNegativeSelected,
        -2);
    VotingButton votePlus1 = new VotingButton(context,
        Background.Corner.RIGHT_TOP,
        R.drawable.ic_exposure_plus_1_white_48dp,
        R.drawable.ic_sentiment_satisfied_white_48dp,
        R.color.votingPositive,
        R.color.votingPositiveSelected,
        1);
    VotingButton votePlus2 = new VotingButton(context,
        Background.Corner.RIGHT_BOTTOM,
        R.drawable.ic_exposure_plus_2_white_48dp,
        R.drawable.ic_sentiment_very_satisfied_white_48dp,
        R.color.votingPositive,
        R.color.votingPositiveSelected,
        2);
    votingButtons.addAll(
        Arrays.asList(voteMinus1, voteMinus2, votePlus1, votePlus2));

    LinearLayout votePanels = new LinearLayout(context);
    votePanels.setLayoutParams(matchLayout());
    votePanels.setOrientation(LinearLayout.VERTICAL);
    votingPanel.addView(votePanels);

    LinearLayout votePanel1 = new LinearLayout(context);
    votePanel1.setLayoutParams(matchAndWrapLayout());
    votePanel1.setOrientation(LinearLayout.HORIZONTAL);
    votePanels.addView(votePanel1);
    votePanel1.addView(voteMinus1);
    votePanel1.addView(votePlus1);

    LinearLayout votePanel2 = new LinearLayout(context);
    votePanel2.setLayoutParams(matchAndWrapLayout());
    votePanel2.setOrientation(LinearLayout.HORIZONTAL);
    votePanels.addView(votePanel2);
    votePanel2.addView(voteMinus2);
    votePanel2.addView(votePlus2);

    emoticon = new ImageView(context);
    RelativeLayout.LayoutParams emoticonParams =
        new RelativeLayout.LayoutParams(
            widgetUtil.dpToPx(110), widgetUtil.dpToPx(110));
    emoticonParams.addRule(RelativeLayout.CENTER_IN_PARENT,
        RelativeLayout.TRUE);
    emoticon.setLayoutParams(emoticonParams);
    emoticon.setImageDrawable(
        widgetUtil.getDrawable(R.drawable.ic_sentiment_neutral_white_48dp));
    emoticon.setColorFilter(
        widgetUtil.color(R.color.votingNeutral));
    votingPanel.addView(emoticon);

    setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        Rect emoticonBounds = new Rect(emoticon.getLeft(), emoticon.getTop(),
            emoticon.getRight(), emoticon.getBottom());
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
          case MotionEvent.ACTION_MOVE:
            if (applyEvent(emoticonBounds, event)) {
              emoticon.setImageDrawable(widgetUtil.getDrawable(
                  R.drawable.ic_sentiment_neutral_white_48dp));
              emoticon.setColorFilter(widgetUtil.color(R.color.votingNeutral));
              for (VotingButton b : votingButtons) {
                if (!b.isDisabled()) {
                  WidgetUtil.setBackground(b, b.getBackgroundDrawable());
                }
              }
            } else {
              boolean selected = false;
              for (VotingButton b : votingButtons) {
                if (!b.isDisabled()) {
                  if (applyEvent(b, event)) {
                    WidgetUtil.setBackground(b,
                        b.getBackgroundDrawableSelected());
                    b.applyIcon(emoticon);
                    selected = true;
                  } else {
                    WidgetUtil.setBackground(b, b.getBackgroundDrawable());
                  }
                }
                if (!selected) {
                  emoticon.setImageDrawable(widgetUtil.getDrawable(
                      R.drawable.ic_sentiment_neutral_white_48dp));
                  emoticon.setColorFilter(
                      widgetUtil.color(R.color.votingNeutral));
                }
              }
            }
            break;
          case MotionEvent.ACTION_UP:
            checkState(change != null, "Change not set");
            Integer vote = null;
            if (applyEvent(emoticonBounds, event)) {
              vote = 0;
            } else {
              for (VotingButton b : votingButtons) {
                if (!b.isDisabled() && applyEvent(b, event)) {
                  vote = b.getVote();
                }
              }
            }
            if (vote != null && onVoteListener != null) {
              onVoteListener.vote(vote);
            }
            break;
          default:
            break;
        }
        return true;
      }

      private boolean applyEvent(VotingButton b, MotionEvent event) {
        return applyEvent(b.getBounds(), event);
      }

      private boolean applyEvent(Rect bounds, MotionEvent event) {
        return bounds.contains((int) event.getX() - getPaddingLeft(),
            (int) event.getY() - getPaddingTop());
      }
    });
  }

  public void setChange(Change change) {
    this.change = change;

    Collection<String> permittedCodeReviewVotes =
        change.info.permittedLabels.get("Code-Review");

    Collection<Integer> voteableValues = permittedCodeReviewVotes != null
        ? Collections2.transform(permittedCodeReviewVotes,
          new Function<String, Integer>() {
            @Override
            public Integer apply(String value) {
              return Integer.parseInt(value.trim());
            }
          })
        : new ArrayList<Integer>();
    for (VotingButton b : votingButtons) {
      if (!voteableValues.contains(b.getVote())) {
        b.setDisabled(true);
        WidgetUtil.setBackground(b, b.getBackgroundDrawableDisabled());
      }
    }
  }

  public void select(int vote) {
    for (VotingButton b : votingButtons) {
      if (b.getVote() == vote) {
        WidgetUtil.setBackground(b, b.getBackgroundDrawableSelected());
        b.applyIcon(emoticon);
      }
    }
  }

  public void setOnVoteListener(OnVoteListener onVoteListener) {
    this.onVoteListener = onVoteListener;
  }

  private class VotingButton extends LinearLayout {
    private final Background.Corner corner;
    private final Background background;
    private final Background backgroundSelected;
    private final Background backgroundDisabled;
    private final
    @ColorRes
    int colorSelected;
    private final
    @DrawableRes
    int iconResSelected;
    private final int vote;
    private boolean disabled;

    public VotingButton(
        Context context, Background.Corner corner, @DrawableRes int iconRes,
        @DrawableRes int iconResSelected, @ColorRes int color,
        @ColorRes int colorSelected, int vote) {
      super(context);
      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT, widgetUtil.dpToPx(100));
      params.weight = 1;
      setLayoutParams(params);

      this.corner = corner;
      this.background = new Background(widgetUtil.color(color), corner);
      this.backgroundSelected = new Background(
          widgetUtil.color(colorSelected), corner);
      this.backgroundDisabled = new Background(
          widgetUtil.color(R.color.disabled), corner);
      this.colorSelected = colorSelected;
      this.iconResSelected = iconResSelected;
      this.vote = vote;

      WidgetUtil.setBackground(this, background);

      ImageView icon = new ImageView(context);
      icon.setImageDrawable(widgetUtil.getDrawable(iconRes));
      LinearLayout.LayoutParams iconParams =
          new LinearLayout.LayoutParams(
              widgetUtil.dpToPx(60), widgetUtil.dpToPx(60));
      switch (corner) {
        case LEFT_TOP:
          iconParams.topMargin = widgetUtil.dpToPx(8);
          iconParams.leftMargin = widgetUtil.dpToPx(8);
          break;
        case LEFT_BOTTOM:
          iconParams.bottomMargin = widgetUtil.dpToPx(8);
          iconParams.leftMargin = widgetUtil.dpToPx(8);
          iconParams.gravity = Gravity.BOTTOM;
          break;
        case RIGHT_TOP:
          iconParams.topMargin = widgetUtil.dpToPx(8);
          iconParams.rightMargin = widgetUtil.dpToPx(8);
          setGravity(Gravity.END);
          break;
        case RIGHT_BOTTOM:
          iconParams.bottomMargin = widgetUtil.dpToPx(8);
          iconParams.rightMargin = widgetUtil.dpToPx(8);
          iconParams.gravity = Gravity.BOTTOM;
          setGravity(Gravity.END);
          break;
        default:
          break;
      }
      icon.setLayoutParams(iconParams);
      addView(icon);
    }

    public void setDisabled(boolean disabled) {
      this.disabled = disabled;
    }

    public boolean isDisabled() {
      return disabled;
    }

    public int getVote() {
      return vote;
    }

    public Background getBackgroundDrawable() {
      return background;
    }

    public Background getBackgroundDrawableSelected() {
      return backgroundSelected;
    }

    public Background getBackgroundDrawableDisabled() {
      return backgroundDisabled;
    }

    public void applyIcon(ImageView image) {
      image.setImageDrawable(widgetUtil.getDrawable(iconResSelected));
      image.setColorFilter(widgetUtil.color(colorSelected));
    }

    public Rect getBounds() {
      if (corner == Background.Corner.LEFT_BOTTOM
          || corner == Background.Corner.RIGHT_BOTTOM) {
        return new Rect(getLeft(), getHeight() + getTop(), getRight(),
            getHeight() + getBottom());
      } else {
        return new Rect(getLeft(), getTop(), getRight(), getBottom());
      }
    }
  }

  private static class Background extends Drawable {
    public enum Corner {
      LEFT_TOP,
      LEFT_BOTTOM,
      RIGHT_TOP,
      RIGHT_BOTTOM
    }

    private final int color;
    private final Corner corner;

    public Background(int color, Corner corner) {
      this.color = color;
      this.corner = corner;
    }

    @Override
    public void draw(Canvas canvas) {
      canvas.save();

      Paint paint = new Paint();
      paint.setColor(color);
      paint.setStyle(Paint.Style.FILL);

      Rect bounds = getBounds();
      float lamda = (float) Math.toDegrees(
          Math.atan((float) (bounds.height()) / (float) (bounds.width())));
      switch (corner) {
        case LEFT_TOP:
          lamda = 180 - lamda;
          break;
        case LEFT_BOTTOM:
          // lamda = lamda;
          break;
        case RIGHT_TOP:
          lamda = 180 + lamda;
          break;
        case RIGHT_BOTTOM:
          lamda = -lamda;
          break;
        default:
          throw new IllegalStateException();
      }
      canvas.rotate(lamda, bounds.width() / 2, bounds.height() / 2);
      canvas.translate(-bounds.width() / 3, bounds.height() / 3);
      canvas.drawRect(new RectF(0, 0, bounds.width() * 2,
          bounds.height() * 2), paint);
      canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getOpacity() {
      return 0;
    }
  }

  public interface OnVoteListener {
    void vote(int vote);
  }
}
