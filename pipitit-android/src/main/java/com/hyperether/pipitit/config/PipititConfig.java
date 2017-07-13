package com.hyperether.pipitit.config;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hyperether.pipitit.R;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.push.PipititPushListener;
import com.hyperether.pipitit.util.Constants;
import com.hyperether.pipitit.util.SharedPreferenceUtil;

import java.util.MissingResourceException;

/**
 * Is used to configure pipitit sdk
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 5/1/2017
 */
public class PipititConfig {

    /**
     * fcmRegistrationEnabled
     * If is true pipitit skd will automatically register to FireBase cloud messaging and token will
     * be sent to pipitit server.
     * <p>
     * Default value is true.
     */

    /**
     * webSocketEnabled
     * If is true pipitit skd will connect to web socket.
     * <p>
     * Default value is false.
     */

    /**
     * sendingPushEnabled
     * If is true sdk can be used to send push notification message via pipitit server
     * <p>
     * Default value is false.
     */

    /**
     * debug
     * If is true {@link PipititLogger} is set to debug mode.
     * <p>
     * Default value is false.
     */

    /**
     * Pipitit server url
     * <p>
     * Default value is HyperEther service.
     */

    /**
     * notificationWakeUp
     * If is true push notification will launch app
     * <p>
     * Default value is false.
     */


    /**
     * If is not null, all push message notification will be returned via this listener.
     * <p>
     * Default value is NULL.
     */
    private final PipititPushListener listener;

    private PipititConfig(Builder builder, Context context) {
        this.listener = builder.listener;
        SharedPreferenceUtil shared = SharedPreferenceUtil.getInstance();
        shared.saveBoolean(context, SharedPreferenceUtil.ATTRIBUTE_DEBUG, builder.debug);
        shared.saveBoolean(context, SharedPreferenceUtil.ATTRIBUTE_WAKE_UP_NOTIFICATION,
                builder.notificationWakeUp);
        shared.saveBoolean(context, SharedPreferenceUtil.ATTRIBUTE_WEBSOCKET,
                builder.webSocketEnabled);
        shared.saveBoolean(context, SharedPreferenceUtil.ATTRIBUTE_SENDING_PUSH,
                builder.sendingPushEnabled);
        shared.saveBoolean(context, SharedPreferenceUtil.ATTRIBUTE_FCM_REGISTRATION_ENABLED,
                builder.fcmRegistrationEnabled);
        shared.saveString(context, SharedPreferenceUtil.ATTRIBUTE_SERVER_URL, builder.url);
        shared.saveString(context, SharedPreferenceUtil.ATTRIBUTE_WEB_SERVER_URL,
                builder.weSocketUrl);
    }

    public static boolean isFcmRegistrationEnabled(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_FCM_REGISTRATION_ENABLED,
                        true);
    }

    public static boolean isWebSocketEnabled(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_WEBSOCKET, false);
    }

    public static boolean isSendingPushEnabled(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_SENDING_PUSH, false);
    }

    public static boolean isDebug(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_DEBUG, false);
    }

    public PipititPushListener getListener() {
        return listener;
    }

    public static String getUrl(@NonNull Context context) {
        if (context.getString(R.string.pipitit_api_key)
                .equalsIgnoreCase("API_KEY"))
            throw new MissingResourceException(
                    "Value Pipitit_api_key is missing in pipitit.xml", "String", "Pipitit_api_key");

        return SharedPreferenceUtil.getInstance()
                .readString(context, SharedPreferenceUtil.ATTRIBUTE_SERVER_URL,
                        Constants.PIPITIT_API_SERVER) + "/" +
                context.getString(R.string.pipitit_api_key);
    }

    public static boolean isNotificationWakeUp(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_WAKE_UP_NOTIFICATION, false);
    }

    public static String getWebSocketUrl(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readString(context, SharedPreferenceUtil.ATTRIBUTE_WEB_SERVER_URL,
                        Constants.PIPITIT_WEB_SERVER);
    }

    public static class Builder {

        private boolean fcmRegistrationEnabled = true;
        private boolean debug = false;
        private boolean webSocketEnabled = false;
        private boolean sendingPushEnabled = false;
        private boolean notificationWakeUp = false;
        private PipititPushListener listener = null;
        private String url = Constants.PIPITIT_API_SERVER;
        private String weSocketUrl = Constants.PIPITIT_WEB_SERVER;

        public PipititConfig build(Context context) {
            return new PipititConfig(this, context);
        }

        public Builder setFcmRegistrationEnabled(boolean fcmRegistrationEnabled) {
            this.fcmRegistrationEnabled = fcmRegistrationEnabled;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setWebSocketEnabled(boolean webSocketEnabled) {
            this.webSocketEnabled = webSocketEnabled;
            return this;
        }

        public Builder setSendingPushEnabled(boolean sendingPushEnabled) {
            this.sendingPushEnabled = sendingPushEnabled;
            return this;
        }

        public Builder setListener(PipititPushListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setURL(String url) {
            this.url = url;
            return this;
        }

        public Builder setNotificationWakeUp(boolean notificationWakeUp) {
            this.notificationWakeUp = notificationWakeUp;
            return this;
        }

        public Builder setWeSocketUrl(String weSocketUrl) {
            this.weSocketUrl = weSocketUrl;
            return this;
        }
    }
}
