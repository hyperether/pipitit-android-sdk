package com.hyperether.pipitit.websocket.model;

/**
 * Represent server response class on class {@link RegisterMessage}
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 5/1/2017
 */
public class RegisteredMessage extends ServerResponseMessage {

    private String nodeName;

    public String getNodeName() {
        return nodeName;
    }
}
