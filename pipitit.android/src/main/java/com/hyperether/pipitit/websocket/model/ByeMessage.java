package com.hyperether.pipitit.websocket.model;

import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for unregister form Pipitit web server
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public class ByeMessage extends Message {

    private static final String TAG = ByeMessage.class.getSimpleName();

    public ByeMessage() {
        setFirstPlace(true);
    }

    @Override
    public JSONObject makeJsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", getType());
        } catch (JSONException e) {
            PipititLogger.e(TAG, "makeJsonObject", e);
        }
        return json;
    }

    @Override
    public String getType() {
        return Constants.MESSAGE_TYPE_BYE;
    }
}

