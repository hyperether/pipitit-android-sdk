package com.hyperether.pipitit.push.fcm;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hyperether.pipitit.PipititManager;
import com.hyperether.pipitit.R;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.util.SharedPreferenceUtil;
import com.hyperether.pipitit.util.SystemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

/**
 * Singleton class is used for registration or deregistration of push token-a
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/29/2017
 */
public class FirebaseRegisterManager {

    private static final String TAG = FirebaseRegisterManager.class.getSimpleName();

    private static FirebaseRegisterManager INSTANCE;
    private boolean registrationRunning = false;
    private final List<TokenListener> listeners = new ArrayList<>();

    private FirebaseRegisterManager() {

    }

    public static synchronized FirebaseRegisterManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseRegisterManager();
        }
        return INSTANCE;
    }

    public static void clear(Context context) {
        if (INSTANCE != null) {
            INSTANCE.unregisterInBackground(context);
        }
    }

    /**
     * Check if push token exist and if not register in background
     *
     * @param listener listener to inform about registration
     */
    public void registerInBackground(Context context, TokenListener listener) {

        String registrationId = getRegistrationId(context);
        if (registrationId.length() == 0 && !registrationRunning) {
            if (PipititManager.getConfig(context).isFcmRegistrationEnabled(context)) {
                PipititLogger.d(TAG, "registerInBackground: start RegisterAsyncTask");
                FirebaseApp.initializeApp(context);
                new RegisterAsyncTask(context).execute();
                synchronized (listeners) {
                    if (listener != null)
                        listeners.add(listener);
                }
            } else if (listener != null)
                listener.onError();
        } else {
            PipititLogger.d(TAG, "registerInBackground -  token exist" + registrationId);
            if (listener != null)
                listener.onSuccess(registrationId);
        }
    }

    /**
     * Return registration id from SharedPreferences if exist and application version is the same
     * like in SharedPreferences else return empty string.
     *
     * @return current registration id
     */
    public String getRegistrationId(Context context) {
        String registrationId = SharedPreferenceUtil.getInstance()
                .readString(context, SharedPreferenceUtil.PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            PipititLogger.d(TAG, "FCM: registration not found.");
            registrationId = "";
        }
        PipititLogger.d(TAG, "getRegistrationId: " + registrationId);

        int registeredVersion = SharedPreferenceUtil.getInstance()
                .readInt(context, SharedPreferenceUtil.PROPERTY_APPLICATION_VERSION, 0);
        int currentVersion = SystemInfo.getInstance().getAppVersion(context);
        if (registeredVersion != currentVersion) {
            PipititLogger.d(TAG, "FCM: app version changed");
            registrationId = "";
        }
        return registrationId;
    }

    public void unregisterInBackground(Context context) {
        if (PipititManager.getConfig(context).isFcmRegistrationEnabled(context)) {
            FirebaseApp.initializeApp(context);
            PipititLogger.d(TAG, "unregisterInBackground");
            new UnregisterAsyncTask(context).execute();
        }
    }

    /**
     * Remove registration id from SharedPreferences
     */
    public void removeRegistrationId(Context context) {
        PipititLogger.d(TAG, "removeRegistrationId");
        SharedPreferenceUtil.getInstance()
                .saveString(context, SharedPreferenceUtil.PROPERTY_REG_ID, "");
        SharedPreferenceUtil.getInstance()
                .saveInt(context, SharedPreferenceUtil.PROPERTY_APPLICATION_VERSION, -1);
    }

    /**
     * Store registration id in SharedPreferences
     *
     * @param registrationId - registration id to store
     */
    private void storeRegistrationId(Context context, String registrationId) {
        if (registrationId == null)
            registrationId = "";
        int appVersion = SystemInfo.getInstance().getAppVersion(context);
        PipititLogger
                .d(TAG, "FCM: saving " + registrationId + " regId for app version " + appVersion);

        SharedPreferenceUtil.getInstance()
                .saveString(context, SharedPreferenceUtil.PROPERTY_REG_ID, registrationId);
        SharedPreferenceUtil.getInstance()
                .saveInt(context, SharedPreferenceUtil.PROPERTY_APPLICATION_VERSION, appVersion);
    }


    private class RegisterAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private Context mContext;

        public RegisterAsyncTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Boolean doInBackground(Void[] params) {
            boolean success = false;
            try {
                if (mContext != null) {
                    if (mContext.getString(R.string.com_hyperether_pipitit_server_push)
                            .equalsIgnoreCase("SERVER"))
                        throw new MissingResourceException(
                                "Value com_hyperether_Pipitit_server_push is missing in pipitit.xml",
                                "String", "com_hyperether_Pipitit_server_push");
                    registrationRunning = true;
                    String registrationId = FirebaseInstanceId.getInstance().getToken(
                            mContext.getString(R.string.com_hyperether_pipitit_server_push),
                            FirebaseMessaging.INSTANCE_ID_SCOPE);
                    PipititLogger
                            .d(TAG, "device registered, registration ID=" + registrationId);
                    if (registrationId != null && !registrationId.isEmpty()) {
                        storeRegistrationId(mContext, registrationId);
                        success = true;
                    } else
                        storeRegistrationId(mContext, null);
                } else {
                    PipititLogger.e(TAG, "Device registration context is NULL!!!");
                }
            } catch (Exception e) {
                PipititLogger.e(TAG, "RegisterAsyncTask", e);
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            synchronized (listeners) {
                if (!listeners.isEmpty()) {
                    for (TokenListener listener : listeners) {
                        if (success)
                            listener.onSuccess(getRegistrationId(mContext));
                        else
                            listener.onError();
                    }
                }
                listeners.clear();
            }
            registrationRunning = false;
        }
    }

    private class UnregisterAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private Context mContext;

        public UnregisterAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void[] params) {
            boolean success = false;
            try {

                if (mContext != null) {
                    if (mContext.getString(R.string.com_hyperether_pipitit_server_push)
                            .equalsIgnoreCase("SERVER"))
                        throw new MissingResourceException(
                                "Value com_hyperether_Pipitit_server_push is missing in pipitit.xml",
                                "String", "com_hyperether_Pipitit_server_push");
                    FirebaseInstanceId.getInstance()
                            .deleteToken(
                                    mContext.getString(R.string.com_hyperether_pipitit_server_push),
                                    FirebaseMessaging.INSTANCE_ID_SCOPE);

                    PipititLogger.d(TAG, "FCM: device UNREGISTER");
                    removeRegistrationId(mContext);
                    success = true;
                }

            } catch (Exception e) {
                PipititLogger.e(TAG, "UnregisterAsyncTask", e);
                removeRegistrationId(mContext);
            }

            return success;
        }
    }
}
