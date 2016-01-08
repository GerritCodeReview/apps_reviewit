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

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.ErrorFragment;
import com.google.reviewit.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewItApp extends Application {
  private static final String TAG = ReviewItApp.class.getName();

  private ActionHandler actionHandler;
  private AvatarCache avatarCache;
  private ConfigManager cfgManager;
  private ExecutorService executor;
  private Gerrit gerrit;
  private PreferenceManager prefManager;
  private AccountInfo self;
  private Activity currentActivity;

  @Override
  public void onCreate() {
    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(
          Activity activity, Bundle savedInstanceState) {
        currentActivity = activity;
      }

      @Override
      public void onActivityStarted(Activity activity) {
      }

      @Override
      public void onActivityResumed(Activity activity) {
      }

      @Override
      public void onActivityPaused(Activity activity) {
      }

      @Override
      public void onActivityStopped(Activity activity) {
      }

      @Override
      public void onActivitySaveInstanceState(
          Activity activity, Bundle outState) {
      }

      @Override
      public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
      }
    });

    Thread.setDefaultUncaughtExceptionHandler(
        new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable t) {
        Log.e(TAG, "Application failure", t);
        if (currentActivity != null) {
          FragmentManager fragmentManager =
              currentActivity.getFragmentManager();
          fragmentManager.beginTransaction()
              .replace(R.id.mainFrame, ErrorFragment.create(t))
              .commit();
        }
      }
    });
    super.onCreate();
  }

  public ActionHandler getActionHandler() {
    if (actionHandler == null) {
      actionHandler = new ActionHandler(getConfigManager(), getGerrit());
    }
    return actionHandler;
  }

  public AvatarCache getAvatarCache() {
    if (avatarCache == null) {
      avatarCache = new AvatarCache(this);
    }
    return avatarCache;
  }

  public ConfigManager getConfigManager() {
    if (cfgManager == null) {
      cfgManager = new ConfigManager(this);
    }
    return cfgManager;
  }

  public GerritApi getApi() {
    return getGerrit().api();
  }

  private Gerrit getGerrit() {
    if (gerrit == null) {
      gerrit = new Gerrit(getConfigManager());
    }
    return gerrit;
  }

  public ExecutorService getExecutor() {
    if (executor == null) {
      executor = Executors.newFixedThreadPool(10);
    }
    return executor;
  }

  public Preferences getPrefs() {
    return getPrefManager().getPreferences();
  }

  public PreferenceManager getPrefManager() {
    if (prefManager == null) {
      prefManager = new PreferenceManager(this);
    }
    return prefManager;
  }

  public AccountInfo getSelf() {
    if (self == null) {
      try {
        self = getApi().accounts().self().get();
      } catch (RestApiException e) {
        Log.e(TAG, "Failed to get self", e);
      }
    }
    return self;
  }
}
