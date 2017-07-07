package com.hyperether.pipitit.api.response;

/**
 * Model for Create device record response.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public class DeviceCreateResponse extends PipititServerResponse {

    /**
     * Device unique identification
     */
    private String deviceId;

    /**
     * Device push token
     */
    private String token;

    /**
     * Device connected WS server
     */
    private String WSNode;

    /**
     * Device user email
     */
    private String email;

    /**
     * Device user username
     */
    private String username;

    /**
     * Id returned from server
     */
    private long id;


    public String getDeviceId() {
        return deviceId;
    }

    public String getToken() {
        return token;
    }

    public String getWSNode() {
        return WSNode;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }
}
