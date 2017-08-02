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
     * If is true pipitit sdk will automatically register to FireBase cloud messaging and token will
     * be sent to pipitit server.
     * <p>
     * Default value is true.
     */

    /**
     * webSocketEnabled
     * If is true pipitit sdk will connect to web socket.
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

    /**
     * If is true pipitit sdk will automatically register to FireBase cloud messaging and token will
     * be sent to pipitit server.
     * <p>
     * Default value is true.
     *
     * @param context application context
     *
     * @return true if sdk need to automatically register to FCM other false
     */
    public static boolean isFcmRegistrationEnabled(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_FCM_REGISTRATION_ENABLED,
                        true);
    }

    /**
     * If is true pipitit sdk will connect to web socket.
     * <p>
     * Default value is false.
     *
     * @param context application context
     *
     * @return true if sdk need to connect to web socket other false
     */
    public static boolean isWebSocketEnabled(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_WEBSOCKET, false);
    }

    /**
     * //TODO check this
     * If is true sdk can be used to send push notification message via pipitit server
     * <p>
     * Default value is false.
     *
     * @param context application context
     *
     * @return true if sending push notifications is enable other false
     *
     * @deprecated do not use it
     */
    @Deprecated
    public static boolean isSendingPushEnabled(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_SENDING_PUSH, false);
    }

    /**
     * If is true {@link PipititLogger} is set to debug mode.
     * <p>
     * Default value is false.
     *
     * @param context application context
     *
     * @return true if logger is set to debug mode other false
     */
    public static boolean isDebug(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_DEBUG, false);
    }

    /**
     * If is not null, all push message notification will be returned via this listener.
     * <p>
     * Default value is NULL.
     *
     * @return listener if is set
     */
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

    /**
     * If is true push notification will launch app
     * <p>
     * Default value is false.
     *
     * @param context application context
     *
     * @return true if push notification need to wake up and launch app
     */
    public static boolean isNotificationWakeUp(Context context) {
        return SharedPreferenceUtil.getInstance()
                .readBoolean(context, SharedPreferenceUtil.ATTRIBUTE_WAKE_UP_NOTIFICATION, false);
    }

    /**
     * Pipitit server url
     * <p>
     * Default value is HyperEther service.
     *
     * @param context application context
     *
     * @return pipitit server url
     */
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

        /**
         * Set pipitit sdk to automatically register to FCM and send push token to Pipitit server
         * <p>
         * If not set default value will be true
         *
         * @param fcmRegistrationEnabled If is true pipitit sdk will automatically register to FCM
         * and token will be sent to pipitit server.
         *
         * @return builder instance
         */
        public Builder setFcmRegistrationEnabled(boolean fcmRegistrationEnabled) {
            this.fcmRegistrationEnabled = fcmRegistrationEnabled;
            return this;
        }

        /**
         * If not set default value will be false
         *
         * @param debug If is true {@link PipititLogger} is set to debug mode.
         *
         * @return builder instance
         */
        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * If not set default value will be false
         *
         * @param webSocketEnabled If is true pipitit sdk will connect to web socket.
         *
         * @return builder instance
         */
        public Builder setWebSocketEnabled(boolean webSocketEnabled) {
            this.webSocketEnabled = webSocketEnabled;
            return this;
        }

        /**
         * If not set default value will be false
         *
         * @param sendingPushEnabled If is true sdk can be used to send push notification message
         * via pipitit server
         *
         * @return builder instance
         *
         * @deprecated do not use it
         */
        @Deprecated
        public Builder setSendingPushEnabled(boolean sendingPushEnabled) {
            this.sendingPushEnabled = sendingPushEnabled;
            return this;
        }

        /**
         * @param listener
         *
         * @return builder instance
         */
        public Builder setListener(PipititPushListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * @param url piptit server url
         *
         * @return builder instance
         */
        public Builder setURL(String url) {
            this.url = url;
            return this;
        }

        /**
         * If not set default value will be false
         *
         * @param notificationWakeUp true if push notification need to wake up and launch app
         *
         * @return builder instance
         */
        public Builder setNotificationWakeUp(boolean notificationWakeUp) {
            this.notificationWakeUp = notificationWakeUp;
            return this;
        }

        /**
         * @param weSocketUrl
         *
         * @return builder instance
         */
        public Builder setWeSocketUrl(String weSocketUrl) {
            this.weSocketUrl = weSocketUrl;
            return this;
        }
    }
}
