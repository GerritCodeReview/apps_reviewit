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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.LayoutUtil;
import com.google.reviewit.util.WidgetUtil;
import com.google.reviewit.widget.SelectCodeReviewView;
import com.google.reviewit.widget.ScrollWithHeadingsView;
import com.google.reviewit.widget.UnifiedDiffView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.reviewit.util.LayoutUtil.matchLayout;
import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setInvisible;
import static com.google.reviewit.util.WidgetUtil.setVisible;

/**
 * Fragment that shows a list of unified diffs for all files of one change.
 */
public class UnifiedDiffFragment extends BaseFragment
    implements OnBackPressedAware, DispatchTouchEventAware {
  private static final String TAG = UnifiedDiffFragment.class.getName();
  private static final String PATH =
      "com.google.reviewit.UnifiedDiffFragment.PATH";

  private ScrollWithHeadingsView diffView;

  public static UnifiedDiffFragment create(String path) {
    UnifiedDiffFragment fragment = new UnifiedDiffFragment();
    Bundle bundle = new Bundle();
    bundle.putString(PATH, path);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_unified_diff;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    ((MainActivity) getActivity()).getSupportActionBar().hide();

    String path = getArguments() != null
        ? getArguments().getString(PATH)
        : null;

    Change change = getApp().getCurrentChange();
    checkState(change != null, "Change not set");
    Map<String, FileInfo> files = change.currentRevision().files;
    createDiffView();

    if (path != null) {
      setVisible(v(R.id.navigationButtons));
      initPostReviewNavPanel(change);
      FileInfo file = files.get(path);
      checkState(file != null, "File not found: " + path);
      init(path, change, files);
      displayFile(change, path, file,
          (new ArrayList<>(files.keySet())).indexOf(path) + 1, files.size());
    } else {
      setGone(v(R.id.navigationButtons));
      initPostReviewPanel(change);
      displayFiles(change, files);
    }
  }

  private void createDiffView() {
    diffView = new ScrollWithHeadingsView(getContext());
    diffView.setLayoutParams(matchLayout());
    diffView.setWindow(getWindow());
    vg(R.id.unifiedDiffRoot).addView(diffView);
  }

  private void removeDiffView() {
    vg(R.id.unifiedDiffRoot).removeAllViews();
  }

  @Override
  public void dispatchTouchEvent(MotionEvent event) {
    diffView.getZoomHandler().dispatchTouchEvent(event);
  }

  private void initPostReviewPanel(Change change) {
    initPostReview(change, R.id.postReview);

    setVisible(v(R.id.postReviewPanel));

    v(R.id.expandPostReviewPanel).setOnClickListener(
        new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setGone(v(R.id.expandPostReviewPanel));
        setVisible(v(R.id.collapsePostReviewPanel, R.id.postReview));
      }
    });

    v(R.id.collapsePostReviewPanel).setOnClickListener(
        new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setGone(v(R.id.collapsePostReviewPanel, R.id.postReview));
        setVisible(v(R.id.expandPostReviewPanel));
      }
    });
  }

  private void initPostReviewNavPanel(Change change) {
    initPostReview(change, R.id.postReviewNav);

    v(R.id.expandPostReviewPanelNav).setOnClickListener(
        new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setGone(v(R.id.expandPostReviewPanelNav));
        setVisible(v(R.id.collapsePostReviewPanelNav, R.id.postReviewNav));
      }
    });

    v(R.id.collapsePostReviewPanelNav).setOnClickListener(
        new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setGone(v(R.id.collapsePostReviewPanelNav, R.id.postReviewNav));
        setVisible(v(R.id.expandPostReviewPanelNav));
      }
    });
  }

  private void initPostReview(Change change, @IdRes int id) {
    SelectCodeReviewView postReview = ((SelectCodeReviewView) v(id));
    postReview.setChange(change);
    postReview.select(change.getVote("Code-Review", getApp().getSelf()));
    postReview.setOnVoteListener(new SelectCodeReviewView.OnVoteListener() {
      @Override
      public void vote(int vote) {
        display(PostReviewFragment.create(vote));
      }
    });
  }

  private void init(String path, Change change, Map<String, FileInfo> files) {
    String prevPath = null;
    String nextPath = null;
    List<String> paths = new ArrayList<>(files.keySet());
    int i = paths.indexOf(path);
    if (i > 0) {
      prevPath = paths.get(i - 1);
    }
    if (i < paths.size() - 1) {
      nextPath = paths.get(i + 1);
    }
    diffView.setNextBackgroundColor(i);

    if (prevPath != null || nextPath != null) {
      if (prevPath != null) {
        setVisible(v(R.id.navigationPrev));
        WidgetUtil.setText(v(R.id.navigationPrevFile),
            new File(prevPath).getName());
        setNavigationOnClickListener(R.id.navigationPrev, change, prevPath,
            i, files);
      } else {
        setInvisible(v(R.id.navigationPrev));
      }
      if (nextPath != null) {
        setVisible(v(R.id.navigationNext));
        WidgetUtil.setText(v(R.id.navigationNextFile),
            new File(nextPath).getName());
        setNavigationOnClickListener(R.id.navigationNext, change, nextPath,
            i + 2, files);
      } else {
        setInvisible(v(R.id.navigationNext));
      }
    }
  }

  private void setNavigationOnClickListener(
      @IdRes int id, final Change change,
      final String path, final int fileNumber,
      final Map<String, FileInfo> files) {
    v(id).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        removeDiffView();
        createDiffView();
        init(path, change, files);
        displayFile(change, path, files.get(path), fileNumber, files.size());
      }
    });
  }

  private void displayFile(Change change, String path, FileInfo file,
                           int fileNumber, int totalFileCount) {
    Map<String, FileInfo> files = new HashMap<>();
    files.put(path, file);
    displayFiles(change, files, fileNumber, totalFileCount);
  }

  private void displayFiles(Change change, Map<String, FileInfo> files) {
    displayFiles(change, files, 1, files.size());
  }

  private void displayFiles(final Change change,
                            final Map<String, FileInfo> files,
                            final int startFileNumber,
                            final int totalFileCount) {
    final Iterator<Map.Entry<String, FileInfo>> fileEntryIt =
        files.entrySet().iterator();
    diffView.setContent(new Iterator<ScrollWithHeadingsView.Content>() {
      private int currentFileNumber = startFileNumber;

      @Override
      public boolean hasNext() {
        return fileEntryIt.hasNext();
      }

      @Override
      public ScrollWithHeadingsView.Content next() {
        Map.Entry<String, FileInfo> fileEntry = fileEntryIt.next();
        File f = new File(fileEntry.getKey());
        return new ScrollWithHeadingsView.Content(
            new ScrollWithHeadingsView.HeadingWithDetails(
                getContext(), f.getName(),
                new ScrollWithHeadingsView.RightAlignedHeadingDetails(
                    getContext().getString(R.string.file_count,
                        currentFileNumber++, totalFileCount)),
                new ScrollWithHeadingsView.CenteredHeadingDetails(
                    FormatUtil.ensureSlash(f.getParent()))),
            createContent(change, fileEntry.getKey(),
                fileEntry.getValue()));
      }

      public View createContent(
          final Change change, final String path, FileInfo file) {
        final LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, widgetUtil.dpToPx(400)));
        layout.setPadding(0, widgetUtil.dpToPx(30), 0, widgetUtil.dpToPx(30));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        final ProgressBar p = new ProgressBar(getContext());
        p.setLayoutParams(matchLayout());
        p.setIndeterminate(true);
        p.getIndeterminateDrawable().setColorFilter(
            widgetUtil.color(R.color.progressReload),
            android.graphics.PorterDuff.Mode.SRC_IN);
        layout.addView(p);

        if (file.binary != null && file.binary) {
          // TODO
          return layout;
        }

        LayoutUtil.addOneTimeOnGlobalLayoutListener(
            p, new LayoutUtil.OneTimeOnGlobalLayoutListener() {
          @Override
          public void onFirstGlobalLayout() {
            new AsyncTask<String, Void, DiffInfo>() {
              @Override
              protected DiffInfo doInBackground(String... paths) {
                String path = paths[0];
                try {
                  return change.diff(path);
                } catch (RestApiException e) {
                  Log.e(TAG, "Failed to get diff of file " + path
                      + " from change " + change.info._number, e);
                  return null;
                }
              }

              protected void onPostExecute(final DiffInfo diff) {
                layout.removeView(p);
                layout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.setPadding(0,
                    layout.getPaddingTop() - widgetUtil.dpToPx(20),
                    0,
                    layout.getPaddingBottom() - widgetUtil.dpToPx(20));
                UnifiedDiffView unifiedDiffView = new UnifiedDiffView
                    (getContext(), diff);
                layout.addView(unifiedDiffView);
                LayoutUtil.addOneTimeOnGlobalLayoutListener(
                    unifiedDiffView,
                    new LayoutUtil.OneTimeOnGlobalLayoutListener() {
                      @Override
                      public void onFirstGlobalLayout() {
                        diffView.relayout();
                      }
                    });
                unifiedDiffView.setOnSizeChangedListener(
                    new UnifiedDiffView.OnSizeChangedListener() {
                  @Override
                  public void onSizeChanged() {
                    diffView.relayout();
                  }
                });
              }
            }.executeOnExecutor(getApp().getExecutor(), path);
          }
        });

        return layout;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    });
  }

  @Override
  public boolean onBackPressed() {
    // TODO abort background tasks
    return false;
  }
}
