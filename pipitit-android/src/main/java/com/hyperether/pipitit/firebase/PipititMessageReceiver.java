package com.hyperether.pipitit.firebase;

import android.content.Intent;
import android.content.pm.PackageManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
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
 * Pipitit message receiver. Class just {@link RemoteMessage} send to {@link
 * PipititManager}
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class PipititMessageReceiver extends FirebaseMessagingService {

    private static final String TAG = PipititMessageReceiver.class.getSimpleName();

    public PipititMessageReceiver() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (PipititManager.getConfig(getApplicationContext())
                .isFcmRegistrationEnabled(getApplicationContext())) {
            PipititApp.getInstance().send(message);
            CustomPushNotification customPushNotification = null;
            try {
                if (message != null) {
                    PipititLogger.d(TAG,
                            "parse message: from = " + message.getFrom() + ", message ID = " +
                                    message.getMessageId() + ", send Time = " +
                                    message.getSentTime() +
                                    ", data = " + message.getData() + ", from = " +
                                    message.getFrom());
                    if (message.getData() != null && message.getData().get("message") != null)
                        try {
                            customPushNotification = new Gson().fromJson(
                                    message.getData().get("message"), CustomPushNotification.class);
                        } catch (Exception e) {
                            PipititLogger.e(TAG, "onFirebaseMessageReceived", e);
                        }
                }
            } catch (Exception e) {
                PipititLogger.e(TAG, "parse message ", e);
            }
            if (customPushNotification != null) {
                sendAckToServer(customPushNotification);
            }

            PipititManager pipititManager = null;
            if (PipititManager.isInitiated()) {
                pipititManager = PipititManager.getInstance();
            }

            if (pipititManager != null) {
                if (customPushNotification != null && CustomPushNotification.CAMPAIGN_MESSAGE
                        .equalsIgnoreCase(customPushNotification.getMessageType())) {
                    CampaignMessage campaignMessage = parseCampaignMessage(customPushNotification);
                    PipititApp.getInstance().send(campaignMessage);
                }
            } else {
                if (customPushNotification != null) {
                    if (CustomPushNotification.CAMPAIGN_MESSAGE
                            .equalsIgnoreCase(customPushNotification.getMessageType())) {
                        CampaignMessage campaignMessage =
                                parseCampaignMessage(customPushNotification);
                        NotificationHandler.getInstance()
                                .showNotification(getApplicationContext(), "",
                                        campaignMessage.getPayload().getMessage());
                    }
                }

                PipititLogger
                        .d(TAG, "We receive PUSH message but PipititManager is not initiated!!!");
                if (PipititManager.getConfig(getApplicationContext())
                        .isNotificationWakeUp(getApplicationContext())) {
                    try {
                        PipititLogger.d(TAG, "Start launch Activity");
                        PackageManager pm = getApplicationContext().getPackageManager();
                        Intent intent =
                                pm.getLaunchIntentForPackage(
                                        getApplicationContext().getPackageName());
                        startActivity(intent);
                    } catch (Exception e) {
                        PipititLogger.e(TAG, "wake up notiifcation", e);
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

    private void sendAckToServer(CustomPushNotification customPushNotification) {
        PipititApiManager.getInstance().confirmDelivered(getApplicationContext(),
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
