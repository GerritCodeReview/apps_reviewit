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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gerrit.extensions.api.GerritApi;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import com.urswolfer.gerrit.client.rest.http.HttpClientBuilderExtension;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class Gerrit {
  private final Context context;
  private com.google.gerrit.extensions.api.GerritApi api;

  Gerrit(Context context, final ConfigManager cfgManager) {
    this.context = context;
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
            serverCfg.url, serverCfg.user, serverCfg.password),
        new HttpClientBuilderExtension() {
          @Override
          public HttpClientBuilder extend(
              HttpClientBuilder httpClientBuilder,
              GerritAuthData authData) {
            httpClientBuilder.addInterceptorLast(new HttpRequestInterceptor() {
              public void process(HttpRequest request, HttpContext context)
                  throws HttpException, IOException {
                Header existingUserAgent =
                    request.getFirstHeader(HttpHeaders.USER_AGENT);
                String userAgent =
                    String.format("%s/%s", getApplicationName(), getVersion());
                userAgent += " using " + existingUserAgent.getValue();
                request.setHeader(HttpHeaders.USER_AGENT, userAgent);
              }
            });
            return super.extend(httpClientBuilder, authData);
          }
        });
  }

  public String getApplicationName() {
    int stringId = context.getApplicationInfo().labelRes;
    return context.getString(stringId);
  }

  public String getVersion() {
    try {
      PackageInfo pInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), 0);
      return pInfo.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      return "n/a";
    }
  }

  public GerritApi api() {
    return api;
  }
}
