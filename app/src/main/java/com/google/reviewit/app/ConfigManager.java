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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.reviewit.util.CryptUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ConfigManager {
  public interface OnUpdate {
    /**
     * Invoked when the current query config is updated.
     */
    void onUpdate(QueryConfig config);
  }

  private static final String KEY_SERVERS = "SERVERS";
  private static final String KEY_SERVER = "SERVER";
  private static final String KEY_SERVER_ID = "SERVER_ID";
  private static final String KEY_NAME = "NAME";
  private static final String KEY_URL = "URL";
  private static final String KEY_USER = "USER";
  private static final String KEY_PASSWORD = "PASSWORD";
  private static final String KEY_QUERY = "QUERY";
  private static final String KEY_LABEL = "LABEL";

  private static final Set<String> DEFAULT_SERVERS = Collections.emptySet();
  private static final String DEFAULT_QUERY = "status:open project:gerrit";
  private static final String DEFAULT_LABEL = "reviewit";

  private final SharedPreferences prefs;
  private final CryptUtil crypt;
  private final List<OnUpdate> updateListeners = new ArrayList<>();

  /**
   * Cached configs.
   */
  private QueryConfig queryConfig;
  private ServerConfig serverConfig;
  private Map<String, ServerConfig> servers;

  ConfigManager(Context context) {
    this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    this.crypt = CryptUtil.get(context);
  }

  /**
   * Returns the current query config.
   */
  public QueryConfig getQueryConfig() {
    if (queryConfig == null) {
      queryConfig = new QueryConfig.Builder()
          .setServerId(getString(KEY_SERVER_ID, null))
          .setQuery(getString(KEY_QUERY, DEFAULT_QUERY))
          .setLabel(getString(KEY_LABEL, DEFAULT_LABEL))
          .build();
    }
    return queryConfig;
  }

  private String getString(String key, String defaultValue) {
    return crypt.decrypt(prefs.getString(key, crypt.encrypt(defaultValue)));
  }

  /**
   * Sets/updates the current query config.
   */
  public void setQueryConfig(QueryConfig config) {
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(KEY_SERVER_ID, crypt.encrypt(config.serverId));
    editor.putString(KEY_QUERY, crypt.encrypt(config.query));
    editor.putString(KEY_LABEL, crypt.encrypt(config.label));
    editor.apply();

    queryConfig = config;

    for (OnUpdate updateListener : updateListeners) {
      updateListener.onUpdate(config);
    }
  }

  /**
   * Returns the server config for the current query config.
   */
  public ServerConfig getServerConfig() {
    return getServerConfig(getQueryConfig().serverId);
  }

  public ServerConfig getServerConfig(String id) {
    if (serverConfig == null || !serverConfig.id.equals(id)) {
      if (servers != null) {
        serverConfig = servers.get(id);
      } else {
        serverConfig = readServerConfig(id);
      }
    }
    return serverConfig;
  }

  private ServerConfig readServerConfig(String id) {
    return new ServerConfig.Builder()
        .setId(id)
        .setName(getString(serverKey(id, KEY_NAME), null))
        .setUrl(getString(serverKey(id, KEY_URL), null))
        .setUser(getString(serverKey(id, KEY_USER), null))
        .setPassword(getString(serverKey(id, KEY_PASSWORD), null))
        .build();
  }

  /**
   * Adds/updates a server config.
   */
  public String setServerConfig(ServerConfig config) {
    SharedPreferences.Editor editor = prefs.edit();
    String id = config.id;
    if (Strings.isNullOrEmpty(id)) {
      id = UUID.randomUUID().toString();
      addServerId(editor, id);
    }
    editor.putString(serverKey(id, KEY_NAME), crypt.encrypt(config.name));
    editor.putString(serverKey(id, KEY_URL), crypt.encrypt(config.url));
    editor.putString(serverKey(id, KEY_USER), crypt.encrypt(config.user));
    editor.putString(serverKey(id, KEY_PASSWORD),
        crypt.encrypt(config.password));
    editor.apply();

    serverConfig = config;
    if (servers != null) {
      servers.put(id, config);
    }

    QueryConfig queryConfig = getQueryConfig();
    for (OnUpdate updateListener : updateListeners) {
      updateListener.onUpdate(queryConfig);
    }
    return id;
  }

  private Collection<String> addServerId(
      SharedPreferences.Editor editor, String id) {
    Set<String> servers = new HashSet<>(readServerIds());
    servers.add(id);
    storeServerIds(editor, servers);
    return servers;
  }

  /**
   * Removes a server config.
   */
  public void removeServer(String id) {
    if (serverConfig != null && serverConfig.id.equals(id)) {
      serverConfig = null;
    }
    if (servers != null) {
      servers.remove(id);
    }

    SharedPreferences.Editor editor = prefs.edit();
    removeServerId(editor, id);
    editor.remove(serverKey(id, KEY_NAME));
    editor.remove(serverKey(id, KEY_URL));
    editor.remove(serverKey(id, KEY_USER));
    editor.remove(serverKey(id, KEY_PASSWORD));
    editor.apply();
  }

  private Collection<String> removeServerId(
      SharedPreferences.Editor editor, String id) {
    Set<String> servers = new HashSet<>(readServerIds());
    servers.remove(id);
    storeServerIds(editor, servers);
    return servers;
  }

  /**
   * Finds a server config by server name.
   */
  public ServerConfig getServerByName(String name) {
    for (ServerConfig cfg : getServers()) {
      if (name.equals(cfg.name)) {
        return cfg;
      }
    }
    return null;
  }

  public List<ServerConfig> getServers() {
    if (servers == null) {
      servers = new HashMap<>();
      for (String id : readServerIds()) {
        servers.put(id, readServerConfig(id));
      }
    }
    List<ServerConfig> list = new ArrayList<>(servers.values());
    Collections.sort(list);
    return list;
  }

  private Collection<String> readServerIds() {
    return Collections2.transform(
        prefs.getStringSet(KEY_SERVERS, DEFAULT_SERVERS),
        new Function<String, String>() {
      @Override
      public String apply(String s) {
        return crypt.decrypt(s);
      }
    });
  }

  private void storeServerIds(
      SharedPreferences.Editor editor, Collection<String> servers) {
    editor.putStringSet(KEY_SERVERS, new HashSet<>(
        Collections2.transform(servers, new Function<String, String>() {
      @Override
      public String apply(String s) {
        return crypt.encrypt(s);
      }
    })));
  }

  private static String serverKey(String name, String key) {
    return KEY_SERVER + "_" + name + "_" + key;
  }

  public void addUpdateListener(OnUpdate listener) {
    updateListeners.add(listener);
  }
}
