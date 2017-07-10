package com.hyperether.pipitit.data;

import com.hyperether.pipitit.util.SystemInfo;

/**
 * Represent data of device.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/30/2017
 */
public class Device {

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
     * If location permission is granted in app
     */
    private String most_recent_location;

    /**
     * First taken from IP address, if not available, taken from Device Locale
     */
    private String country;

    /**
     * Device Wireless Carrier
     */
    private String device_wireless_carrier;

    /**
     * Id on pipitit server
     */
    private long id;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWSNode() {
        return WSNode;
    }

    public void setWSNode(String WSNode) {
        this.WSNode = WSNode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getMost_recent_location() {
        return most_recent_location;
    }

    public String getCountry() {
        return country;
    }

    public String getDevice_wireless_carrier() {
        return device_wireless_carrier;
    }

}
