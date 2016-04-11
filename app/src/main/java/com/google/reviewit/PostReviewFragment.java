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
import android.view.WindowManager;

import com.google.reviewit.app.Change;
import com.google.reviewit.util.TaskObserver;
import com.google.reviewit.widget.PostReviewView;

/**
 * Fragment to post a review
 */
public class PostReviewFragment extends BaseFragment {
  private static final String VOTE =
      "com.google.reviewit.PostReviewFragment.VOTE";

  public static PostReviewFragment create(int vote) {
    PostReviewFragment fragment = new PostReviewFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(VOTE, vote);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_post_review;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    TaskObserver.enableProgressBar(getWindow());

    // do not show keyboard by default
    getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    int vote = getArguments().getInt(VOTE);
    Change change = getApp().getCurrentChange();

    setTitle(getString(R.string.detailed_change_title, change.info._number));
    ((PostReviewView)v(R.id.postReview)).init(getApp(), this, vote,
        SortChangesFragment.class);
  }
}
