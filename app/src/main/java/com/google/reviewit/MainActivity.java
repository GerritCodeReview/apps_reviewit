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

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.reviewit.app.ReviewItBaseActivity;
import com.google.reviewit.util.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MainActivity extends ReviewItBaseActivity {
  private DrawerLayout drawer;
  private ActionBarDrawerToggle toggle;
  private ListView list;
  private List<Item> items;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .permitAll().build());

    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
    }

    drawer = (DrawerLayout) findViewById(R.id.drawer);
    list = (ListView) findViewById(R.id.mainList);

    items = new ArrayList<>();
    items.add(new Item(SortChangesFragment.class, R.string.app_menu_sort,
        R.drawable.ic_star_white_16dp, null));
    items.add(new Item(ReviewChangesFragment.class, R.string.app_menu_review,
        R.drawable.ic_feedback_white_18dp, null));
    items.add(new Item(SettingsFragment.class, R.string.app_menu_settings,
        R.drawable.ic_settings_white_18dp, null));

    list.setAdapter(new BaseAdapter() {
      @Override
      public int getCount() {
        return items.size();
      }

      @Override
      public Object getItem(int position) {
        return items.get(position);
      }

      @Override
      public long getItemId(int position) {
        return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
          convertView = ((LayoutInflater) MainActivity.this.getSystemService
              (Activity.LAYOUT_INFLATER_SERVICE)).inflate(
                  R.layout.main_list_item, null);
        }

        Item item = items.get(position);

        ((ImageView) convertView.findViewById(R.id.mainListItemIcon))
            .setImageResource(item.iconId);
        WidgetUtil.setText(convertView, R.id.mainListItemTitle,
            getString(item.titleId));

        if (item.count != null) {
          WidgetUtil.setText(convertView, R.id.mainListItemCounter,
              item.count.toString());
        } else {
          convertView.findViewById(R.id.mainListItemCounter)
              .setVisibility(View.GONE);
        }

        return convertView;
      }
    });

    toggle = new ActionBarDrawerToggle(this, drawer, R.string.app_name,
        R.string.app_name) {
      public void onDrawerClosed(View view) {
        invalidateOptionsMenu();
      }

      public void onDrawerOpened(View drawerView) {
        invalidateOptionsMenu();
      }
    };
    drawer.setDrawerListener(toggle);

    list.setOnItemClickListener(new ListView.OnItemClickListener() {
      @Override
      public void onItemClick(
          AdapterView<?> parent, View view, int position, long id) {
        displayView(position);
      }
    });

    if (getApp().getPrefs().showIntro) {
      displayView(new IntroFragment1());
    } else if (savedInstanceState == null) {
      switch (getApp().getPrefs().startScreen) {
        case REVIEW_SCREEN:
          displayView(1);
          break;
        case SORT_SCREEN:
        default:
          displayView(0);
          break;
      }
    }
  }

  private void displayView(int position) {
    if (position < items.size()) {
      Item item = items.get(position);
      displayView(newFragmentInstance(item.fragmentClass), position);
      return;
    }
    throw new IllegalStateException("view not found: " + position);
  }

  private void displayView(Fragment fragment, int position) {
    displayView(fragment);

    Item item = items.get(position);
    checkNotNull(item);
    list.setItemChecked(position, true);
    list.setSelection(position);
    setTitle(getString(item.titleId));
    drawer.closeDrawer(list);
  }

  private void displayView(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.mainFrame, fragment)
        .addToBackStack(null)
        .commit();
  }

  private Fragment newFragmentInstance(
      Class<? extends Fragment> fragmentClass) {
    try {
      return fragmentClass.newInstance();
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (InstantiationException e) {
      throw new IllegalStateException(e);
    }
  }

  public void select(Class<? extends Fragment> fragmentClass) {
    for (Item item : items) {
      if (item.fragmentClass.equals(fragmentClass)) {
        list.setItemChecked(items.indexOf(item), true);
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (toggle.onOptionsItemSelected(item)) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void setTitle(CharSequence title) {
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(title);
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    toggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    toggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    Fragment fragment = getSupportFragmentManager()
        .findFragmentById(R.id.mainFrame);
    if (fragment instanceof DispatchTouchEventAware) {
      ((DispatchTouchEventAware) fragment).dispatchTouchEvent(event);
    }

    return super.dispatchTouchEvent(event);
  }

  @Override
  public void onBackPressed() {
    Fragment fragment = getSupportFragmentManager()
        .findFragmentById(R.id.mainFrame);
    if (fragment instanceof OnBackPressedAware) {
      if (((OnBackPressedAware) fragment).onBackPressed()) {
        return;
      }
    }

    getFragmentManager().executePendingTransactions();
    int count = getFragmentManager().getBackStackEntryCount();
    if (count == 0) {
      super.onBackPressed();
    } else {
      getFragmentManager().popBackStack();
    }
  }

  private class Item {
    final Class<? extends Fragment> fragmentClass;
    final
    @StringRes
    int titleId;
    final
    @DrawableRes
    int iconId;
    Integer count;

    Item(Class<? extends Fragment> fragmentClass, @StringRes int titleId,
         @DrawableRes int iconId, Integer count) {
      this.fragmentClass = fragmentClass;
      this.titleId = titleId;
      this.iconId = iconId;
      this.count = count;
    }
  }
}
