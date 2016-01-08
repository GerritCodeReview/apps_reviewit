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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.reviewit.app.ConfigManager;
import com.google.reviewit.app.ServerConfig;

import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableLayout;
import static com.google.reviewit.util.LayoutUtil.matchAndWrapTableRowLayout;
import static com.google.reviewit.util.LayoutUtil.wrapTableRowLayout;

public class ServerListFragment extends BaseFragment {

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_server_list;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    init();
    displayServerList();
  }

  private void init() {
    v(R.id.add_server).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        display(ServerSettingsFragment.create(ServerListFragment.class));
      }
    });
  }

  private void displayServerList() {
    ConfigManager cfgManager = getApp().getConfigManager();
    TableLayout tl = (TableLayout) v(R.id.serverTable);
    tl.removeAllViews();

    for (final ServerConfig cfg : cfgManager.getServers()) {
      TableRow tr = new TableRow(getContext());
      tr.setLayoutParams(matchAndWrapTableRowLayout());

      // edit icon
      tr.addView(createIcon(R.drawable.ic_create_black_24dp,
          new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          display(ServerSettingsFragment.create(
              ServerListFragment.class, cfg.id));
        }
      }));

      // delete icon
      tr.addView(createIcon(R.drawable.ic_clear_black_24dp,
          new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          removeServer(cfg);
        }
      }));

      TextView serverName = new TextView(getContext());
      serverName.setLayoutParams(wrapTableRowLayout());
      serverName.setText(cfg.name);
      tr.addView(serverName);

      tl.addView(tr, matchAndWrapTableLayout());
    }
  }

  private ImageView createIcon(
      int drawableId, View.OnClickListener onClickListener) {
    ImageView icon = new ImageView(getContext());
    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
        widgetUtil.dpToPx(20), widgetUtil.dpToPx(20));
    layoutParams.setMargins(0, 0, widgetUtil.dpToPx(5), 0);
    icon.setLayoutParams(layoutParams);
    // TODO use setBackGround(Drawable) with API 16
    icon.setBackgroundDrawable(widgetUtil.getDrawable(drawableId));
    icon.setOnClickListener(onClickListener);
    return icon;
  }

  private void removeServer(final ServerConfig cfg) {
    new AlertDialog.Builder(getContext())
        .setTitle(getString(R.string.server_remove_title))
        .setMessage(getString(R.string.server_remove_message, cfg.name))
        .setPositiveButton(android.R.string.yes,
            new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        getApp().getConfigManager().removeServer(cfg.id);
        displayServerList();
      }
    }).setNegativeButton(android.R.string.no,
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        // do nothing
      }
    }).setIcon(android.R.drawable.ic_dialog_alert).show();
  }
}
