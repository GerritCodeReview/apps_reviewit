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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.AbandonInput;
import com.google.gerrit.extensions.api.changes.RestoreInput;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.client.ChangeStatus;
import com.google.gerrit.extensions.client.ReviewerState;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.common.ApprovalInfo;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.CommentInfo;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.LabelInfo;
import com.google.gerrit.extensions.common.RevisionInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.util.FormatUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Change {
  private final GerritApi api;

  public ChangeInfo info;
  private Map<String, DiffInfo> diffs = new HashMap<>();
  private Integer inlineCommentCount;
  private ApprovalData approvalData;

  public Change(GerritApi api, ChangeInfo info) {
    this.api = api;
    this.info = info;
  }

  public void reload() throws RestApiException {
    info = api.changes().id(info._number).get();
    diffs = null;
    inlineCommentCount = null;
  }

  public RevisionInfo currentRevision() {
    return info.revisions.get(info.currentRevision);
  }

  public Collection<AccountInfo> reviewers(boolean filterOutOwner) {
    if (info.reviewers == null
        || !info.reviewers.containsKey(ReviewerState.REVIEWER)) {
      return Collections.emptySet();
    }

    Collection<AccountInfo> reviewers =
        info.reviewers.get(ReviewerState.REVIEWER);
    if (reviewers != null) {
      if (filterOutOwner) {
        return ImmutableList.copyOf(Collections2.filter(reviewers,
            new Predicate<AccountInfo>() {
          @Override
          public boolean apply(AccountInfo r) {
            return !r._accountId.equals(info.owner._accountId);
          }
        }));
      } else {
        return ImmutableList.copyOf(reviewers);
      }
    } else {
      return Collections.emptySet();
    }
  }

  public Collection<AccountInfo> ccs() {
    if (info.reviewers == null
        || !info.reviewers.containsKey(ReviewerState.CC)) {
      return Collections.emptySet();
    }

    Collection<AccountInfo> ccs = info.reviewers.get(ReviewerState.CC);
    if (ccs != null) {
      return ImmutableList.copyOf(ccs);
    } else {
      return Collections.emptySet();
    }
  }

  public Voters voters(String label) {
    Voters voters = new Voters();
    if (info.labels == null || !info.labels.containsKey(label)) {
      return voters;
    }

    List<ApprovalInfo> all = info.labels.get(label).all;
    if (all == null) {
      return voters;
    }

    for (ApprovalInfo a : all) {
      if (a.value == null) {
        continue;
      }
      if (a.value > 0) {
        voters.likers.add(a);
      } else if (a.value < 0) {
        voters.dislikers.add(a);
      }
    }
    return voters;
  }

  public int getVote(String label, AccountInfo account) {
    LabelInfo labelInfo = info.labels.get(label);
    if (labelInfo == null) {
      return 0;
    }

    if (labelInfo.all != null) {
      for (ApprovalInfo a : labelInfo.all) {
        if (a._accountId.equals(account._accountId)) {
          return a.value;
        }
      }
    }
    return 0;
  }

  public int getInlineCommentCount() throws RestApiException {
    if (inlineCommentCount != null) {
      return inlineCommentCount;
    }

    int count = 0;
    // TODO implement commentsAsList in REST API Client
    for (List<CommentInfo> commentList : api.changes().id(info._number)
        .revision(info.currentRevision).comments().values()) {
      count += commentList.size();
    }
    inlineCommentCount = count;
    return inlineCommentCount;
  }

  public DiffInfo diff(String path) throws RestApiException {
    DiffInfo diff = diffs.get(path);
    if (diff != null) {
      return diff;
    }

    diff =
        api.changes()
            .id(info._number)
            .revision(info.currentRevision)
            .file(path)
            .diffRequest()
            .withIntraline(true)
            .get();
    diffs.put(path, diff);
    return diff;
  }

  public void addReviewer(String reviewerId) throws RestApiException {
    api.changes().id(info._number).addReviewer(reviewerId);
    reload();
  }

  public void postReview(String message, Map<String, Integer> votes)
      throws RestApiException {
    ReviewInput in = new ReviewInput();
    in.message(message);
    in.drafts = ReviewInput.DraftHandling.KEEP;
    for (Map.Entry<String, Integer> e : votes.entrySet()) {
      in.label(e.getKey(), e.getValue());
    }
    api.changes().id(info._number).current().review(in);
  }

  public void submit() throws RestApiException {
    api.changes().id(info._number).current().submit();
  }

  public void abandon(String message) throws RestApiException {
    AbandonInput in = new AbandonInput();
    in.message = message;
    api.changes().id(info._number).abandon(in);
    info.status = ChangeStatus.ABANDONED;
  }

  public void restore(String message) throws RestApiException {
    RestoreInput in = new RestoreInput();
    in.message = message;
    api.changes().id(info._number).restore(in);
    info.status = ChangeStatus.NEW;
  }

  public static class Voters {
    public Set<AccountInfo> likers = new HashSet<>();
    public Set<AccountInfo> dislikers = new HashSet<>();
  }

  public String getUrl(String serverUrl) {
    return FormatUtil.ensureSlash(serverUrl) + "#/c/" + info._number;
  }

  public ApprovalData getApprovalData() {
    if (approvalData == null) {
      approvalData = new ApprovalData(info);
    }
    return approvalData;
  }
}
