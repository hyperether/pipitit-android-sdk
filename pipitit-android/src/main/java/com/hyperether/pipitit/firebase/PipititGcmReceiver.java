package com.hyperether.pipitit.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.gson.Gson;
import com.hyperether.pipitit.PipititApp;
import com.hyperether.pipitit.PipititManager;
import com.hyperether.pipitit.api.ApiResponseCallback;
import com.hyperether.pipitit.api.PipititApiManager;
import com.hyperether.pipitit.api.response.PipititServerResponse;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.data.CampaignMessage;
import com.hyperether.pipitit.data.CustomPushNotification;
import com.hyperether.pipitit.notification.NotificationHandler;

/**
 * Receiver for GCM message
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 7/12/2017
 */
public class PipititGcmReceiver extends BroadcastReceiver {

    private static final String TAG = PipititGcmReceiver.class.getSimpleName();

    public PipititGcmReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null &&
                !PipititManager.getConfig(context).isFcmRegistrationEnabled(context)) {
            PipititApp.getInstance().send(intent);
            CustomPushNotification customPushNotification = null;
            Bundle bundle = intent.getExtras();
            String action = intent.getAction();
            PipititLogger.d(TAG, "Receive GCM message with action = " + action);
            if (bundle != null) {
                String message = bundle.getString("message");
                try {
                    if (message != null) {
                        try {
                            customPushNotification = new Gson().fromJson(
                                    message, CustomPushNotification.class);
                        } catch (Exception e) {
                            PipititLogger.e(TAG, "onGCMMessageReceived", e);
                        }
                    }
                } catch (Exception e) {
                    PipititLogger.e(TAG, "parse message ", e);
                }
                if (customPushNotification != null) {
                    sendAckToServer(customPushNotification, context);
                }

                PipititManager pipititManager = null;
                if (PipititManager.isInitiated()) {
                    pipititManager = PipititManager.getInstance();
                }

                if (pipititManager != null) {
                    if (customPushNotification != null && CustomPushNotification.CAMPAIGN_MESSAGE
                            .equalsIgnoreCase(customPushNotification.getMessageType())) {
                        CampaignMessage campaignMessage =
                                parseCampaignMessage(customPushNotification);
                        PipititApp.getInstance().send(campaignMessage);
                    }
                } else {
                    if (customPushNotification != null) {
                        if (CustomPushNotification.CAMPAIGN_MESSAGE
                                .equalsIgnoreCase(customPushNotification.getMessageType())) {
                            CampaignMessage campaignMessage =
                                    parseCampaignMessage(customPushNotification);
                            NotificationHandler.getInstance()
                                    .showNotification(context, "",
                                            campaignMessage.getPayload().getMessage());
                        }
                    }

                    PipititLogger
                            .d(TAG,
                                    "We receive PUSH message but PipititManager is not initiated!!!");
                    if (PipititManager.getConfig(context).isNotificationWakeUp(context)) {
                        try {
                            PipititLogger.d(TAG, "Start launch Activity");
                            PackageManager pm = context.getPackageManager();
                            Intent wakeIntent =
                                    pm.getLaunchIntentForPackage(context.getPackageName());
                            context.startActivity(wakeIntent);
                        } catch (Exception e) {
                            PipititLogger.e(TAG, "wake up notiifcation", e);
                        }
                    }
                }
            }
        }
    }

    private CampaignMessage parseCampaignMessage(CustomPushNotification customPushNotification) {
        CampaignMessage campaignMessage = null;
        try {
            campaignMessage = new Gson()
                    .fromJson(customPushNotification.getData().getMessage(), CampaignMessage.class);
        } catch (Exception e) {
            PipititLogger.e(TAG, "parseCampaignMessage", e);
        }
        return campaignMessage;
    }

    private void sendAckToServer(CustomPushNotification customPushNotification, Context context) {
        PipititApiManager.getInstance().confirmDelivered(context,
                new ApiResponseCallback() {
                    @Override
                    public void onSuccess(PipititServerResponse response) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                }, customPushNotification.getPid(), customPushNotification.getJid());
    }
}

