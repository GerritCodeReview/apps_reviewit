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

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.google.reviewit.util.WidgetUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Fragment to display an application error.
 */
public class ErrorFragment extends BaseFragment {
  private static final String STACKTRACE
      = "com.google.reviewit.ErrorFragment.STACKTRACE";

  public static ErrorFragment create(Throwable t) {
    ErrorFragment fragment = new ErrorFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(STACKTRACE, getStacktrace(t));
    fragment.setArguments(bundle);
    return fragment;
  }

  private static String getStacktrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_error;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    WidgetUtil.setText(v(R.id.error), getArguments().getString(STACKTRACE));
  }
}
