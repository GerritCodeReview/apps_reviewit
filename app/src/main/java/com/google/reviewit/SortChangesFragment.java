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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gerrit.extensions.client.ChangeStatus;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.Preferences;
import com.google.reviewit.app.SortActionHandler;
import com.google.reviewit.app.Change;
import com.google.reviewit.app.ConfigManager;
import com.google.reviewit.app.QueryConfig;
import com.google.reviewit.util.ChangeUtil;
import com.google.reviewit.widget.ChangeBox;
import com.google.reviewit.util.NoOpAnimatorListener;
import com.google.reviewit.util.ObservableAsyncTask;
import com.google.reviewit.util.TaskObserver;
import com.google.reviewit.util.WidgetUtil;
import com.urswolfer.gerrit.client.rest.http.HttpStatusException;

import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setInvisible;
import static com.google.reviewit.util.WidgetUtil.setVisible;

/**
 * Fragment that presents changes one by one and lets the user decide to
 * ignore/skip/star each change.
 */
public class SortChangesFragment extends BaseFragment
    implements OnBackPressedAware {
  private static final String TAG = SortChangesFragment.class.getName();

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_sort_changes;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setTitle(getString(R.string.app_menu_sort));

    setHasOptionsMenu(true);

    if (getSortActionHandler().hasCurrentChange()) {
      getSortActionHandler().pushBack();
    }

    getApp().getPrefManager().setPreferences(
        new Preferences.Builder(getApp().getPrefManager().getPreferences())
            .setStartScreen(Preferences.StartScreen.SORT_SCREEN)
            .build());

    TaskObserver.enableProgressBar(getWindow());
    init();
    display();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }

  private void display() {
    ConfigManager cfgManager = getApp().getConfigManager();
    QueryConfig config = cfgManager.getQueryConfig();
    if (!config.isComplete()) {
      display(QuerySettingsFragment.class);
      return;
    }

    if (!isOnline()) {
      setInvisible(v(R.id.progress));
      WidgetUtil.setText(v(R.id.noResultBoxText),
          getString(R.string.no_network));
      setVisible(v(R.id.root, R.id.noResultBox));
      setGone(v(R.id.resultBox, R.id.loadingBox));
      return;
    }

    if (getSortActionHandler().isQueryNeeded()) {
      if (v(R.id.changeBox) == null) {
        setVisible(v(R.id.loadingBox));
        setInvisible(v(R.id.progress));
        disableButtons();
        showButtons(false);
      } else {
        setVisible(v(R.id.progress));
      }
    } else {
      Change change = getSortActionHandler().preview();
      if (change != null) {
        ChangeUtil.colorBackground(root, change);
      }
    }
    setVisible(v(R.id.root));

    new AsyncTask<Void, Void, ChangeData>() {
      private View progress;
      private View loadingBox;
      private ViewGroup resultBox;
      private View noResultBox;
      private TextView noResultBoxText;
      private ChangeBox changeBox;

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        progress = v(R.id.progress);
        loadingBox = v(R.id.loadingBox);
        resultBox = vg(R.id.resultBox);
        noResultBox = v(R.id.noResultBox);
        noResultBoxText = tv(R.id.noResultBoxText);
        changeBox = (ChangeBox) v(R.id.changeBox);
      }

      @Override
      protected ChangeData doInBackground(Void... v) {
        try {
          SortActionHandler actionHandler = getSortActionHandler();
          if (actionHandler.hasNext()) {
            Change change = actionHandler.next();
            int queueSize = actionHandler.getQueueSize();
            Change nextChange = null;
            if (queueSize > 0) {
              nextChange = actionHandler.preview();
            }
            return new ChangeData(change, nextChange, queueSize);
          } else {
            return ChangeData.empty();
          }
        } catch (RestApiException e) {
          // e.g. server not reachable
          Log.e(TAG, "Request failed", e);
          if (e.getCause() != null) {
            return new ChangeData(getString(R.string.error_with_cause,
                e.getMessage(), e.getCause().getMessage()));
          } else {
            return new ChangeData(e.getMessage());
          }
        }
      }

      protected void onPostExecute(ChangeData changeData) {
        if (getActivity() == null) {
          // user navigated away while we were waiting for the request
          return;
        }

        getActivity().invalidateOptionsMenu();
        setInvisible(progress);

        if (changeData.error != null) {
          noResultBoxText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
          noResultBoxText.setText(changeData.error);
          setVisible(noResultBox);
          setGone(resultBox, loadingBox);
          return;
        }

        if (changeData.change != null) {
          setVisible(resultBox);
          setGone(noResultBox, loadingBox);

          if (changeBox == null) {
            changeBox = new ChangeBox(getContext());
            resultBox.addView(changeBox);
            changeBox.display(getApp(), changeData.change);
          }
          ChangeUtil.colorBackground(root, changeData.change);

          initButtons(changeBox);
          initSwipeAnimation(changeBox);

          if (changeData.nextChange != null) {
            ChangeBox changeBox2 = new ChangeBox(getContext());
            resultBox.addView(changeBox2, resultBox.getChildCount() - 1);
            changeBox2.display(getApp(), changeData.nextChange);
          }
          pageEffect(changeData.queueSize);
        } else {
          noResultBoxText.setText(getString(R.string.no_more_changes));
          setVisible(noResultBox);
          setGone(resultBox, loadingBox);
        }
      }
    }.execute();
  }

  private void init() {
    v(R.id.reloadButton).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        reloadQuery();
      }
    });
  }

  private void initButtons(final View changeBox) {
    showButtons(true);

    setButtonBackgroundEnabled(R.id.ignoreLayout,
        SemiCircleDrawable.Direction.RIGHT);
    setButtonBackgroundEnabled(R.id.skipButton,
        SemiCircleDrawable.Direction.TOP);
    setButtonBackgroundEnabled(R.id.starLayout,
        SemiCircleDrawable.Direction.LEFT);

    v(R.id.skipButton).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        disableButtons();
        getSortActionHandler().skip();
        animate(changeBox, SortActionHandler.Action.SKIP);
      }
    });

    View.OnClickListener onStarClickListener = new View.OnClickListener() {
      public void onClick(View v) {
        disableButtons();
        getSortActionHandler().star();
        animate(changeBox, SortActionHandler.Action.STAR);
      }
    };
    v(R.id.starButton).setOnClickListener(onStarClickListener);
    v(R.id.starLayout).setOnClickListener(onStarClickListener);

    View.OnClickListener onIgnoreClickListener = new View.OnClickListener() {
      public void onClick(View v) {
        disableButtons();
        getSortActionHandler().ignore();
        animate(changeBox, SortActionHandler.Action.IGNORE);
      }
    };
    v(R.id.ignoreButton).setOnClickListener(onIgnoreClickListener);
    v(R.id.ignoreLayout).setOnClickListener(onIgnoreClickListener);

    changeBox.findViewById(R.id.changeBoxLowerPart).setOnClickListener(
        new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        display(DetailedChangeFragment.class);
      }
    });
  }

  private void disableButtons() {
    setButtonBackgroundDisabled(R.id.ignoreLayout,
        SemiCircleDrawable.Direction.RIGHT);
    setButtonBackgroundDisabled(R.id.skipButton,
        SemiCircleDrawable.Direction.TOP);
    setButtonBackgroundDisabled(R.id.starLayout,
        SemiCircleDrawable.Direction.LEFT);

    v(R.id.skipButton).setOnClickListener(null);
    v(R.id.starButton).setOnClickListener(null);
    v(R.id.starLayout).setOnClickListener(null);
    v(R.id.ignoreButton).setOnClickListener(null);
    v(R.id.ignoreLayout).setOnClickListener(null);
  }

  private void showButtons(boolean show) {
    if (show) {
      setVisible(v(R.id.skipButton, R.id.starButton, R.id.starLayout,
          R.id.ignoreButton, R.id.ignoreLayout));
    } else {
      setInvisible(v(R.id.skipButton, R.id.starButton, R.id.starLayout,
          R.id.ignoreButton, R.id.ignoreLayout));
    }
  }

  // TODO extract animation into own class
  private void initSwipeAnimation(final View changeBox) {
    final Point screenSize = getScreenSize();
    final int screenCenter = screenSize.x / 2;

    // TODO swipe animation doesn't look good on wide screens,
    // e.g. when phone is turned horizontally
    final ViewGroup resultBox = vg(R.id.resultBox);
    changeBox.findViewById(R.id.changeBoxUpperPart).setOnTouchListener(
        new View.OnTouchListener() {
      private SortActionHandler.Action action = SortActionHandler.Action.NONE;
      private int x;
      private int y;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            x = (int) event.getRawX();
            y = (int) event.getRawY();
            break;
          case MotionEvent.ACTION_MOVE:
            changeBox.setX(widgetUtil.getDimension(R.dimen
                .activity_horizontal_margin) + eventX - x);
            changeBox.setY(widgetUtil.getDimension(R.dimen
                .activity_vertical_margin) + eventY - y);

            if (eventX >= screenCenter) {
              changeBox.setRotation(
                  (float) ((eventX - screenCenter) * (Math.PI / 32)));

              if (eventX > (screenCenter + (screenCenter / 2))) {
                ((GradientDrawable) changeBox.getBackground())
                    .setColor(widgetUtil.color(R.color.commitMessageStar));
                action = SortActionHandler.Action.STAR;
              } else {
                action = SortActionHandler.Action.NONE;
                ((GradientDrawable) changeBox.getBackground())
                    .setColor(widgetUtil.color(R.color.commitMessage));
              }
            } else {
              changeBox.setRotation(
                  (float) ((eventX - screenCenter) * (Math.PI / 32)));
              if (eventX < (screenCenter / 2)) {
                ((GradientDrawable) changeBox.getBackground())
                    .setColor(widgetUtil.color(R.color.commitMessageIgnore));
                action = SortActionHandler.Action.IGNORE;
              } else {
                action = SortActionHandler.Action.NONE;
                ((GradientDrawable) changeBox.getBackground())
                    .setColor(widgetUtil.color(R.color.commitMessage));
              }
            }
            break;
          case MotionEvent.ACTION_UP:
            ((GradientDrawable) changeBox.getBackground())
                .setColor(widgetUtil.color(R.color.commitMessage));
            switch (action) {
              case STAR:
                getSortActionHandler().star();
                resultBox.removeView(changeBox);
                display();
                break;
              case IGNORE:
                getSortActionHandler().ignore();
                resultBox.removeView(changeBox);
                display();
                break;
              case SKIP:
                getSortActionHandler().skip();
                resultBox.removeView(changeBox);
                display();
                break;
              case NONE:
                WidgetUtil.setXY(changeBox,
                    widgetUtil.getDimension(R.dimen.activity_horizontal_margin),
                    widgetUtil.getDimension(R.dimen.activity_vertical_margin));
                changeBox.setRotation(0);
                break;
              default:
                throw new IllegalStateException("unknown action: " + action);
            }
            break;
          default:
            break;
        }
        return true;
      }
    });
  }

  private void animate(
      final View changeBox, final SortActionHandler.Action action) {
    final ViewGroup resultBox = vg(R.id.resultBox);
    Point screenSize = getScreenSize();
    final int screenCenter = screenSize.x / 2;
    ObjectAnimator animator;
    switch (action) {
      case STAR:
        animator = ObjectAnimator.ofFloat(changeBox, "x", screenSize.x);
        break;
      case IGNORE:
        animator = ObjectAnimator.ofFloat(changeBox, "x",
            -changeBox.getWidth());
        break;
      case SKIP:
        animator = ObjectAnimator.ofFloat(changeBox, "alpha", 0);
        break;
      case NONE:
        return;
      default:
        throw new IllegalStateException("unknown action: " + action);
    }

    animator.setDuration(1000);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        switch (action) {
          case STAR:
            changeBox.setRotation((float) (
                (changeBox.getX() + changeBox.getWidth() / 2 - screenCenter)
                    * (Math.PI / 32)));
            ((GradientDrawable) changeBox.getBackground())
                .setColor(widgetUtil.color(R.color.commitMessageStar));
            break;
          case IGNORE:
            changeBox.setRotation(
                (float) ((changeBox.getX() +
                    changeBox.getWidth() / 2 - screenCenter)
                        * (Math.PI / 32)));
            ((GradientDrawable) changeBox.getBackground())
                .setColor(widgetUtil.color(R.color.commitMessageIgnore));
            break;
          case SKIP:
          case NONE:
          default:
            break;
        }
      }
    });
    animator.addListener(new NoOpAnimatorListener() {
      @Override
      public void onAnimationEnd(Animator animation) {
        switch (action) {
          case STAR:
          case IGNORE:
          case SKIP:
            resultBox.removeView(changeBox);
            display();
          case NONE:
          default:
            break;
        }
      }
    });
    animator.start();
  }

  private Point getScreenSize() {
    Point screenSize = new Point();
    getActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);
    return screenSize;
  }

  private void pageEffect(int moreChangesCount) {
    View page = v(R.id.page);
    switch (moreChangesCount) {
      case 0:
        setGone(page);
        return;
      case 1:
        WidgetUtil.setRightMargin(page,
            widgetUtil.getDimension(R.dimen.activity_horizontal_margin) - 6);
        WidgetUtil.setBottomMargin(page,
            widgetUtil.getDimension(R.dimen.reviewit_bottom_area_height) - 6);
        setVisible(page);
        WidgetUtil.setBackground(page,
            widgetUtil.getDrawable(R.drawable.two_page_border));
        return;
      case 2:
        WidgetUtil.setRightMargin(page,
            widgetUtil.getDimension(R.dimen.activity_horizontal_margin) - 12);
        WidgetUtil.setBottomMargin(page,
            widgetUtil.getDimension(R.dimen.reviewit_bottom_area_height) - 12);
        setVisible(page);
        WidgetUtil.setBackground(page,
            widgetUtil.getDrawable(R.drawable.three_page_border));
        return;
      default:
        WidgetUtil.setRightMargin(page,
            widgetUtil.getDimension(R.dimen.activity_horizontal_margin) - 18);
        WidgetUtil.setBottomMargin(page,
            widgetUtil.getDimension(R.dimen.reviewit_bottom_area_height) - 18);
        setVisible(page);
        WidgetUtil.setBackground(page,
            widgetUtil.getDrawable(R.drawable.multi_page_border));
    }
  }

  private void setButtonBackgroundEnabled(
      @IdRes int id, SemiCircleDrawable.Direction direction) {
    setButtonBackground(id, direction, R.color.buttonFill,
        R.color.buttonBorder);
  }

  private void setButtonBackgroundDisabled(
      @IdRes int id, SemiCircleDrawable.Direction direction) {
    setButtonBackground(id, direction, R.color.buttonFillDisabled,
        R.color.buttonBorderDisabled);
  }

  private void setButtonBackground(
      @IdRes int id, SemiCircleDrawable.Direction direction,
      @ColorRes int fillColorId, @ColorRes int borderColorId) {
    WidgetUtil.setBackground(v(id), new SemiCircleDrawable(direction,
        widgetUtil.color(fillColorId), widgetUtil.color(borderColorId)));
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    if (!isAdded()) {
      super.onCreateOptionsMenu(menu, inflater);
      return;
    }
    SortActionHandler actionHandler = getSortActionHandler();
    inflater.inflate(R.menu.menu_sort_changes, menu);
    for (int i = 0; i < menu.size(); i++) {
      MenuItem item = menu.getItem(i);
      if (item.getItemId() == R.id.action_undo) {
        item.setVisible(actionHandler.undoPossible());
      } else if (item.getItemId() == R.id.action_add_reviewer) {
        item.setVisible(actionHandler.hasCurrentChange());
      } else if (item.getItemId() == R.id.action_abandon) {
        item.setVisible(actionHandler.hasCurrentChange()
            && (actionHandler.getCurrentChange().info.status
                == ChangeStatus.NEW ||
            actionHandler.getCurrentChange().info.status
                == ChangeStatus.SUBMITTED));
      } else if (item.getItemId() == R.id.action_reload_change) {
        item.setVisible(actionHandler.hasCurrentChange());
      } else if (item.getItemId() == R.id.action_restore) {
        item.setVisible(actionHandler.hasCurrentChange() && actionHandler
            .getCurrentChange().info.status == ChangeStatus.ABANDONED);
      }
    }
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_abandon:
        display(AbandonFragment.create(getClass()));
        return true;
      case R.id.action_add_reviewer:
        display(AddReviewerFragment.create(getClass()));
        return true;
      case R.id.action_help:
        display(HelpFragment.class);
        return true;
      case R.id.action_preferences:
        display(PreferencesFragment.class);
        return true;
      case R.id.action_reload_change:
        reloadChange();
        return true;
      case R.id.action_reload_query:
        reloadQuery();
        return true;
      case R.id.action_restore:
        display(RestoreFragment.create(getClass()));
        return true;
      case R.id.action_undo:
        undo();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void undo() {
    SortActionHandler actionHandler = getSortActionHandler();
    if (!actionHandler.undoPossible()) {
      return;
    }
    actionHandler.undo();
    ViewGroup resultBox = vg(R.id.resultBox);
    View changeBox = v(R.id.changeBox);
    while (changeBox != null) {
      resultBox.removeView(changeBox);
      changeBox = v(R.id.changeBox);
    }
    display();
  }

  private void reloadChange() {
    new ObservableAsyncTask<Change, Void, String>() {
      @Override
      protected String doInBackground(Change... changes) {
        Change change = changes[0];
        try {
          change.reload();
          return null;
        } catch (RestApiException e) {
          if (e instanceof HttpStatusException) {
            HttpStatusException se = (HttpStatusException) e;
            return getString(R.string.reload_error, se.getStatusCode(),
                se.getStatusText());
          } else {
            return e.getMessage();
          }
        }
      }

      @Override
      protected void postExecute(String errorMsg) {
        if (errorMsg != null) {
          widgetUtil.showError(errorMsg);
        } else {
          display(SortChangesFragment.class);
        }
      }
    }.execute(getSortActionHandler().getCurrentChange());
  }

  private void reloadQuery() {
    getSortActionHandler().reset();
    display(getClass(), false);
  }

  @Override
  public boolean onBackPressed() {
    if (getSortActionHandler().undoPossible()) {
      undo();
      return true;
    }

    return false;
  }

  private static class ChangeData {
    static ChangeData empty() {
      return new ChangeData(null, null, 0);
    }

    final Change change;
    final Change nextChange;
    final int queueSize;
    final String error;

    ChangeData(Change change, Change nextChange, int queueSize) {
      this.change = change;
      this.nextChange = nextChange;
      this.queueSize = queueSize;
      this.error = null;
    }

    ChangeData(String error) {
      this.change = null;
      this.nextChange = null;
      this.queueSize = 0;
      this.error = error;
    }
  }
}
