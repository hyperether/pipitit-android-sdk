package com.hyperether.pipitit.websocket.model;

/**
 * This class represent message status for all message which we be send to web server.
 * <p>
 * Message can have one of three statuses. After instance creation its status is WAITING. After
 * message is sent status will be SENT. When server answer that message is received status will
 * change to ACKNOWLEDGED.
 * <p>
 * Send time represent time when message is send to server.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public abstract class Message implements IMessage {

    public enum Status {
        SENT,
        ACKNOWLEDGED,
        WAITING;
    }

    private Status status = Status.WAITING;

    private long sendTime = -1;

    private String sequence;

    private boolean firstPlace;

    public void setStatus(Status messageStatus) {
        this.status = messageStatus;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }

    public boolean isFirstPlace() {
        return firstPlace;
    }

    public void setFirstPlace(boolean firstPlace) {
        this.firstPlace = firstPlace;
    }
}
