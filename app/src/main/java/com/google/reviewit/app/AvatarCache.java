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
import android.util.Log;

import com.google.gerrit.extensions.common.AccountInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Cache for the avatar images.
 */
public class AvatarCache {
  private static final String TAG = AvatarCache.class.getName();

  /**
   * Max size of the cache.
   */
  private static final long CACHE_SIZE_BYTES = 10 * 1024 * 1024;

  private final File avatarDir;

  AvatarCache(Context context) {
    this.avatarDir = new File(context.getCacheDir(), "avatars");
    if (!avatarDir.exists() && !avatarDir.mkdirs()) {
      Log.e(TAG, "Failed to create avatar cache directory");
    }
  }

  public byte[] getIfExists(AccountInfo account) {
    String fileName = Integer.toString(account._accountId);
    File file = new File(avatarDir, fileName);
    if (!file.exists()) {
      return null;
    }

    try {
      // TODO use try-with-resources with API 19
      FileInputStream in = new FileInputStream(file);
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
          out.write(buffer, 0, read);
        }
        return out.toByteArray();
      } catch (IOException e) {
        Log.e(TAG, "Failed to read cached avatar image for "
            + account._accountId, e);
        return null;
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          Log.e(TAG, "Failed closing FileInputStream on reading avatar image for "
              + account._accountId, e);
        }
      }
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  public void putAvatar(AccountInfo account, byte[] avatar) {
    long size = size();
    long newSize = avatar.length + size;
    if (newSize > CACHE_SIZE_BYTES) {
      clean(newSize - CACHE_SIZE_BYTES);
    }

    String fileName = Integer.toString(account._accountId);
    try {
      File file = new File(avatarDir, fileName);

      // TODO use try-with-resources with API 19
      FileOutputStream out = new FileOutputStream(file);
      try {
        out.write(avatar);
      } finally {
        out.close();
      }
    } catch (IOException e) {
      Log.e(TAG, "Failed to cache avatar image for " + account._accountId, e);
    }
  }

  /**
   * Removes all avatar images from the cache.
   */
  public void clean() {
    clean(Long.MAX_VALUE);
  }

  private void clean(long bytesToDelete) {
    long bytesDeleted = 0;
    File[] files = avatarDir.listFiles();

    Arrays.sort(files, new Comparator<File>() {
      public int compare(File f1, File f2) {
        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
      }
    });

    for (File file : files) {
      bytesDeleted += file.length();
      Log.d(TAG, "Clean avatar for " + file.getName());
      if (!file.delete()) {
        Log.e(TAG, "Cleaning avatar for " + file.getName() + " failed.");
      }

      if (bytesDeleted >= bytesToDelete) {
        break;
      }
    }
  }

  private long size() {
    long size = 0;
    File[] files = avatarDir.listFiles();
    for (File file : files) {
      if (file.isFile()) {
        size += file.length();
      }
    }
    return size;
  }
}
