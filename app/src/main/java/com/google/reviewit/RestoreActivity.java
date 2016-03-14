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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.Change;
import com.google.reviewit.widget.ExpandableCommitMessageView;
import com.urswolfer.gerrit.client.rest.http.HttpStatusException;

/**
 * Fragment to restore a change.
 */
public class RestoreActivity extends BaseFragment
    implements OnBackPressedAware {
  private static final String ORIGIN =
      "com.google.reviewit.RestoreActivity.ORIGIN";

  public static RestoreActivity create(Class<? extends Fragment> origin) {
    RestoreActivity fragment = new RestoreActivity();
    Bundle bundle = new Bundle();
    bundle.putSerializable(ORIGIN, origin);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_restore;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Change change = getApp().getActionHandler().getCurrentChange();
    setTitle(getString(R.string.restore_change_title, change.info._number));
    init(change);
  }

  private void init(final Change change) {
    ((ExpandableCommitMessageView)v(R.id.commitMessage)).init(change);

    v(R.id.restoreButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View v) {
        v.setEnabled(false);
        v.setBackgroundColor(widgetUtil.color(R.color.buttonDisabled));
        new AsyncTask<Change, Void, String>() {
          @Override
          protected String doInBackground(Change... changes) {
            Change change = changes[0];
            try {
              change.restore(textOf(R.id.restoreMessageInput).trim());
              return null;
            } catch (RestApiException e) {
              if (e instanceof HttpStatusException) {
                HttpStatusException se = (HttpStatusException) e;
                return getString(R.string.restore_error, se.getStatusCode(),
                    se.getStatusText());
              } else {
                return e.getMessage();
              }
            }
          }

          protected void onPostExecute(String errorMsg) {
            if (errorMsg != null) {
              v.setEnabled(true);
              v.setBackgroundColor(widgetUtil.color(R.color.button));
              widgetUtil.showError(errorMsg);
            } else {
              display(SortChangesFragment.class);
            }
          }
        }.execute(change);
      }
    });
  }

  @Override
  public boolean onBackPressed() {
    display((Class<? extends Fragment>) getArguments().get(ORIGIN));
    return true;
  }
}
