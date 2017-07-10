package com.hyperether.pipitit.websocket.model;

import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Message which will be used do keep web socket connection
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public class PingMessage extends Message {
    private static final String TAG = ByeMessage.class.getSimpleName();


    @Override
    public JSONObject makeJsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("ping", true);
        } catch (JSONException e) {
            PipititLogger.e(TAG, "makeJsonObject", e);
        }
        return json;
    }

    @Override
    public String getSequence() {
        return Constants.SERVER_MESSAGE_TYPE_PONG;
    }

    @Override
    public String getType() {
        return Constants.MESSAGE_TYPE_PING;
    }

}
