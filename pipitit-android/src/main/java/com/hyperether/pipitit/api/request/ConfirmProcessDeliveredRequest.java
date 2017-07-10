package com.hyperether.pipitit.api.request;

import com.hyperether.pipitit.data.Job;

import org.json.JSONObject;

/**
 * Send acknowledgment to server that we got message
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/30/2017
 */
public class ConfirmProcessDeliveredRequest extends PipititServerRequest {

    /**
     * Job id
     */
    private String jid;
    /**
     * Process id
     */
    private String pid;

    public ConfirmProcessDeliveredRequest(String url, String jid, String pid) {
        super(url + jid + "/process/" + pid + "/delivered");
        this.pid = pid;
        this.jid = jid;
    }

    public ConfirmProcessDeliveredRequest(String url, Job job) {
        super(url + job.getJid() + "/process/" + job.getPid() + "/delivered");
        this.pid = job.getPid();
        this.jid = job.getJid();
    }

    @Override
    public JSONObject getJsonObject() {
        return new JSONObject();
    }
}
