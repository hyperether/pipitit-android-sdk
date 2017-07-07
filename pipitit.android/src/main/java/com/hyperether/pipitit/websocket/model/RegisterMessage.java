package com.hyperether.pipitit.websocket.model;

import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for register on Pipitit server
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class RegisterMessage extends Message {

    private static final String TAG = RegisterMessage.class.getSimpleName();

    //Token which we got from Pipitit API server
    private String did;

    public RegisterMessage(String did) {
        this.did = did;
        setFirstPlace(true);
    }

    public String getDid() {
        return did;
    }

    @Override
    public JSONObject makeJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", getType());
            jsonObject.put("did", getDid());
        } catch (JSONException e) {
            PipititLogger.e(TAG, "makeJsonObject", e);
        }
        return jsonObject;
    }

    @Override
    public String getSequence() {
        return Constants.SERVER_MESSAGE_TYPE_REGISTER;
    }

    @Override
    public String getType() {
        return Constants.MESSAGE_TYPE_REGISTER;
    }
}
