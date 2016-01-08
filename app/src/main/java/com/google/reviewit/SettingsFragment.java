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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class SettingsFragment extends BaseFragment {

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_settings;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setHasOptionsMenu(true);

    v(R.id.serverConfig).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        display(ServerListFragment.class);
      }
    });

    v(R.id.sortConfig).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        display(SortSettingFragment.class);
      }
    });
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_settings, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_clean_avatar_cache:
        getApp().getAvatarCache().clean();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
