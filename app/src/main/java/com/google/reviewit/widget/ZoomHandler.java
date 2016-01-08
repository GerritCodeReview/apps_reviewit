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

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class ZoomHandler {
  private final View view;
  private final GestureDetector gestureDetector;
  private final ScaleGestureDetector scaleDetector;

  private float scaleFactor = 1.f;
  private float onTouchStartX;
  private float onTouchStartY;
  private float translationX;
  private float translationY;

  public ZoomHandler(View v) {
    this.view = v;
    scaleDetector = new ScaleGestureDetector(v.getContext(),
        new ScaleGestureDetector.SimpleOnScaleGestureListener() {
      @Override
      public boolean onScale(ScaleGestureDetector detector) {
        scaleFactor *= detector.getScaleFactor();
        scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 4.0f));
        scale();
        return true;
      }
    });
    gestureDetector = new GestureDetector(v.getContext(),
        new GestureDetector.SimpleOnGestureListener() {
      @Override
      public boolean onDoubleTap(MotionEvent e) {
        scaleFactor = 1.0f;
        scale();
        return true;
      }
    });
  }

  private void scale() {
    view.setScaleX(scaleFactor);
    view.setScaleY(scaleFactor);
    view.setTranslationX(view.getWidth() * (scaleFactor - 1) / 2);
    view.setTranslationY(view.getHeight() * (scaleFactor - 1) / 2);
    view.invalidate();
  }

  public boolean dispatchTouchEvent(MotionEvent event) {
    scaleDetector.onTouchEvent(event);
    gestureDetector.onTouchEvent(event);

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        onTouchStartX = event.getX();
        onTouchStartY = event.getY();
        translationX = view.getTranslationX();
        translationY = view.getTranslationY();
        break;
      case MotionEvent.ACTION_MOVE:
        float maxX = view.getWidth() * (scaleFactor - 1) / 2;
        view.setTranslationX(Math.max(
            Math.min(translationX + event.getX() - onTouchStartX, maxX), -maxX));
        float maxY = view.getHeight() * (scaleFactor - 1) / 2;
        view.setTranslationY(Math.max(
            Math.min(translationY + event.getY() - onTouchStartY, maxY), -maxY));
        break;
      default:
        break;
    }

    return false;
  }
}
