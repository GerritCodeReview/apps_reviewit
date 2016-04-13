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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.gerrit.extensions.client.ChangeStatus;
import com.google.reviewit.app.Change;
import com.google.reviewit.widget.ChangeEntry;
import com.google.reviewit.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class PagedChangeDetailsFragment extends BaseFragment {

  @Override
  protected @LayoutRes
  int getLayout() {
    return R.layout.content_paged_change_details;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    ((MainActivity) getActivity()).getSupportActionBar().hide();

    init();
  }

  private void init() {
    final Change change = getApp().getQueryHandler().getCurrentChange();
    checkArgument(change != null, "No change to display");

    ChangeEntry changeEntry = new ChangeEntry(getContext());
    changeEntry.init(getApp(), change);
    vg(R.id.changeHeader).addView(changeEntry);

    final List<PageFragment> fragments = new ArrayList<>();

    CommitMessageFragment commitMessageFragment = new CommitMessageFragment();
    commitMessageFragment.setChange(change);
    fragments.add(commitMessageFragment);

    FileListFragment fileListFragment = new FileListFragment();
    fileListFragment.setChange(change);
    fragments.add(fileListFragment);

    ReplyFragment replyFragment = new ReplyFragment();
    replyFragment.setChange(change);
    fragments.add(replyFragment);

    // TODO add more tabs, e.g. for change messages and approvals

    ViewPager pager = ((ViewPager) v(R.id.pager));
    pager.setAdapter(
        new FragmentPagerAdapter(getChildFragmentManager()) {
          @Override
          public Fragment getItem(int position) {
            return fragments.get(position);
          }

          @Override
          public int getCount() {
            return fragments.size();
          }

          @Override
          public CharSequence getPageTitle(int position) {
            return getString(fragments.get(position).getTitle());
          }
        });

    SlidingTabLayout tabs = ((SlidingTabLayout) v(R.id.tabs));
    tabs.setTextColor(widgetUtil.color(R.color.tab));
    tabs.setSelectedIndicatorColors(widgetUtil.color(R.color.tab));
    tabs.setBorderColor(widgetUtil.color(R.color.tab));
    tabs.setDividerColors(widgetUtil.color(R.color.tabDivider));
    tabs.setBottomBorderThickness(widgetUtil.dpToPx(1));
    tabs.setSelectedIndicatorThickness(widgetUtil.dpToPx(5));
    tabs.setViewPager(pager);

    final View more = v(R.id.more);
    more.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PopupMenu popup = new PopupMenu(getContext(), more);
        popup.getMenuInflater()
            .inflate(R.menu.menu_paged_change_details, popup.getMenu());

        popup.getMenu().findItem(R.id.action_abandon).setVisible(
            change.info.status == ChangeStatus.NEW
                || change.info.status == ChangeStatus.SUBMITTED);
        popup.getMenu().findItem(R.id.action_restore).setVisible(
            change.info.status == ChangeStatus.ABANDONED);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
              case R.id.action_abandon:
                display(AbandonFragment.create(
                    PagedChangeDetailsFragment.class));
                return true;
              case R.id.action_restore:
                display(RestoreFragment.create(
                    PagedChangeDetailsFragment.class));
                return true;
              default:
                return true;
            }
          }
        });
        popup.show();
      }
    });
  }
}
