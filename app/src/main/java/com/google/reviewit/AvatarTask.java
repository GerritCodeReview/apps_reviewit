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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.restapi.BinaryResult;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.reviewit.app.ReviewItApp;
import com.google.reviewit.util.ObservableAsyncTask;
import com.urswolfer.gerrit.client.rest.accounts.Accounts;
import com.urswolfer.gerrit.client.rest.http.HttpStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Background task to retrieve and show an avatar image.
 * If available the avatar image is taken from the avatar cache, if not it
 * is downloaded and stored in the avatar cache.
 */
public class AvatarTask extends ObservableAsyncTask<AccountInfo, Void, Bitmap> {
  private static final String TAG = AvatarTask.class.getName();

  private final ReviewItApp app;
  private final ImageView avatar;

  public AvatarTask(ReviewItApp app, ImageView avatar) {
    this.avatar = avatar;
    this.app = app;
  }

  @Override
  protected Bitmap doInBackground(AccountInfo... accounts) {
    AccountInfo account = accounts[0];

    byte[] bytes = app.getAvatarCache().getIfExists(account);
    if (bytes == null) {
      try {
        Log.d(TAG, "Download avatar for " + account._accountId);
        BinaryResult r = ((Accounts) app.getApi()
            .accounts())
            .id(account._accountId)
            .downloadAvatar(50);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        r.writeTo(out);
        bytes = out.toByteArray();
        app.getAvatarCache().putAvatar(account, bytes);
      } catch (HttpStatusException e) {
        if (e.getStatusCode() != 404) {
          Log.w(TAG, "Failed to download avatar for " + account._accountId
              + ": " + e.getStatusCode() + " " + e.getStatusText());
        }
        return null;
      } catch (RestApiException e) {
        Log.w(TAG, "Failed to download avatar for " + account._accountId, e);
        return null;
      } catch (IOException e) {
        Log.w(TAG, "Failed to read avatar for " + account._accountId, e);
        return null;
      }
    }
    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    return bitmap != null ? getRoundedCornerBitmap(bitmap, 10) : null;
  }

  protected void postExecute(Bitmap result) {
    if (result != null) {
      avatar.setImageBitmap(result);
    } else {
      avatar.setVisibility(View.GONE);
    }
  }

  private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float pixels) {
    Bitmap output = Bitmap.createBitmap(
        bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);
    Paint paint = new Paint();
    Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    RectF rectF = new RectF(rect);
    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    canvas.drawRoundRect(rectF, pixels, pixels, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);
    return output;
  }
}
