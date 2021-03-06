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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.gerrit.extensions.client.ChangeStatus;
import com.google.reviewit.app.SortActionHandler;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.ChangeUtil;
import com.google.reviewit.util.Linkifier;
import com.google.reviewit.widget.ChangeBox;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.TaskObserver;
import com.google.reviewit.util.WidgetUtil;
import com.google.reviewit.widget.ApprovalsView;
import com.google.reviewit.widget.FileBox;
import com.google.reviewit.widget.ZoomHandler;

import static com.google.reviewit.util.WidgetUtil.setVisible;

/**
 * Fragment to show details of a change.
 */
public class DetailedChangeFragment extends BaseFragment implements
    OnBackPressedAware, DispatchTouchEventAware {
  private static final String TAG = DetailedChangeFragment.class.getName();

  private ZoomHandler zoomHandler;

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_detailed_change;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Change change = getApp().getSortActionHandler().getCurrentChange();
    setTitle(getString(R.string.detailed_change_title, change.info._number));
    setHasOptionsMenu(true);
    init(change);
    zoomHandler = new ZoomHandler(v(R.id.scrollContent));
    TaskObserver.enableProgressBar(getWindow());

    display(change);
  }

  private void init(final Change change) {
    SwipeRefreshLayout swipeRefreshLayout =
        (SwipeRefreshLayout) v(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeColors(R.color.progressBar);
    swipeRefreshLayout.setRefreshing(true);
    swipeRefreshLayout.setOnRefreshListener(
        new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
            refresh(change);
          }
        });
  }

  private void display(Change change) {
    try {
      ChangeUtil.colorBackground(root, change);
      ((ChangeBox) v(R.id.changeBox)).display(getApp(), change);
      (new Linkifier(getApp())).linkifyCommitMessage(tv(R.id.commitMessage));
      displayChangeUrl(change);
      ((ApprovalsView) v(R.id.approvals)).displayApprovals(
          getApp(), change, this);
      ((FileBox)v(R.id.fileBox)).display(this, change);
      // TODO show further change info, e.g. summary comments, hashtags,
      // related changes

      ((SwipeRefreshLayout) v(R.id.swipeRefreshLayout)).setRefreshing(false);
    } catch (Throwable t) {
      Log.e(TAG, "Failed to display change", t);
      display(ErrorFragment.create(t));
    }
  }

  @Override
  public void dispatchTouchEvent(MotionEvent event) {
    zoomHandler.dispatchTouchEvent(event);
  }

  private void displayChangeUrl(Change change) {
    WidgetUtil.setText(v(R.id.changeUrl), change.getUrl(getServerUrl()));
    setVisible(v(R.id.changeUrlBox));
  }

  private String getServerUrl() {
    String serverUrl = getApp().getConfigManager().getServerConfig().url;
    return FormatUtil.ensureSlash(serverUrl);
  }

  private void refresh(final Change change) {
    new AsyncTask<Void, Void, Change>() {
      @Override
      protected Change doInBackground(Void... v) {
        try {
          change.reload();
          return change;
        } catch (RestApiException e) {
          // e.g. server not reachable
          Log.e(TAG, "Reload failed", e);
          return null;
        }
      }

      @Override
      protected void onPostExecute(Change change) {
        if (change != null) {
          ((ApprovalsView) v(R.id.approvals)).clear();
          ((FileBox) v(R.id.fileBox)).clear();
          display(change);
        }
      }
    }.execute();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    SortActionHandler actionHandler = getApp().getSortActionHandler();
    inflater.inflate(R.menu.menu_detailed_change, menu);
    for (int i = 0; i < menu.size(); i++) {
      MenuItem item = menu.getItem(i);
      if (item.getItemId() == R.id.action_abandon) {
        item.setVisible(actionHandler.hasCurrentChange()
            && (actionHandler.getCurrentChange().info.status
                  == ChangeStatus.NEW
              || actionHandler.getCurrentChange().info.status
                  == ChangeStatus.SUBMITTED));
      } else if (item.getItemId() == R.id.action_restore) {
        item.setVisible(actionHandler.hasCurrentChange()
            && actionHandler.getCurrentChange().info.status
                == ChangeStatus.ABANDONED);
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    SortActionHandler actionHandler = getApp().getSortActionHandler();
    switch (item.getItemId()) {
      case R.id.action_add_reviewer:
        display(AddReviewerFragment.create(getClass()));
        return true;
      case R.id.action_abandon:
        display(AbandonFragment.create(getClass()));
        return true;
      case R.id.action_help:
        display(HelpFragment.class);
        return true;
      case R.id.action_ignore:
        actionHandler.ignore();
        widgetUtil.toast(R.string.change_ignored);
        startActivity(new Intent(getActivity(), MainActivity.class));
        return true;
      case R.id.action_restore:
        display(RestoreFragment.create(getClass()));
        return true;
      case R.id.action_skip:
        actionHandler.skip();
        widgetUtil.toast(R.string.change_skipped);
        startActivity(new Intent(getActivity(), MainActivity.class));
        return true;
      case R.id.action_star:
        actionHandler.star();
        widgetUtil.toast(R.string.change_starred);
        startActivity(new Intent(getActivity(), MainActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onBackPressed() {
    display(SortChangesFragment.class);
    return true;
  }
}
