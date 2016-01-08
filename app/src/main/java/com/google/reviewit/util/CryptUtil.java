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

package com.google.reviewit.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Utility to encrypt/decrypt.
 */
public class CryptUtil {
  private static final String TAG = CryptUtil.class.getName();

  private static CryptUtil INSTANCE;

  private static final char[] SEKRIT = ("kbj0-IKCsajm =90fs0VALK Caoo" +
      "(*^Y98chac0yhCh>HCXZC<CLKJC>LJ>CL>Zj<adsadsiuotpl;m ncads")
      .toCharArray();

  // TODO use java.nio.charset.StandardCharsets.UTF_8 with API 19
  private static final String UTF_8 = "utf-8";

  private static final String PBEW_CIPHER_TRANSFORMATION = "PBEWithMD5AndDES";

  private final String androidId;
  private final Cipher pbeCipherEncrypt;
  private final Cipher pbeCipherDecrypt;

  public static CryptUtil get(Context context) {
    if (INSTANCE == null) {
      String androidId = Settings.Secure.getString(
          context.getContentResolver(), Settings.Secure.ANDROID_ID);
      INSTANCE = new CryptUtil(androidId);
    }
    return INSTANCE;
  }

  private CryptUtil(String androidId) {
    this.androidId = androidId;
    this.pbeCipherEncrypt = initPbeCipher(Cipher.ENCRYPT_MODE);
    this.pbeCipherDecrypt = initPbeCipher(Cipher.DECRYPT_MODE);
  }

  private Cipher initPbeCipher(int mode) {
    try {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance
          (PBEW_CIPHER_TRANSFORMATION);
      SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
      Cipher pbeCipher = Cipher.getInstance(PBEW_CIPHER_TRANSFORMATION);
      pbeCipher.init(mode, key, new PBEParameterSpec(
          androidId.getBytes(UTF_8), 20));
      return pbeCipher;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException
        | NoSuchPaddingException | UnsupportedEncodingException
        | InvalidAlgorithmParameterException | InvalidKeyException e) {
      Log.e(TAG, "Failed to init pbe cipher", e);
      throw new RuntimeException(e);
    }
  }

  public String encrypt(String value) {
    try {
      byte[] bytes = value != null
          ? value.getBytes(UTF_8)
          : new byte[0];
      return new String(
          Base64.encode(pbeCipherEncrypt.doFinal(bytes), Base64.NO_WRAP),
          UTF_8);
    } catch (IllegalBlockSizeException | BadPaddingException |
        UnsupportedEncodingException e) {
      Log.e(TAG, "Failed to encrypt preference value", e);
      throw new RuntimeException(e);
    }
  }

  public String decrypt(String value) {
    try {
      byte[] bytes = value != null
          ? Base64.decode(value, Base64.DEFAULT)
          : new byte[0];
      return new String(pbeCipherDecrypt.doFinal(bytes), UTF_8);
    } catch (IllegalBlockSizeException | BadPaddingException |
        UnsupportedEncodingException e) {
      Log.e(TAG, "Failed to decrypt preference value", e);
      throw new RuntimeException(e);
    }
  }
}
