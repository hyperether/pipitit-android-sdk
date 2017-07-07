package com.hyperether.pipitit.websocket;

/**
 * Interface for additional staff after the standard web socket methods
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
interface WebSocketEvent {

    /**
     * Event when web socket is open.
     */
    void onWebSocketOpen();

    /**
     * Message received
     *
     * @param message text message received from web socket
     */
    void onWebSocketMessage(final String message);

    /**
     * Event when wbe socket is close.
     *
     * @param code error code
     * @param reason text error code explanation
     */
    void onWebSocketClose(int code, String reason);

    /**
     * Socket error
     *
     * @param description description for error
     */
    void onWebSocketError(final String description);
}
