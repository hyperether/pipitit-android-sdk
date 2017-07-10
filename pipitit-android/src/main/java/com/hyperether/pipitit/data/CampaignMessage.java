package com.hyperether.pipitit.data;

/**
 * Object represent Campaign message from server
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 5/17/2017
 */
public class CampaignMessage {
    public static final String TYPE_MODAL_1 = "modal_1";

    private String type;

    private int notificationID = 0;

    private Payload payload;

    public class Payload {
        public Payload(String message) {
            this.message = message;
        }

        private String message;

        public String getMessage() {
            return message;
        }
    }

    public String getType() {
        return type;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }
}
