package com.hyperether.pipitit.api.request;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.hyperether.pipitit.util.SystemInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model for Create device record request.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public class DeviceCreateRequest extends PipititServerRequest {
    /**
     * Device unique identification
     */
    private String device_id;

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
     * Is location available on device
     */
    private String location_available;

    /**
     * Is push enabled on device
     */
    private String push_enabled;

    /**
     * Device locale
     */
    private String device_locale;

    /**
     * If location permission is granted in app
     */
    private String most_recent_location;

    /**
     * First taken from IP address, if not available, taken from Device Locale
     */
    private String country;

    /**
     * Application version
     */
    private String most_recent_app_version;

    /**
     * Device Model
     */
    private String device_model;

    /**
     * Device OS Version
     */
    private String device_os_version;

    /**
     * Device resolution
     */
    private String device_resolution;

    /**
     * Device Wireless Carrier
     */
    private String device_wireless_carrier;

    /**
     * Device time zone
     */
    private String device_time_zone;

    public DeviceCreateRequest(@NonNull Context mAppContext, @NonNull String url) {
        super(url);
        this.device_id = SystemInfo.getInstance().getAndroidId(mAppContext);
        this.push_enabled =
                String.valueOf(SystemInfo.getInstance().notificationEnabled(mAppContext));
        this.location_available =
                String.valueOf(SystemInfo.getInstance().locationEnabled(mAppContext));
        this.device_locale = SystemInfo.getInstance().getLocale(mAppContext).getLanguage();
        this.device_model = SystemInfo.getInstance().getDeviceModel();
        this.device_os_version = SystemInfo.getInstance().getOsVersion();
        this.device_resolution = SystemInfo.getInstance().getDeviceResolution(mAppContext);
        this.most_recent_app_version = SystemInfo.getInstance().getVersion(mAppContext);
        this.device_time_zone = SystemInfo.getInstance().getTimeZone();
        this.country = SystemInfo.getInstance().getNetworkCountry(mAppContext);
        this.device_wireless_carrier = SystemInfo.getInstance().getNetworkCarrier(mAppContext);
    }

    public DeviceCreateRequest(@NonNull Context mAppContext, @NonNull String url, String token,
                               String WSNode, String email, String username) {
        super(url);
        this.device_id = SystemInfo.getInstance().getAndroidId(mAppContext);
        this.token = token;
        this.WSNode = WSNode;
        this.email = email;
        this.username = username;
        this.push_enabled =
                String.valueOf(SystemInfo.getInstance().notificationEnabled(mAppContext));
        this.location_available =
                String.valueOf(SystemInfo.getInstance().locationEnabled(mAppContext));
        this.device_locale = SystemInfo.getInstance().getLocale(mAppContext).getLanguage();
        this.device_model = SystemInfo.getInstance().getDeviceModel();
        this.device_os_version = SystemInfo.getInstance().getOsVersion();
        this.device_resolution = SystemInfo.getInstance().getDeviceResolution(mAppContext);
        this.most_recent_app_version = SystemInfo.getInstance().getVersion(mAppContext);
        this.device_time_zone = SystemInfo.getInstance().getTimeZone();
        this.country = SystemInfo.getInstance().getNetworkCountry(mAppContext);
        this.device_wireless_carrier = SystemInfo.getInstance().getNetworkCarrier(mAppContext);
    }

    public void setMost_recent_location(String most_recent_location) {
        this.most_recent_location = most_recent_location;
    }

    @Override
    public JSONObject getJsonObject() {
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(this));
            if (jsonObject.has("url"))
                jsonObject.remove("url");
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
