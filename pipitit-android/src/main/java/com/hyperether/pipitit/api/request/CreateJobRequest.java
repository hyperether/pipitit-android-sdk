package com.hyperether.pipitit.api.request;

import com.google.gson.Gson;
import com.hyperether.pipitit.data.Job;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Class is used to send some message to some device
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/30/2017
 */
public class CreateJobRequest extends PipititServerRequest {

    /**
     * Job type (0=push, 1=email, 2=sms, 3=websocket) {@link Job}
     */
    private int type;

    /**
     * Targeted ids of devices
     */
    private List<Long> targets;

    /**
     * Text to send
     */
    private String message;

    /**
     * Type of the message (0=system, 1=custom). Used just for job type 0=push.
     */
    private Integer messageType;


    public CreateJobRequest(String url, int type, List<Long> targets, String message,
                            Integer messageType) {
        super(url);
        this.type = type;
        this.targets = targets;
        this.message = message;
        this.messageType = messageType;
    }

    public CreateJobRequest(String url, Job job) {
        super(url);
        this.type = job.getType();
        this.targets = job.getTargets();
        this.message = job.getMessage();
        this.messageType = job.getMessageType();
    }


    @Override
    public JSONObject getJsonObject() {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(this));
            if (jsonObject.has("url"))
                jsonObject.remove("url");
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
