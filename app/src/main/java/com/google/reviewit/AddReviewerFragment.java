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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.Change;
import com.google.reviewit.util.ChangeUtil;
import com.google.reviewit.widget.ChangeBox;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.WidgetUtil;
import com.urswolfer.gerrit.client.rest.http.HttpStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableRowLayout;
import static com.google.reviewit.util.LayoutUtil.wrapTableRowLayout;

/**
 * Fragment to add a reviewer to a change.
 * On typing reviewer names are auto-completed.
 */
public class AddReviewerFragment extends BaseFragment
    implements OnBackPressedAware {
  private static final String TAG = AddReviewerFragment.class.getName();
  private static final String ORIGIN =
      "com.google.reviewit.AddReviewerFragment.ORIGIN";

  public static AddReviewerFragment create(Class<? extends Fragment> origin) {
    AddReviewerFragment fragment = new AddReviewerFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(ORIGIN, origin);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_add_reviewer;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Change change = getApp().getSortActionHandler().getCurrentChange();
    initInputField(change);
    try {
      displayReviewers(change);
      displayCCs(change);
      ChangeUtil.colorBackground(root, change);
      ((ChangeBox) v(R.id.changeBox)).display(getApp(), change);
    } catch (Throwable t) {
      Log.e(TAG, "Failed to display change", t);
      display(ErrorFragment.create(t));
    }
  }

  private void initInputField(final Change change) {
    AutoCompleteTextView editText =
        (AutoCompleteTextView) v(R.id.reviewerInput);
    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEND) {
          addReviewer(change, v.getText().toString());
          handled = true;
        }
        return handled;
      }
    });
    editText.setAdapter(new SuggestReviewerAdapter(getContext()));
  }

  private void addReviewer(Change change, final String reviewerId) {
    new AsyncTask<Change, Void, String>() {
      @Override
      protected String doInBackground(Change... changes) {
        Change change = changes[0];
        try {
          change.addReviewer(reviewerId);
          return null;
        } catch (RestApiException e) {
          Log.w(TAG, "Adding reviewer failed", e);
          if (e instanceof HttpStatusException) {
            HttpStatusException se = (HttpStatusException) e;
            return getString(R.string.add_reviewer_error, se.getStatusCode(),
                se.getStatusText());
          } else {
            return e.getMessage();
          }
        }
      }

      protected void onPostExecute(String errorMsg) {
        if (errorMsg != null) {
          widgetUtil.showError(errorMsg);
        } else {
          widgetUtil.toast(R.string.reviewer_added);

          AddReviewerFragment fragment = new AddReviewerFragment();
          Bundle bundle = new Bundle();
          bundle.putAll(getArguments());
          fragment.setArguments(bundle);
          display(fragment, false);
        }
      }
    }.execute(change);
  }


  private void displayReviewers(Change change) {
    TableLayout tl = (TableLayout) v(R.id.reviewerTable);
    Collection<AccountInfo> reviewers = change.reviewers(false);
    if (!reviewers.isEmpty()) {
      for (AccountInfo reviewer : reviewers) {
        addReviewerRow(tl, reviewer);
      }
    } else {
      WidgetUtil.setText(v(R.id.reviewersLabel),
          getString(R.string.no_reviewers));
    }
  }

  private void displayCCs(Change change) {
    TableLayout tl = (TableLayout) v(R.id.reviewerTable);
    Collection<AccountInfo> ccs = change.ccs();
    if (!ccs.isEmpty()) {
      TableRow tr = new TableRow(getContext());
      tr.setLayoutParams(matchAndWrapTableRowLayout());
      TextView ccsTitle = new TextView(getContext());
      TableRow.LayoutParams params = wrapTableRowLayout(2);
      params.bottomMargin = widgetUtil.dpToPx(3);
      ccsTitle.setLayoutParams(params);
      ccsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
      ccsTitle.setText(getString(R.string.ccs));
      tr.addView(ccsTitle);
      tl.addView(tr, matchAndWrapTableLayout());
      for (AccountInfo cc : ccs) {
        addReviewerRow(tl, cc);
      }
    }
  }

  private void addReviewerRow(TableLayout tl, AccountInfo reviewer) {
    TableRow tr = new TableRow(getContext());
    tr.setLayoutParams(matchAndWrapTableRowLayout());
    ImageView avatar = new ImageView(getContext());
    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
        widgetUtil.dpToPx(20), widgetUtil.dpToPx(20));
    layoutParams.setMargins(0, 0, widgetUtil.dpToPx(5), widgetUtil.dpToPx(2));
    avatar.setLayoutParams(layoutParams);

    WidgetUtil.displayAvatar(getApp(), reviewer, avatar);
    tr.addView(avatar);
    TextView reviewerName = new TextView(getContext());
    reviewerName.setLayoutParams(wrapTableRowLayout());
    reviewerName.setText(FormatUtil.format(reviewer));
    tr.addView(reviewerName);
    tl.addView(tr, matchAndWrapTableLayout());
  }

  @Override
  public boolean onBackPressed() {
    display((Class<? extends Fragment>) getArguments().get(ORIGIN),
        getArguments(), false);
    return true;
  }

  private class SuggestReviewerAdapter extends ArrayAdapter<String>
      implements Filterable {
    private List<String> suggestions;

    public SuggestReviewerAdapter(Context context) {
      super(context, android.R.layout.simple_dropdown_item_1line);
      suggestions = Collections.emptyList();
    }

    @Override
    public int getCount() {
      return suggestions.size();
    }

    @Override
    public String getItem(int index) {
      return suggestions.get(index);
    }

    @Override
    public Filter getFilter() {
      return new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
          FilterResults filterResults = new FilterResults();
          if (constraint != null) {
            suggestions = suggest(constraint.toString());
            filterResults.values = suggestions;
            filterResults.count = suggestions.size();
          }
          return filterResults;
        }

        @Override
        protected void publishResults(
            CharSequence constraint, FilterResults results) {
          if (results != null && results.count > 0) {
            notifyDataSetChanged();
          } else {
            notifyDataSetInvalidated();
          }
        }
      };
    }

    private List<String> suggest(String text) {
      List<String> suggestions = new ArrayList<>();
      try {
        for (AccountInfo a
            : getApp().getApi()
                .accounts()
                .suggestAccounts(text)
                .withLimit(10)
                .get()) {
          suggestions.add(FormatUtil.format(a));
        }
      } catch (RestApiException e) {
        Log.e(TAG, "Suggesting reviewers failed", e);
      }
      return suggestions;
    }
  }
}
