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

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Drawable for semi circles.
 */
public class SemiCircleDrawable extends Drawable {
  public enum Direction {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM
  }

  private Direction angle;
  private int color;
  private int borderColor;
  private Paint paint;
  private RectF rectF;

  public SemiCircleDrawable(Direction angle, int color, int borderColor) {
    this.angle = angle;
    this.color = color;
    this.borderColor = borderColor;
    this.paint = new Paint();
    this.rectF = new RectF();
  }

  @Override
  public void draw(Canvas canvas) {
    canvas.save();

    Rect bounds = getBounds();
    if (angle == Direction.LEFT || angle == Direction.RIGHT) {
      canvas.scale(2, 1);
      if (angle == Direction.RIGHT) {
        canvas.translate(-(bounds.right / 2), 0);
      }
    } else {
      canvas.scale(1, 2);
      if (angle == Direction.BOTTOM) {
        canvas.translate(0, -(bounds.bottom / 2));
      }
    }

    rectF.set(bounds);
    drawArc(canvas, color, Paint.Style.FILL);
    drawArc(canvas, borderColor, Paint.Style.STROKE);
  }

  private void drawArc(Canvas canvas, int color, Paint.Style style) {
    paint.setColor(color);
    paint.setStyle(style);
    if (angle == Direction.LEFT) {
      canvas.drawArc(rectF, 90, 180, true, paint);
    } else if (angle == Direction.TOP) {
      canvas.drawArc(rectF, -180, 180, true, paint);
    } else if (angle == Direction.RIGHT) {
      canvas.drawArc(rectF, 270, 180, true, paint);
    } else if (angle == Direction.BOTTOM) {
      canvas.drawArc(rectF, 0, 180, true, paint);
    }
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
