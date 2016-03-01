package com.flipbox.utils.OAuth10;

/*
 * taken from : http://www.programcreek.com/java-api-examples/index.php?source_dir=kaa-master/server/verifiers/twitter-verifier/verifier/src/main/java/org/kaaproject/kaa/server/verifiers/twitter/verifier/OAuthHeaderBuilder.java
 * modified by bukhori muhammad aqid, Feb 2016
 *
 * Copyright 2015 CyberVision, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import oauth.signpost.OAuth;

public class OAuthHeaderBuilder {
    private final String SIGNATURE_METHOD;
    private final String REQUEST_METHOD;
    private final String URL;
    private final String ENCRYPTION_ALGO;
    private final String CONSUMER_KEY;
    private final String CONSUMER_SECRET;

    public OAuthHeaderBuilder(String SIGNATURE_METHOD, String REQUEST_METHOD, String URL, String ENCRYPTION_ALGO,
                              String CONSUMER_KEY, String CONSUMER_SECRET) {
        this.SIGNATURE_METHOD = SIGNATURE_METHOD;
        this.REQUEST_METHOD = REQUEST_METHOD;
        this.URL = URL;
        this.ENCRYPTION_ALGO = ENCRYPTION_ALGO;
        this.CONSUMER_KEY = CONSUMER_KEY;
        this.CONSUMER_SECRET = CONSUMER_SECRET;
    }

    public String generateHeader(String accessToken, String accessTokenSecret) throws InvalidKeyException, NoSuchAlgorithmException {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = UUID.randomUUID().toString().replaceAll("-", "");

        String signatureBase = generateSignatureBase(CONSUMER_KEY, accessToken, timestamp, nonce);
        String signature = generateSignature(signatureBase, accessTokenSecret);

        String header = getKeyValueString("OAuth " +
                        OAuth.OAUTH_CONSUMER_KEY, CONSUMER_KEY,
                OAuth.OAUTH_NONCE, nonce,
                OAuth.OAUTH_TIMESTAMP, timestamp,
                OAuth.OAUTH_SIGNATURE_METHOD, SIGNATURE_METHOD,
                OAuth.OAUTH_VERSION, OAuth.VERSION_1_0,
                OAuth.OAUTH_SIGNATURE, OAuth.percentEncode(signature)
        );

        return header;
    }

    private String generateSignatureBase(String consumerKey, String accessToken, String timestamp, String nonce) {
        StringBuilder parameters = new StringBuilder();
        appendQueryPairs(parameters,
                OAuth.OAUTH_CONSUMER_KEY, consumerKey,
                OAuth.OAUTH_NONCE, nonce,
                OAuth.OAUTH_TIMESTAMP, timestamp,
                OAuth.OAUTH_SIGNATURE_METHOD, SIGNATURE_METHOD,
                OAuth.OAUTH_VERSION, OAuth.VERSION_1_0);
        String percentEncodedParams = OAuth.percentEncode(parameters.toString());
        StringBuilder signatureBase = new StringBuilder();
        appendAll(signatureBase, REQUEST_METHOD, "&", OAuth.percentEncode(URL), "&", percentEncodedParams);

        Log.d("headers", signatureBase.toString());

        return signatureBase.toString();
    }

    private String generateSignature(String signatureBase, String accessTokenSecret)
            throws InvalidKeyException, NoSuchAlgorithmException {

        Mac mac = Mac.getInstance(ENCRYPTION_ALGO);
        mac.init(new SecretKeySpec((CONSUMER_SECRET + "&" + accessTokenSecret).getBytes(), ENCRYPTION_ALGO));
        mac.update(signatureBase.getBytes());
        byte[] res = mac.doFinal();
        String signature = new String(Base64.encodeBase64(res)).trim();
        Log.d("headers", signature);
        return signature;
    }

    private void appendQueryPairs(StringBuilder builder, CharSequence... pairs) {
        for (int i = 0; i < pairs.length; i++) {
            if (i % 2 == 0) {
                builder.append(pairs[i]);
            } else {
                if (i != pairs.length - 1) builder.append("=").append(pairs[i]).append("&");
                else builder.append("=").append(pairs[i]);
            }
        }
    }

    private void appendAll(StringBuilder builder, CharSequence... vals) {
        for (CharSequence s : vals) {
            builder.append(s);
        }
    }

    private String getKeyValueString(CharSequence... vals) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < vals.length; i++) {
            if (i % 2 == 0) {
                builder.append(vals[i]);
            } else {
                builder.append("=").append("\"").append(vals[i]).append("\"");
                if (i != vals.length - 1) builder.append(",");
            }
        }
        return builder.toString();
    }
}
