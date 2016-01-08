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

/**
 * Interface to be implemented by fragments that want to control the
 * onBackPressed behaviour.
 */
public interface OnBackPressedAware {
  /**
   * Invoke when the hardware back button was pressed.
   *
   * @return Return true if this event was consumed.
   */
  boolean onBackPressed();
}
