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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gerrit.extensions.client.ListChangesOption;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.RestApiException;

import java.util.LinkedList;
import java.util.List;

public class QueryHandler {
  /**
   * Number of changes that should be fetched at once from the server.
   */
  private static final int LIMIT_QUERY = 25;

  private final Gerrit gerrit;

  private final LinkedList<Change> result = new LinkedList<>();

  private QueryConfig config;

  /**
   * Number of changes which should be skipped when querying the next changes
   * from the server.
   */
  private int start = 0;

  private int page = 0;

  /**
   * Whether there are more changes at the server.
   */
  private boolean more = true;

  QueryHandler(ConfigManager cfgManager, Gerrit gerrit) {
    this.gerrit = gerrit;
    this.config = cfgManager.getQueryConfig();

    cfgManager.addUpdateListener(new ConfigManager.OnUpdate() {
      @Override
      public void onUpdate(QueryConfig cfg) {
        config = cfg;
        reset();
      }
    });
  }

  public List<Change> next() throws RestApiException {
    query();
    return result.subList(page * LIMIT_QUERY, (page + 1) * LIMIT_QUERY);
  }

  public boolean hasNext() {
    return more;
  }

  private void query() throws RestApiException {
    result.addAll(Collections2.transform(Collections2.filter(
        gerrit.api()
            .changes()
            .query(config.encodedQuery())
            .withOption(ListChangesOption.ALL_FILES)
            .withOption(ListChangesOption.CURRENT_COMMIT)
            .withOption(ListChangesOption.CURRENT_REVISION)
            .withOption(ListChangesOption.DETAILED_ACCOUNTS)
            .withOption(ListChangesOption.DETAILED_LABELS)
            .withLimit(LIMIT_QUERY)
            .withStart(start)
            .get(), new Predicate<ChangeInfo>() {
          @Override
          public boolean apply(ChangeInfo changeInfo) {
            // filter out changes with no revisions
            return changeInfo.revisions != null;
          }
        }), new Function<ChangeInfo, Change>() {
      @Override
      public Change apply(ChangeInfo changeInfo) {
        return new Change(gerrit.api(), changeInfo);
      }
    }));
    start += LIMIT_QUERY;
    more = !result.isEmpty() && (result.getLast().info._moreChanges != null
        ? result.getLast().info._moreChanges
        : false);
  }

  public void reset() {
    result.clear();
    start = 0;
    more = true;
    page = 0;
  }
}
