package com.hyperether.pipitit.websocket.model;

import org.json.JSONObject;

/**
 * Interface for sending to pipitit server
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public interface IMessage {

    /**
     * Prepare JSONObject for sending to server
     *
     * @return JSONObject
     */
    JSONObject makeJsonObject();

    /**
     * Type of message
     *
     * @return type of message
     */
    String getType();

    /**
     * Message Status
     *
     * @return status of message
     */
    Message.Status getStatus();
}
