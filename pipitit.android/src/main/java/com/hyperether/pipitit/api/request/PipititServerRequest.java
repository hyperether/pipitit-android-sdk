package com.hyperether.pipitit.api.request;

import org.json.JSONObject;

/**
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
abstract class PipititServerRequest {

    private String url;

    public PipititServerRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public abstract JSONObject getJsonObject();
}
