/*
 * Copyright © 2015, 2017 IBM Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.http.internal.interceptors;

import com.cloudant.http.Http;
import com.cloudant.http.HttpConnection;
import com.cloudant.http.HttpConnectionInterceptorContext;
import com.cloudant.http.HttpConnectionRequestInterceptor;
import com.cloudant.http.HttpConnectionResponseInterceptor;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adds cookie authentication support to http requests.
 *
 * It does this by adding the cookie header for CouchDB
 * using request interceptor pipeline in {@link HttpConnection}.
 *
 * If a response has a response code of 401, it will fetch a cookie from
 * the server using provided credentials and tell {@link HttpConnection} to reply
 * the request by setting {@link HttpConnectionInterceptorContext#replayRequest} property to true.
 *
 * If the request to get the cookie for use in future request fails with a 401 status code
 * (or any status that indicates client error) cookie authentication will not be attempted again.
 */
public class CookieInterceptor extends CookieInterceptorBase {

    /**
     * Constructs a cookie interceptor. Credentials should be supplied not URL encoded, this class
     * will perform the necessary URL encoding.
     *
     * @param username The username to use when getting the cookie (not URL encoded)
     * @param password The password to use when getting the cookie (not URL encoded)
     * @param baseURL  The base URL to use when constructing an `_session` request.
     */
    public CookieInterceptor(String username, String password, String baseURL) {

        try {
            this.sessionURL = new URL(String.format("%s/_session", baseURL));
            this.sessionRequestBody = String.format("name=%s&password=%s", URLEncoder.encode(username, "UTF-8"), URLEncoder.encode(password, "UTF-8"))
                    .getBytes("UTF-8"); ;
        } catch (UnsupportedEncodingException e) {
            //all JVMs should support UTF-8, so this should not happen
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            // this should be a valid URL since the builder is passing it in
            logger.log(Level.SEVERE, "Failed to create URL for _session endpoint", e);
            throw new RuntimeException(e);
        }

    }

}
