package com.hyperether.pipitit.data;

/**
 * Represent custom push notification send from pipitit server
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 5/15/2017
 */
public class CustomPushNotification {
    public static final String SYSTEM_MESSAGE = "0";
    public static final String CUSTOM_MESSAGE = "1";
    public static final String CAMPAIGN_MESSAGE = "2";

    private Data data;

    private String did;

    private String jid;

    private String pid;

    private String messageType;


    public class Data {
        private String message;

        public String getMessage() {
            return message;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
