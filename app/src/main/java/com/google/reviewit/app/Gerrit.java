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

package com.google.reviewit.app;

import com.google.gerrit.extensions.api.GerritApi;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;

public class Gerrit {
  private com.google.gerrit.extensions.api.GerritApi api;

  Gerrit(final ConfigManager cfgManager) {
    QueryConfig queryCfg = cfgManager.getQueryConfig();
    ServerConfig serverCfg = cfgManager.getServerConfig(queryCfg.serverId);
    api = connect(serverCfg);
    cfgManager.addUpdateListener(new ConfigManager.OnUpdate() {
      @Override
      public void onUpdate(QueryConfig queryCfg) {
        ServerConfig serverCfg = cfgManager.getServerConfig(queryCfg.serverId);
        api = connect(serverCfg);
      }
    });
  }

  private GerritApi connect(ServerConfig serverCfg) {
    return new GerritRestApiFactory().create(
        new GerritAuthData.Basic(
            serverCfg.url, serverCfg.user, serverCfg.password));
  }

  public GerritApi api() {
    return api;
  }
}
