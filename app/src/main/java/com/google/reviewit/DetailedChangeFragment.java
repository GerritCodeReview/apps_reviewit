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
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gerrit.extensions.client.ChangeStatus;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.reviewit.app.SortActionHandler;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.ChangeUtil;
import com.google.reviewit.widget.ChangeBox;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.TaskObserver;
import com.google.reviewit.util.WidgetUtil;
import com.google.reviewit.widget.ApprovalsView;
import com.google.reviewit.widget.ZoomHandler;

import java.util.Map;
import java.util.regex.Pattern;

import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableRowLayout;
import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setVisible;

/**
 * Fragment to show details of a change.
 */
public class DetailedChangeFragment extends BaseFragment implements
    OnBackPressedAware, DispatchTouchEventAware {
  private static final String TAG = DetailedChangeFragment.class.getName();

  private static final int PAGE_SIZE = 10;
  private static final Pattern PATTERN_CHANGE_ID =
      Pattern.compile("I[0-9a-f]{5,40}");
  private static final String PART_LINK = "(?:"
      + "[a-zA-Z0-9$_+!*'%;:@=?#/~-]"
      + "|&(?!lt;|gt;)"
      + "|[.,](?!(?:\\s|$))"
      + ")";
  private static final Pattern PATTERN_LINK = Pattern.compile(
      "https?://"
      + PART_LINK + "{2,}"
      + "(?:[(]" + PART_LINK + "*" + "[)])*"
      + PART_LINK + "*");
  private static final Pattern PATTERN_EMAIL =
      Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
          Pattern.CASE_INSENSITIVE);


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
    init();
    zoomHandler = new ZoomHandler(v(R.id.scrollContent));
    TaskObserver.enableProgressBar(getWindow());

    try {
      ChangeUtil.colorBackground(root, change);
      ((ChangeBox) v(R.id.changeBox)).display(getApp(), change);
      linkify();
      displayChangeUrl(change);
      ((ApprovalsView) v(R.id.approvals)).displayApprovals(getApp(),
          change.info, this);
      displayFiles(change, 1, false);
      // TODO show further change info, e.g. summary comments, hashtags,
      // related changes
    } catch (Throwable t) {
      Log.e(TAG, "Failed to display change", t);
      display(ErrorFragment.create(t));
    }
  }

  @Override
  public void dispatchTouchEvent(MotionEvent event) {
    zoomHandler.dispatchTouchEvent(event);
  }

  private void init() {
    TextView commitMsg = (TextView) v(R.id.commitMessage);
    commitMsg.setLinksClickable(true);

    v(R.id.reviewButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        display(UnifiedDiffFragment.class);
      }
    });

    WidgetUtil.underline(tv(R.id.showMore));
    WidgetUtil.underline(tv(R.id.showAll));
  }

  private void linkify() {
    TextView commitMsg = tv(R.id.commitMessage);
    Linkify.addLinks(commitMsg, PATTERN_CHANGE_ID, getServerUrl() + "#/q/");
    Linkify.addLinks(commitMsg, PATTERN_LINK, "");
    Linkify.addLinks(commitMsg, PATTERN_EMAIL, "");
  }

  private void displayChangeUrl(Change change) {
    WidgetUtil.setText(v(R.id.changeUrl), change.getUrl(getServerUrl()));
    setVisible(v(R.id.changeUrlBox));
  }

  private String getServerUrl() {
    String serverUrl = getApp().getConfigManager().getServerConfig().url;
    return FormatUtil.ensureSlash(serverUrl);
  }

  private void displayFiles(
      final Change change, final int page, boolean showAll) {
    TableLayout tl = (TableLayout) v(R.id.filesTable);
    Map<String, FileInfo> files = change.currentRevision().files;
    int count = 0;
    for (Map.Entry<String, FileInfo> e : files.entrySet()) {
      count++;
      if (count <= (page - 1) * PAGE_SIZE) {
        continue;
      }
      if (!showAll && count > page * PAGE_SIZE) {
        break;
      }
      addFileRow(tl, e.getKey(), e.getValue());
    }
    if (!showAll && files.size() > page * PAGE_SIZE) {
      WidgetUtil.setText(v(R.id.showAll),
          getString(R.string.show_all, files.size() - page * PAGE_SIZE));
      setVisible(v(R.id.fileButtons));
      v(R.id.showAll).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          displayFiles(change, page + 1, true);
        }
      });
      if (files.size() > (page + 1) * PAGE_SIZE) {
        WidgetUtil.setText(v(R.id.showMore),
            getString(R.string.show_more, PAGE_SIZE));
        setVisible(v(R.id.showMoreArea, R.id.showMore));
        v(R.id.showMore).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            displayFiles(change, page + 1, false);
          }
        });
      } else {
        setGone(v(R.id.showMoreArea, R.id.showMore));
      }
    } else {
      setGone(v(R.id.fileButtons));
    }
  }

  private void addFileRow(TableLayout tl, final String path, FileInfo file) {
    TableRow tr = new TableRow(getActivity());
    tr.setLayoutParams(matchAndWrapTableRowLayout());

    tr.addView(widgetUtil.tableRowRightMargin(widgetUtil.createTextView(
        file.status != null ? Character.toString(file.status) : "M", 11), 4));

    TextView pathText = widgetUtil.createTextView(path, 11);
    pathText.setTextColor(widgetUtil.color(R.color.hyperlink));
    pathText.setPaintFlags(
        pathText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    pathText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        display(UnifiedDiffFragment.create(path));
      }
    });
    tr.addView(widgetUtil.tableRowRightMargin(pathText, 4));

    tr.addView(widgetUtil.createTextView(
        FormatUtil.formatBytes(file.size), 11));

    // TODO show further file infos

    tl.addView(tr, matchAndWrapTableLayout());
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
        startActivity(new Intent(getActivity(), MainActivity.class));
        return true;
      case R.id.action_restore:
        display(RestoreFragment.create(getClass()));
        return true;
      case R.id.action_skip:
        actionHandler.skip();
        startActivity(new Intent(getActivity(), MainActivity.class));
        return true;
      case R.id.action_star:
        actionHandler.star();
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
