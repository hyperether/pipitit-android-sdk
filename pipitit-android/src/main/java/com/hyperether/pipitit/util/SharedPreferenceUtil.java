package com.hyperether.pipitit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Here we are store all preference. Store is in the default file that is
 * used by the preference framework in the given context.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/29/2017
 */
public class SharedPreferenceUtil {
    private static final String TAG = SharedPreferenceUtil.class.getSimpleName();

    public static final String PROPERTY_REG_ID = "pipitit.registration.id";
    public static final String PROPERTY_APPLICATION_VERSION = "pipitit.application.version";
    public static final String ATTRIBUTE_WAKE_UP_NOTIFICATION = "pipitit.application.wakeUpNotification";
    public static final String ATTRIBUTE_DEBUG = "pipitit.application.debug";
    public static final String ATTRIBUTE_SENDING_PUSH = "pipitit.application.sendingPush";
    public static final String ATTRIBUTE_WEBSOCKET = "pipitit.application.webSocket";
    public static final String ATTRIBUTE_FCM_REGISTRATION_ENABLED = "pipitit.application.fcmRegistrationEnabled";
    public static final String ATTRIBUTE_SERVER_URL = "pipitit.application.serverUrl";
    public static final String ATTRIBUTE_WEB_SERVER_URL = "pipitit.application.webServerUrl";

    private static SharedPreferenceUtil INSTANCE;

    private SharedPreferenceUtil() {
    }

    public static synchronized SharedPreferenceUtil getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SharedPreferenceUtil();
        return INSTANCE;
    }

    public static void clear() {
        INSTANCE = null;
    }

    public void saveBoolean(Context context, String key, Boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void saveString(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void saveInt(Context context, String key, Integer value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public Boolean readBoolean(Context context, String key, Boolean defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defValue != null ? defValue : false);
    }

    public String readString(Context context, String key, String defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defValue != null ? defValue : null);
    }

    public Integer readInt(Context context, String key, Integer defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defValue != null ? defValue : 0);
    }
}
