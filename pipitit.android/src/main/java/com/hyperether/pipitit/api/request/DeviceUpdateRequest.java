package com.hyperether.pipitit.api.request;

import android.content.Context;

import com.hyperether.pipitit.data.Device;

/**
 * Update device data
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/30/2017
 */
public class DeviceUpdateRequest extends DeviceCreateRequest {

    /**
     * Id returned from server
     */
    private long id;

//    public DeviceUpdateRequest(Context context, @NonNull String url, long id) {
//        super(context, url);
//        this.id = id;
//        setUrl(url + id);
//    }
//
//    public DeviceUpdateRequest(Context context, @NonNull String url, String token, String WSNode,
//                               String email, String username, long id) {
//        super(context, url, token, WSNode, email, username);
//        this.id = id;
//        setUrl(url + id);
//    }

    public DeviceUpdateRequest(Context context, Device device, String url) {
        super(context, url, device.getToken(), device.getWSNode(), device.getEmail(),
                device.getUsername());
        this.id = device.getId();
        this.setUrl(url + id);
    }

    public void setId(long id) {
        this.id = id;
//        setUrl(Constants.REQUEST_DEVICE_UPDATE + id);
    }

    public long getId() {
        return id;
    }
}
