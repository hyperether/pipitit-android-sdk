package com.hyperether.pipitit.websocket.model;

/**
 * Class is present web socket response.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public class ServerResponseMessage {

    String ack;
    String type;

    public String getAck() {
        return ack;
    }

    public String getType() {
        return type;
    }
}
