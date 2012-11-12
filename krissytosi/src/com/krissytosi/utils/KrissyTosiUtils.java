/*
   Copyright 2012 Sean O' Shea

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.krissytosi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Just some utilities used throughout the application.
 */
public class KrissyTosiUtils {

    /**
     * @return boolean indicating that the current OS is >= honeycomb (3.0).
     */
    public static boolean atLeastHoneyComb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Checks to see whether the application has a viable internet connection.
     * Does NOT check to see whether any possible Wi-Fi connection is blocked by
     * a splash screen.
     * 
     * @param context
     * @return boolean indicating that there is at least some internet
     *         connection (either 3g, 4g or Wi-Fi).
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInformation = connectivityManager.getActiveNetworkInfo();
        return networkInformation != null && networkInformation.isConnectedOrConnecting();
    }
}
