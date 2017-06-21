package com.cloudant.http.internal.interceptors;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;

/**
 * Created by tomblench on 21/06/2017.
 */

public class IamCookieInterceptor extends CookieInterceptorBase {

    public IamCookieInterceptor(String apiKey, String baseURL) {

        try {
            this.sessionURL = new URL(String.format("%s/_iam_session", baseURL));
            this.sessionRequestBody = String.format("apikey=%s", URLEncoder.encode(apiKey, "UTF-8"))
                    .getBytes("UTF-8");
            ;
        } catch (UnsupportedEncodingException e) {
            //all JVMs should support UTF-8, so this should not happen
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            // this should be a valid URL since the builder is passing it in
            logger.log(Level.SEVERE, "Failed to create URL for _iam_session endpoint", e);
            throw new RuntimeException(e);
        }


    }
}
