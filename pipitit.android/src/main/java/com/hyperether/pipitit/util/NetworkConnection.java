package com.hyperether.pipitit.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class for network status
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class NetworkConnection {

    /**
     * Check internet access
     */
    public static boolean hasNetworkConnection(Context context) {
        boolean hasInternet = false;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            hasInternet = true;
        }

        return hasInternet;
    }

    /**
     * Check is network is metered
     *
     * @param context application context
     *
     * @return true if network is metered other false
     */
    public static boolean isActiveNetworkMetered(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.isActiveNetworkMetered();
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context application context
     *
     * @return true if connection is type wifi other false
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() &&
                info.getType() == android.net.ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context application context
     *
     * @return true if connection is type mobile other false
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() &&
                info.getType() == android.net.ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Get the network info
     *
     * @param context application context
     *
     * @return active network interface
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
}
