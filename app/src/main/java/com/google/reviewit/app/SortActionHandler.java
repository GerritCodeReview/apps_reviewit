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

public class SortActionHandler {
  public enum Action {
    /**
     * Do nothing.
     */
    NONE,

    /**
     * Star the change by putting a label on it.
     */
    STAR,

    /**
     * Ignore the change by putting the <code>Ignore</code> label on it.
     */
    IGNORE,

    /**
     * Skip the change. The user wants to be asked about this change again,
     * when there is a new patch set.
     */
    SKIP
  }

  /**
   * Maximum number of actions that can be undone.
   */
  private static final int LIMIT_UNDO = 10;

  /**
   * Number of changes that should be fetched at once from the server.
   */
  private static final int LIMIT_QUERY = 25;

  /**
   * Number of changes in the queue that triggers fetching more changes from
   * the server.
   */
  private static final int THRESHOLD_QUEUE = 5;

  private final Gerrit gerrit;

  /**
   * Last actions that have been done. Kept for being able to undo actions.
   * Contains at most <code>LIMIT_UNDO</code> actions.
   */
  private final LinkedList<ChangeActionRecord> done = new LinkedList<>();

  /**
   * Changes fetched from the server that the user should process.
   */
  private final LinkedList<Change> toProcess = new LinkedList<>();

  private QueryConfig config;
  private Change currentChange;

  /**
   * Number of changes which should be skipped when querying the next changes
   * from the server.
   */
  private int start = 0;

  /**
   * Whether there are more changes at the server.
   */
  private boolean more = true;

  SortActionHandler(ConfigManager cfgManager, Gerrit gerrit) {
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

  public Change next() {
    currentChange = toProcess.removeFirst();
    return currentChange;
  }

  public Change preview() {
    if (!toProcess.isEmpty()) {
      return toProcess.getFirst();
    } else {
      return null;
    }
  }

  public boolean hasNext() throws RestApiException {
    queryIfNeeded();
    return !toProcess.isEmpty();
  }

  /**
   * Returns the current size of the queue. There may be more changes
   * available which have not been fetched from the server yet.
   * <p/>
   * The queue is automatically refilled when its size is under the
   * <code>THRESHOLD_QUEUE</code>. This means if the returned value is
   * lower than <code>THRESHOLD_QUEUE</code> there are no more changes
   * available at the server.
   */
  public int getQueueSize() throws RestApiException {
    queryIfNeeded();
    return toProcess.size();
  }

  /**
   * Fetches the next <code>LIMIT_QUERY</code> changes from the server when
   * the queue size reached the <code>THRESHOLD_QUEUE</code>.
   */
  private void queryIfNeeded() throws RestApiException {
    if (!isQueryNeeded()) {
      return;
    }

    toProcess.addAll(Collections2.transform(Collections2.filter(
        gerrit.api()
            .changes()
            .query(config.encodedQuery())
            .withOption(ListChangesOption.ALL_FILES)
            .withOption(ListChangesOption.CURRENT_COMMIT)
            .withOption(ListChangesOption.CURRENT_REVISION)
            .withOption(ListChangesOption.DETAILED_ACCOUNTS)
            .withOption(ListChangesOption.DETAILED_LABELS)
            .withLimit(toProcess.isEmpty()
                ? THRESHOLD_QUEUE
                : LIMIT_QUERY)
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
    more = !toProcess.isEmpty() && (toProcess.getLast().info._moreChanges != null
        ? toProcess.getLast().info._moreChanges
        : false);
  }

  public boolean isQueryNeeded() {
    return toProcess.size() < THRESHOLD_QUEUE && more;
  }

  /**
   * Stars the current change.
   */
  public void star() {
    checkCurrentChange();
    // TODO star current change
    recordAction(Action.STAR);
    currentChange = null;
  }

  private void undoStar(Change change) {
    // TODO
  }

  /**
   * Ignores the current change.
   */
  public void ignore() {
    checkCurrentChange();
    // TODO ignore current change
    recordAction(Action.IGNORE);
    currentChange = null;
  }

  private void undoIgnore(Change change) {
    // TODO
  }

  /**
   * Skips the current change.
   */
  public void skip() {
    checkCurrentChange();
    // TODO skip current change
    recordAction(Action.SKIP);
    currentChange = null;
  }

  private void undoSkip(Change change) {
    // TODO
  }

  private void checkCurrentChange() {
    if (currentChange == null) {
      throw new IllegalStateException("no current change");
    }
  }

  private void recordAction(Action action) {
    done.add(new ChangeActionRecord(currentChange, action));
    while (done.size() > LIMIT_UNDO) {
      done.removeFirst();
    }
  }

  public boolean hasCurrentChange() {
    return currentChange != null;
  }

  public Change getCurrentChange() {
    return currentChange;
  }

  /**
   * Puts the current change back into the queue.
   */
  public Change pushBack() {
    Change c = currentChange;
    if (currentChange != null) {
      toProcess.addFirst(currentChange);
      currentChange = null;
    }
    return c;
  }

  public boolean undoPossible() {
    return !done.isEmpty();
  }

  public void undo() {
    pushBack();
    ChangeActionRecord changeActionRecord = done.removeLast();
    switch (changeActionRecord.action) {
      case IGNORE:
        undoIgnore(changeActionRecord.change);
        break;
      case SKIP:
        undoSkip(changeActionRecord.change);
        break;
      case STAR:
        undoStar(changeActionRecord.change);
        break;
      case NONE:
        break;
      default:
        throw new IllegalStateException("unknown action: "
            + changeActionRecord.action);
    }
    toProcess.addFirst(changeActionRecord.change);
  }

  public void reset() {
    done.clear();
    toProcess.clear();
    start = 0;
    currentChange = null;
    more = true;
  }

  private static class ChangeActionRecord {
    final Change change;
    final Action action;

    ChangeActionRecord(Change change, Action action) {
      this.change = change;
      this.action = action;
    }
  }
}
