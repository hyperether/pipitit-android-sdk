package com.hyperether.pipitit.push.fcm;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hyperether.pipitit.PipititApp;
import com.hyperether.pipitit.PipititManager;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.push.MessageParser;

/**
 * Pipitit message receiver. Class just {@link RemoteMessage} send to {@link
 * PipititManager}
 *
 * @author Nebojsa Brankovic
 * @author Slobodan Prijic
 * @version 1.1 - 7/13/2017
 */
public class PipititMessageReceiver extends FirebaseMessagingService {

    private static final String TAG = PipititMessageReceiver.class.getSimpleName();

    public PipititMessageReceiver() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (message == null)
            return;
        Context context = getApplicationContext();
        if (PipititManager.getConfig(context).isFcmRegistrationEnabled(context)) {
            MessageParser messageParser = new MessageParser();
            messageParser.parse(context, message.getData().get("message"));
            PipititApp.getInstance().send(message.getData());
            PipititLogger.d(TAG, "parse message: from = " + message.getFrom() +
                    ", message ID = " + message.getMessageId() +
                    ", send Time = " + message.getSentTime() +
                    ", data = " + message.getData() +
                    ", from = " + message.getFrom());
        }
    }
}
