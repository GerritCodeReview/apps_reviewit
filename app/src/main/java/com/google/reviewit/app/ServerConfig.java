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

import android.support.annotation.NonNull;

public class ServerConfig implements Comparable<ServerConfig> {
  public final String id;
  public final String name;
  public final String url;
  public final String user;
  public final String password;

  private ServerConfig(
      String id, String name, String url, String user, String password) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.user = user;
    this.password = password;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof ServerConfig
        && ((ServerConfig) o).id.equals(id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public int compareTo(@NonNull ServerConfig another) {
    return name.compareTo(another.name);
  }

  public static class Builder {
    private String id;
    private String name;
    private String url;
    private String user;
    private String password;

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder setUser(String user) {
      this.user = user;
      return this;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public ServerConfig build() {
      return new ServerConfig(id, name, url, user, password);
    }
  }
}
