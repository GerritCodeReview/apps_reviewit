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

import com.google.gerrit.extensions.client.ReviewerState;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.common.ApprovalInfo;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.LabelInfo;
import com.google.reviewit.util.FormatUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ApprovalData {
  public final Set<AccountInfo> reviewers;
  public final Map<String, LabelInfo> labels;
  public final TreeMap<String, Map<Integer, ApprovalInfo>> approvalsByLabel;

  public ApprovalData(ChangeInfo change) {
    reviewers = new TreeSet<>(new Comparator<AccountInfo>() {
      @Override
      public int compare(AccountInfo account1, AccountInfo account2) {
        return FormatUtil.format(account1).compareTo(
            FormatUtil.format(account2));
      }
    });
    if (change.reviewers != null
        && change.reviewers.containsKey(ReviewerState.REVIEWER)) {
      reviewers.addAll(change.reviewers.get(ReviewerState.REVIEWER));
    }
    labels = new TreeMap<>();
    approvalsByLabel = new TreeMap<>();
    for (Map.Entry<String, LabelInfo> label : change.labels.entrySet()) {
      Map<Integer, ApprovalInfo> approvalsByAccount = new HashMap<>();
      labels.put(label.getKey(), label.getValue());
      approvalsByLabel.put(label.getKey(), approvalsByAccount);
      List<ApprovalInfo> all = change.labels.get(label.getKey()).all;
      if (all != null) {
        for (ApprovalInfo approval : all) {
          reviewers.add(approval);
          approvalsByAccount.put(approval._accountId, approval);
        }
      }
    }
  }

  public boolean isMax(LabelInfo label, int value) {
    Integer max = null;
    for (String rangeValue : label.values.keySet()) {
      Integer v = Integer.parseInt(rangeValue.trim());
      if (max == null || v.intValue() > max.intValue()) {
        max = v;
      }
    }
    return max != null && max.equals(Integer.valueOf(value));
  }

  public boolean isMin(LabelInfo label, int value) {
    Integer min = null;
    for (String rangeValue : label.values.keySet()) {
      Integer v = Integer.parseInt(rangeValue.trim());
      if (min == null || v.intValue() < min.intValue()) {
        min = v;
      }
    }
    return min != null && min.equals(Integer.valueOf(value));
  }
}
