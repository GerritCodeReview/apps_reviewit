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

import android.view.View;
import android.view.Window;

import com.google.reviewit.R;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskObserver {
  private final AtomicInteger runningTasks = new AtomicInteger(0);
  private OnChangeListener listener;

  private static TaskObserver INSTANCE = new TaskObserver();

  public static TaskObserver get() {
    return INSTANCE;
  }

  public static void enableProgressBar(final Window window) {
    get().setOnChangeListener(new TaskObserver.OnChangeListener() {
      @Override
      public void onChange(int running) {
        if (running > 0) {
          window.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        } else {
          window.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        }
      }
    });
  }

  public static void clear() {
    get().setOnChangeListener(null);
  }

  public void taskStarted() {
    int running = runningTasks.addAndGet(1);
    if (listener != null) {
      listener.onChange(running);
    }
  }

  public void taskDone() {
    int running = runningTasks.addAndGet(-1);
    if (listener != null) {
      listener.onChange(running);
    }
  }

  public void setOnChangeListener(OnChangeListener onChangeListener) {
    listener = onChangeListener;
  }

  public interface OnChangeListener {
    void onChange(int running);
  }
}
