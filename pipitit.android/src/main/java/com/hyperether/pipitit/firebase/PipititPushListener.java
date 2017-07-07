package com.hyperether.pipitit.firebase;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Listener for received push message
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 5/1/2017
 */
public interface PipititPushListener {

    void onMessageReceived(RemoteMessage message);
}
