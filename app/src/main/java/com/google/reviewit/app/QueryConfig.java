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

import com.google.common.base.Strings;

public class QueryConfig {
  public final String serverId;
  public final String query;
  public final String label;

  private QueryConfig(String serverId, String query, String label) {
    this.serverId = serverId;
    this.query = query;
    this.label = label;
  }

  public boolean isComplete() {
    return !Strings.isNullOrEmpty(serverId)
        && !Strings.isNullOrEmpty(query)
        && !Strings.isNullOrEmpty(label);
  }

  public static class Builder {
    private String serverId;
    private String query;
    private String label;

    public Builder() {
    }

    public Builder(QueryConfig cfg) {
      this.serverId = cfg.serverId;
      this.query = cfg.query;
      this.label = cfg.label;
    }

    public Builder setServerId(String serverId) {
      this.serverId = serverId;
      return this;
    }

    public Builder setQuery(String query) {
      this.query = query;
      return this;
    }

    public Builder setLabel(String label) {
      this.label = label;
      return this;
    }

    public QueryConfig build() {
      return new QueryConfig(serverId, query, label);
    }
  }
}
