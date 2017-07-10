package com.hyperether.pipitit.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represent model of Job. Is it use to create job which will send a message.
 * Job can have one of 4 types (sending types).
 * <p>
 * {@link #TYPE_PUSH}
 * <p>
 * {@link #TYPE_EMAIL}
 * <p>
 * {@link #TYPE_SMS}
 * <p>
 * {@link #TYPE_WEBSOCKET}
 * <p>
 * Need to have targeted device and message which we want to send.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/30/2017
 */
public class Job {

    public static final int TYPE_PUSH = 0;
    public static final int TYPE_EMAIL = 1;
    public static final int TYPE_SMS = 2;
    public static final int TYPE_WEBSOCKET = 3;

    /**
     * Job type
     */
    private int type;

    /**
     * Message type
     */
    private Integer messageType;
    /**
     * Targeted ids of devices
     */
    private List<Long> targets;

    /**
     * Text to send
     */
    private String message;

    /**
     * Process id
     */
    private String pid;

    /**
     * Job id
     */
    private String jid;

    protected Job(int type, String message, List<Long> targets, Integer messageType) {
        this.type = type;
        this.targets = targets;
        this.message = message;
        this.messageType = messageType;

        checkParams(type, message, targets);
    }

    protected Job(int type, String message, Integer messageType ,Long... target) {
        List<Long> to = new ArrayList<>();
        if (target != null && target.length > 0) {
            for (Long t : target) {
                to.add(t);
            }
        }

        this.type = type;
        this.targets = to;
        this.message = message;
        this.messageType = messageType;

        checkParams(type, message, targets);
    }

    public Job(int type, String message, Long target, Integer messageType) {
        List<Long> to = new ArrayList<>();
        to.add(target);

        this.type = type;
        this.targets = to;
        this.message = message;
        this.messageType = messageType;

        checkParams(type, message, targets);
    }

    /**
     * Check if targets has at list one device to be targeted if not throw exception.
     * Check if type og job is good define if not throw exception.
     * Check if message is not null and is not empty if not  throw exception.
     *
     * @param type type of job
     * @param message text message in job
     * @param targets device for job
     */
    private void checkParams(int type, String message, List<Long> targets) {
        if (targets.isEmpty())
            throw new IllegalArgumentException("Targets size is 0!!!");
        if (type < Job.TYPE_PUSH || type > Job.TYPE_WEBSOCKET)
            throw new IllegalArgumentException("Type is not good define!!!");
        if (message == null || message.isEmpty())
            throw new IllegalArgumentException("Message is null or empty");
    }

    public int getType() {
        return type;
    }

    public List<Long> getTargets() {
        return targets;
    }

    public String getMessage() {
        return message;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public int getMessageType() {
        return messageType;
    }
}
