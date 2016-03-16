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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.common.base.Strings;
import com.google.reviewit.app.QueryConfig;
import com.google.reviewit.app.ServerConfig;
import com.google.reviewit.util.WidgetUtil;

public class QuerySettingsFragment extends BaseFragment {
  private static final String LABEL_REGEXP = "^[a-zA-Z][a-zA-Z0-9]*$";

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_query_settings;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    init();
    displayQueryConfig(getApp().getConfigManager().getQueryConfig());
  }

  private void init() {
    v(R.id.edit_server).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        display(ServerListFragment.class);
      }
    });

    v(R.id.saveQuerySettings).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!isQueryInputComplete()) {
          widgetUtil.showError(R.string.incompleteInput);
          return;
        }

        // TODO check that query is valid

        if (!isLabelValid()) {
          widgetUtil.showError(getString(R.string.invalidLabel, LABEL_REGEXP));
          return;
        }

        saveQuerySettings();
      }
    });
  }

  private void displayQueryConfig(QueryConfig config) {
    initServerDropDown();

    v(R.id.incompleteSettings).setVisibility(
        config.isComplete()
            ? View.GONE
            : View.VISIBLE);

    Spinner serverInput = (Spinner) v(R.id.serverIdInput);
    ServerConfig serverConfig = getApp().getConfigManager().getServerConfig
        (config.serverId);
    serverInput.setSelection(
        ((ArrayAdapter<ServerConfig>) serverInput.getAdapter())
            .getPosition(serverConfig));
    WidgetUtil.setText(v(R.id.queryInput), config.query);
    WidgetUtil.setText(v(R.id.labelInput), config.label);
  }

  private void initServerDropDown() {
    Spinner serverInput = (Spinner) v(R.id.serverIdInput);
    ArrayAdapter<ServerConfig> adapter =
        new ArrayAdapter<>(getContext(),
            android.R.layout.simple_spinner_dropdown_item,
            getApp().getConfigManager().getServers());
    serverInput.setAdapter(adapter);
  }

  private boolean isQueryInputComplete() {
    return !Strings.isNullOrEmpty(getServerId())
        && !Strings.isNullOrEmpty(textOf(R.id.queryInput))
        && !Strings.isNullOrEmpty(textOf(R.id.labelInput));
  }

  private boolean isLabelValid() {
    return textOf(R.id.labelInput).matches(LABEL_REGEXP);
  }

  private void saveQuerySettings() {
    getApp().getConfigManager().setQueryConfig(
        new QueryConfig.Builder()
            .setServerId(getServerId())
            .setQuery(textOf(R.id.queryInput))
            .setLabel(textOf(R.id.labelInput))
            .build());
    display(SortChangesFragment.class);
  }

  private String getServerId() {
    Spinner serverInput = (Spinner) v(R.id.serverIdInput);
    return ((ServerConfig) serverInput.getSelectedItem()).id;
  }
}
