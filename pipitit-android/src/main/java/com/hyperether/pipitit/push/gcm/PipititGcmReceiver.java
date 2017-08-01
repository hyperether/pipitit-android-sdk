package com.hyperether.pipitit.push.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hyperether.pipitit.PipititApp;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.push.MessageParser;

/**
 * Receiver for GCM message
 *
 * @author Nebojsa Brankovic
 * @author Slobodan Prijic
 * @version 1.1 - 7/13/2017
 */
public class PipititGcmReceiver extends BroadcastReceiver {

    private static final String TAG = PipititGcmReceiver.class.getSimpleName();

    public PipititGcmReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            PipititApp.getInstance().send(intent);
            Bundle bundle = intent.getExtras();
            String action = intent.getAction();
            PipititLogger.d(TAG, "Receive GCM message with action = " + action);
            if (bundle != null) {
                String message = bundle.getString("message");
                MessageParser messageParser = new MessageParser();
                messageParser.parse(context, message);
            }
        }
    }
}

