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
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.Change;
import com.google.reviewit.app.QueryHandler;
import com.google.reviewit.util.ObservableAsynTask;
import com.google.reviewit.util.TaskObserver;
import com.google.reviewit.widget.ChangeEntry;

import java.util.Collections;
import java.util.List;

import static com.google.reviewit.util.LayoutUtil.matchAndFixedLayout;
import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setInvisible;
import static com.google.reviewit.util.WidgetUtil.setVisible;

public class ReviewChangesFragment extends BaseFragment {
  private static final String TAG = ReviewChangesFragment.class.getName();

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_review_changes;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setHasOptionsMenu(true);

    init();
    display();
  }

  private void init() {
    v(R.id.reloadButton).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        reloadQuery();
      }
    });

    // TODO detect when scrolled to the end and automatically load next page
  }

  private void display() {
    if (!isOnline()) {
      setInvisible(v(R.id.progress));
      setGone(v(R.id.initialProgress));
      setVisible(v(R.id.statusText, R.id.reloadButton));
      TextView statusText = tv(R.id.statusText);
      statusText.setText(getString(R.string.no_network));
      statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
      return;
    }

    new ObservableAsynTask<Void, Void, ChangeListData>() {
      private View progress;
      private View initialProgress;
      private View reloadButton;
      private TextView statusText;
      private ViewGroup changeList;

      @Override
      protected void preExecute() {
        super.preExecute();
        progress = v(R.id.progress);
        initialProgress = v(R.id.initialProgress);
        reloadButton = v(R.id.reloadButton);
        statusText = tv(R.id.statusText);
        changeList = vg(R.id.changeList);
      }

      @Override
      protected ChangeListData doInBackground(Void... v) {
        try {
          QueryHandler queryHandler = getApp().getQueryHandler();
          if (queryHandler.hasNext()) {
            return new ChangeListData(queryHandler.next());
          } else {
            return new ChangeListData(Collections.<Change>emptyList());
          }
        } catch (RestApiException e) {
          // e.g. server not reachable
          Log.e(TAG, "Request failed", e);
          if (e.getCause() != null) {
            return new ChangeListData(getString(R.string.error_with_cause,
                e.getMessage(), e.getCause().getMessage()));
          } else {
            return new ChangeListData(e.getMessage());
          }
        }
      }

      protected void postExecute(ChangeListData changeListData) {
        super.postExecute(changeListData);

        if (getActivity() == null) {
          // user navigated away while we were waiting for the request
          return;
        }

        getActivity().invalidateOptionsMenu();
        setInvisible(progress);
        setGone(reloadButton);
        if (initialProgress.getVisibility() != View.GONE) {
          setGone(initialProgress);
          TaskObserver.enableProgressBar(getWindow());
        }

        if (changeListData.error != null) {
          statusText.setText(changeListData.error);
          return;
        }

        if (!changeListData.changeList.isEmpty()) {
          setGone(statusText);
          for (Change change : changeListData.changeList) {
            ChangeEntry changeEntry = new ChangeEntry(getContext());
            changeEntry.init(getApp(), change);
            changeList.addView(changeEntry);
            addSeparator(changeList);
          }
        } else {
          statusText.setText(getString(R.string.no_changes_match));
        }
      }
    }.execute();
  }

  private void addSeparator(ViewGroup viewGroup) {
    View separator = new View(getContext());
    separator.setLayoutParams(
        matchAndFixedLayout(widgetUtil.dpToPx(1)));
    separator.setBackgroundColor(widgetUtil.color(R.color.separator));
    viewGroup.addView(separator);
  }

  private void reloadQuery() {
    getApp().getQueryHandler().reset();
    display(getClass(), false);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_review_changes, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_reload_query:
        reloadQuery();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private static class ChangeListData {
    final List<Change> changeList;
    final String error;

    ChangeListData(List<Change> changeList) {
      this.changeList = changeList;
      this.error = null;
    }

    ChangeListData(String error) {
      this.changeList = null;
      this.error = error;
    }
  }
}
