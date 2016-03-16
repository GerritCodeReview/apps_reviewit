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

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.reviewit.app.SortActionHandler;
import com.google.reviewit.app.ReviewItApp;
import com.google.reviewit.util.TaskObserver;
import com.google.reviewit.util.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragment extends Fragment {
  protected View root;
  protected WidgetUtil widgetUtil;

  @Override
  public final View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    widgetUtil = new WidgetUtil(getActivity());
    reset(container);
    root = inflater.inflate(getLayout(), container, false);
    return root;
  }

  private void reset(ViewGroup container) {
    container.removeAllViews();

    MainActivity activity = ((MainActivity) getActivity());
    activity.select(getClass());

    if (activity.getSupportActionBar() != null) {
      activity.getSupportActionBar().show();
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setStatusBarColor(widgetUtil.color(R.color.black));
    }

    // hide keyboard if it is open
    View view = getActivity().getCurrentFocus();
    if (view != null) {
      ((InputMethodManager) getActivity().getSystemService(
          Context.INPUT_METHOD_SERVICE))
              .hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    TaskObserver.clear();
  }

  protected abstract @LayoutRes int getLayout();

  protected View v(@IdRes int id) {
    return root.findViewById(id);
  }

  protected View[] v(@IdRes int id, @IdRes int... moreIds) {
    List<View> views = new ArrayList<>(moreIds.length + 1);
    views.add(v(id));
    for (@IdRes int yaId : moreIds) {
      views.add(v(yaId));
    }
    return views.toArray(new View[views.size()]);
  }

  protected ViewGroup vg(@IdRes int id) {
    return (ViewGroup) root.findViewById(id);
  }

  protected TextView tv(@IdRes int id) {
    return (TextView) root.findViewById(id);
  }

  protected String textOf(@IdRes int id) {
    return tv(id).getText().toString();
  }

  protected void setTitle(String title) {
    getActivity().setTitle(title);
  }

  @Override
  public Context getContext() {
    return root.getContext();
  }

  protected Window getWindow() {
    return getActivity().getWindow();
  }

  protected ReviewItApp getApp() {
    return ((ReviewItApp) getActivity().getApplication());
  }

  protected SortActionHandler getSortActionHandler() {
    return getApp().getSortActionHandler();
  }

  public void display(
      Class<? extends Fragment> fragmentClass, Bundle bundle,
      boolean addtoBackStack) {
    try {
      Fragment f = fragmentClass.newInstance();
      if (bundle != null) {
        f.setArguments(bundle);
      }
      display(f, addtoBackStack);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (java.lang.InstantiationException e) {
      throw new IllegalStateException(e);
    }
  }

  public void display(Class<? extends Fragment> fragmentClass) {
    display(fragmentClass, null, true);
  }

  public void display(
      Class<? extends Fragment> fragmentClass, boolean addToBackStack) {
    display(fragmentClass, null, addToBackStack);
  }

  public void display(Fragment fragment) {
    display(fragment, true);
  }

  public void display(Fragment fragment, boolean addToBackStack) {
    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction t = fragmentManager.beginTransaction()
        .replace(R.id.mainFrame, fragment);

    if (addToBackStack) {
      t.addToBackStack(null);
    }

    t.commit();
  }

  protected boolean isOnline() {
    ConnectivityManager cm =
        (ConnectivityManager) getActivity()
            .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnected();
  }
}
