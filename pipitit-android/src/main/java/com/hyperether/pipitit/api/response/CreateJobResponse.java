package com.hyperether.pipitit.api.response;

/**
 * Response of creating job (sending some message to some user).
 * Response has one file jid (Created Job ID)
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/30/2017
 */
public class CreateJobResponse extends PipititServerResponse {

    private String jid;

    public String getJid() {
        return jid;
    }
}
