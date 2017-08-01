package com.hyperether.pipitit.push;

import android.content.Intent;

import java.util.Map;

/**
 * Listener for received push message
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 5/1/2017
 */
public interface PipititPushListener {

    void onGCMMessageReceived(Intent message);

}
