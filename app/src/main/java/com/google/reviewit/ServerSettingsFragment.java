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

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.ConfigManager;
import com.google.reviewit.app.Preferences;
import com.google.reviewit.app.QueryConfig;
import com.google.reviewit.app.ServerConfig;
import com.google.reviewit.util.FormatUtil;
import com.google.reviewit.util.WidgetUtil;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import com.urswolfer.gerrit.client.rest.http.HttpStatusException;

import java.net.MalformedURLException;
import java.net.URL;

import static com.google.reviewit.util.WidgetUtil.setGone;
import static com.google.reviewit.util.WidgetUtil.setInvisible;
import static com.google.reviewit.util.WidgetUtil.setVisible;

public class ServerSettingsFragment extends BaseFragment {
  private static final String TAG = ServerSettingsFragment.class.getName();
  private static final String ORIGIN =
      "com.google.reviewit.ServerSettingsFragment.ORIGIN";
  private static final String SERVER_ID =
      "com.google.reviewit.ServerSettingsFragment.SERVER_ID";

  public static ServerSettingsFragment create(
      Class<? extends Fragment> origin) {
    ServerSettingsFragment fragment = new ServerSettingsFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(ORIGIN, origin);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static ServerSettingsFragment create(
      Class<? extends Fragment> origin, String serverId) {
    ServerSettingsFragment fragment = new ServerSettingsFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(ORIGIN, origin);
    bundle.putString(SERVER_ID, serverId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  protected @LayoutRes int getLayout() {
    return R.layout.content_server_settings;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    init();
    String serverId = getArguments() != null
        ? getArguments().getString(SERVER_ID)
        : null;
    displayServerConfig(serverId != null ? getApp().getConfigManager()
        .getServerConfig(serverId) : null);
  }

  private void displayServerConfig(ServerConfig cfg) {
    if (cfg != null) {
      WidgetUtil.setText(v(R.id.idInput), cfg.id);
      WidgetUtil.setText(v(R.id.nameInput), cfg.name);
      WidgetUtil.setText(v(R.id.urlInput), cfg.url);
      WidgetUtil.setText(v(R.id.userInput), cfg.user);
      WidgetUtil.setText(v(R.id.passwordInput), cfg.password);

      displayCredentialsInfo(cfg.url);
    } else {
      WidgetUtil.setText(v(R.id.idInput), "");
      WidgetUtil.setText(v(R.id.nameInput), "");
      WidgetUtil.setText(v(R.id.urlInput), "");
      WidgetUtil.setText(v(R.id.userInput), "");
      WidgetUtil.setText(v(R.id.passwordInput), "");

      displayCredentialsInfo(null);
    }
  }

  private void init() {
    final AutoCompleteTextView urlInput =
        (AutoCompleteTextView) v(R.id.urlInput);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
            getResources().getStringArray(R.array.urls));
    urlInput.setAdapter(adapter);

    v(R.id.pasteCredentialsButton).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            ClipboardManager clipboard = (ClipboardManager) getActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()
                && clipboard.getPrimaryClipDescription().hasMimeType(
                ClipDescription.MIMETYPE_TEXT_PLAIN)) {
              ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
              String pasteData = item.getText().toString();
              if (!pasteData.contains("/.gitcookies")) {
                return;
              }

              pasteData = pasteData.substring(pasteData.indexOf("/.gitcookies"));
              pasteData = pasteData.substring(pasteData.lastIndexOf(",") + 1);
              int pos = pasteData.indexOf("=");
              String user = pasteData.substring(0, pos);
              pasteData = pasteData.substring(pos + 1);
              String password = pasteData.substring(0, pasteData.indexOf("\n"));
              WidgetUtil.setText(v(R.id.userInput), user);
              WidgetUtil.setText(v(R.id.passwordInput), password);

              // hide keyboard if it is open
              View view = getActivity().getCurrentFocus();
              if (view != null) {
                ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(view.getWindowToken(), 0);
              }
            }
          }
        });

    ((EditText) v(R.id.urlInput)).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(
          CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(
          CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        if (Strings.isNullOrEmpty(textOf(R.id.nameInput))) {
          try {
            String host = new URL(s.toString()).getHost();
            int pos = host.indexOf(".");
            WidgetUtil.setText(v(R.id.nameInput),
                pos > 0
                    ? host.substring(0, pos)
                    : host);
          } catch (MalformedURLException e) {
            // ignore
          }
        }
        displayCredentialsInfo(s.toString());
      }
    });

    v(R.id.saveServerSettings).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        enabledForm(false);

        if (!isServerInputComplete()) {
          widgetUtil.showError(R.string.incompleteInput);
          enabledForm(true);
          return;
        }

        if (!isUrlValid()) {
          widgetUtil.showError(R.string.invalidUrl);
          enabledForm(true);
          return;
        }

        if (!hasUniqueName()) {
          widgetUtil.showError(
              getString(R.string.duplicate_server_name, textOf(R.id.nameInput)));
          enabledForm(true);
          return;
        }

        setVisible(
            v(R.id.statusTestConnection, R.id.statusTestConnectionProgress));
        WidgetUtil.setText(v(R.id.statusTestConnectionText), null);
        new AsyncTask<Void, Void, String>() {
          private TextView status;
          private View statusTestConnectionProgress;
          private View statusTestConnection;

          @Override
          protected void onPreExecute() {
            super.onPreExecute();
            status = tv(R.id.statusTestConnectionText);
            statusTestConnectionProgress = v(R.id.statusTestConnectionProgress);
            statusTestConnection = v(R.id.statusTestConnection);
          }

          @Override
          protected String doInBackground(Void... v) {
            return testConnection();
          }

          protected void onPostExecute(String errorMsg) {
            if (errorMsg != null) {
              enabledForm(true);
              status.setTextColor(widgetUtil.color(R.color.statusFailed));
              status.setText(getString(R.string.test_server_connection_failed));
              setInvisible(statusTestConnectionProgress);
              new AlertDialog.Builder(getContext())
                  .setTitle(getString(R.string.error_title))
                  .setMessage(getString(R.string.connection_failed, errorMsg))
                  .setPositiveButton(android.R.string.ok,
                      new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                          // do nothing
                        }
                      }).setNegativeButton(getString(R.string.save_anyway),
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      setGone(statusTestConnection);
                      onServerSave(saveServerSettings());
                    }
                  }).setIcon(android.R.drawable.ic_dialog_alert).show();
            } else {
              status.setTextColor(widgetUtil.color(R.color.statusOk));
              status.setText(getString(R.string.test_server_connection_ok));
              setGone(statusTestConnection);
              onServerSave(saveServerSettings());
            }
          }
        }.execute();
      }
    });

    enabledForm(true);
    setGone(v(R.id.statusTestConnection));
  }

  private void enabledForm(boolean enable) {
    v(R.id.urlInput).setEnabled(enable);
    v(R.id.nameInput).setEnabled(enable);
    v(R.id.pasteCredentialsButton).setEnabled(enable);
    v(R.id.userInput).setEnabled(enable);
    v(R.id.passwordInput).setEnabled(enable);
    v(R.id.saveServerSettings).setEnabled(enable);
  }

  private boolean isServerInputComplete() {
    return !Strings.isNullOrEmpty(textOf(R.id.urlInput))
        && !Strings.isNullOrEmpty(textOf(R.id.nameInput))
        && !Strings.isNullOrEmpty(textOf(R.id.userInput))
        && !Strings.isNullOrEmpty(textOf(R.id.passwordInput));
  }

  private boolean isUrlValid() {
    try {
      new URL(textOf(R.id.urlInput));
      return true;
    } catch (MalformedURLException e) {
      return false;
    }
  }

  private boolean hasUniqueName() {
    if (!Strings.isNullOrEmpty(textOf(R.id.idInput))) {
      // update of existing entry
      return true;
    }

    ServerConfig otherCfg =
        getApp().getConfigManager().getServerByName(textOf(R.id.nameInput));
    return otherCfg == null;
  }

  private String testConnection() {
    GerritApi api = new GerritRestApiFactory().create(
        new GerritAuthData.Basic(
            textOf(R.id.urlInput),
            textOf(R.id.userInput),
            textOf(R.id.passwordInput)));
    try {
      api.changes().query().withLimit(1).get();

      String version = api.config().server().getVersion();
      Log.i(TAG, "version of " + textOf(R.id.nameInput) + ": " + version);
      // TODO check server version (server must support labeled stars and
      // run notedb)
      return null;
    } catch (HttpStatusException e) {
      return e.getStatusCode() + " " + e.getStatusText();
    } catch (RestApiException e) {
      // server not reachable
      Log.e(TAG, "Request failed", e);
      if (e.getCause() != null) {
        return getString(R.string.error_with_cause, e.getMessage(),
            e.getCause().getMessage());
      } else {
        return e.getMessage();
      }
    }
  }

  private void onServerSave(String serverId) {
    if (getArguments().get(ORIGIN).equals(IntroFragment2.class)) {
      ConfigManager cfgManager = getApp().getConfigManager();
      cfgManager.setQueryConfig(
          new QueryConfig.Builder(cfgManager.getQueryConfig())
              .setServerId(serverId)
              .build());
      getApp().getPrefManager().setPreferences(
          new Preferences.Builder(getApp().getPrefManager().getPreferences())
              .setShowIntro(false)
              .build());
      startActivity(new Intent(getContext(), MainActivity.class));
    } else {
      widgetUtil.toast(R.string.server_settings_saved);
      display(ServerListFragment.class);
    }
  }

  private void displayCredentialsInfo(String url) {
    if (Strings.isNullOrEmpty(url)) {
      setGone(v(R.id.crendentialsInfo, R.id.credentialsInfoText,
          R.id.pasteCredentialsButton));
      return;
    }

    TextView credentialsInfo = (TextView) v(R.id.credentialsInfoText);
    credentialsInfo.setMovementMethod(LinkMovementMethod.getInstance());
    url = FormatUtil.ensureSlash(url);

    String host;
    try {
      host = new URL(url).getHost();
    } catch (MalformedURLException e) {
      setGone(v(R.id.crendentialsInfo, R.id.credentialsInfoText,
          R.id.pasteCredentialsButton));
      return;
    }

    if (host.endsWith(".googlesource.com")) {
      url += "new-password";
      credentialsInfo.setText(Html.fromHtml(
          getString(R.string.credentials_info_googlesource,
              createLink(url, getString(R.string.googlesource_obtain_password)))));
      setVisible(v(R.id.crendentialsInfo, R.id.credentialsInfoText,
          R.id.pasteCredentialsButton));
    } else {
      url += "#/settings/http-password";
      credentialsInfo.setText(Html.fromHtml(
          getString(R.string.credentials_info,
              createLink(url, getString(R.string.http_password)))));
      setGone(v(R.id.pasteCredentialsButton));
      setVisible(v(R.id.crendentialsInfo, R.id.credentialsInfoText));
    }
  }

  private String createLink(String url, String text) {
    return "<a href=\"" + url + "\">" + text + "</a>";
  }

  private String saveServerSettings() {
    return getApp().getConfigManager().setServerConfig(
        new ServerConfig.Builder()
            .setId(textOf(R.id.idInput))
            .setName(textOf(R.id.nameInput))
            .setUrl(textOf(R.id.urlInput))
            .setUser(textOf(R.id.userInput))
            .setPassword(textOf(R.id.passwordInput))
            .build());
  }
}
