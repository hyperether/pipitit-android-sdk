package com.hyperether.pipitit.api;

import android.content.Context;

import com.google.gson.Gson;
import com.hyperether.pipitit.PipititManager;
import com.hyperether.pipitit.api.request.ConfirmProcessDeliveredRequest;
import com.hyperether.pipitit.api.request.CreateJobRequest;
import com.hyperether.pipitit.api.request.DeviceCreateRequest;
import com.hyperether.pipitit.api.request.DeviceUpdateRequest;
import com.hyperether.pipitit.api.request.PipititApiRequest;
import com.hyperether.pipitit.api.response.CreateJobResponse;
import com.hyperether.pipitit.api.response.DeviceCreateResponse;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.data.Device;
import com.hyperether.pipitit.data.Job;

/**
 * In this class we are have implementation off all API calls.
 *
 * @author Nebojsa Brankovic
 * @author Slobodan Prijic
 * @version 1.0 - 4/30/2017
 */
public class PipititApiManager {

    private static final String TAG = PipititApiManager.class.getName();

    private static PipititApiManager INSTANCE;

    private PipititApiManager() {
    }

    public static synchronized PipititApiManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PipititApiManager();
        return INSTANCE;
    }

    public static void clear() {
        VolleyController.clear();
        INSTANCE = null;
    }

    /**
     * Call API request for create device. This api will return id for server and all data if we set
     * before for this device.
     * <p>
     * When response from server is received we will set data in object {@link
     * Device} in {@link PipititManager}
     *
     * @param mAppContext application context
     * @param callback callback to return response
     * @param deviceCreateRequest request with all data which need to be send to server
     */
    public void deviceCreate(final Context mAppContext, final ApiResponseCallback callback,
                             DeviceCreateRequest deviceCreateRequest) {
        new PipititApiRequest(new VolleyResponse() {
            @Override
            public void onSuccess(String response) {
                DeviceCreateResponse apiResponse = null;
                try {
                    apiResponse = new Gson().fromJson(response, DeviceCreateResponse.class);
                } catch (Exception e) {
                    PipititLogger.e(TAG, "deviceCreate", e);
                }
                if (apiResponse != null && apiResponse.getId() > 0) {
                    if (PipititManager.isInitiated()) {
                        PipititManager.getInstance()
                                .populateDevice(apiResponse.getDeviceId(), apiResponse.getToken(),
                                        apiResponse.getWSNode(), apiResponse.getEmail(),
                                        apiResponse.getUsername(), apiResponse.getId());
                    }
                    if (callback != null)
                        callback.onSuccess(apiResponse);
                } else if (callback != null)
                    callback.onError("Response is null or id is smaller or equal to 0");
                PipititLogger.d(TAG, response);
            }

            @Override
            public void onError(int statusCode, String message) {
                if (callback != null)
                    callback.onError(message);
            }
        }).addJsonRequest(deviceCreateRequest, deviceCreateRequest.getUrl(), mAppContext);
    }

    /**
     * Call API request for update device data. This api will return all data which we have on
     * server for this device.
     * <p>
     * When response from server is received we will set data in object  {@link
     * Device} in {@link PipititManager}
     *
     * @param mAppContext application context
     * @param callback callback to return response
     * @param deviceUpdateRequest request with all data which need to be send to server
     */
    public void deviceUpdate(final Context mAppContext, final ApiResponseCallback callback,
                             DeviceUpdateRequest deviceUpdateRequest)
            throws IllegalArgumentException {
        if (deviceUpdateRequest == null || deviceUpdateRequest.getId() <= 0)
            throw new IllegalArgumentException("id need to be bigger then 0!!");

        new PipititApiRequest(new VolleyResponse() {
            @Override
            public void onSuccess(String response) {
                DeviceCreateResponse apiResponse = null;
                try {
                    apiResponse = new Gson().fromJson(response, DeviceCreateResponse.class);
                } catch (Exception e) {
                    PipititLogger.e(TAG, "deviceUpdate", e);
                }
                if (apiResponse != null && apiResponse.getId() > 0) {
                    if (PipititManager.isInitiated()) {
                        PipititManager.getInstance()
                                .populateDevice(apiResponse.getDeviceId(), apiResponse.getToken(),
                                        apiResponse.getWSNode(), apiResponse.getEmail(),
                                        apiResponse.getUsername(), apiResponse.getId());
                    }
                    if (callback != null)
                        callback.onSuccess(apiResponse);
                } else if (callback != null)
                    callback.onError("Response is null or id is smaller or equal to 0");
                PipititLogger.d(TAG, response);
            }

            @Override
            public void onError(int statusCode, String message) {
                if (callback != null)
                    callback.onError(message);
            }
        }).addJsonRequest(deviceUpdateRequest, deviceUpdateRequest.getUrl(), mAppContext);
    }

    /**
     * Api call for sending message
     *
     * @param mAppContext application context
     * @param job to be send to server
     * @param callback callback to return response
     */
    public void sendMessage(final Context mAppContext,
                            final ApiResponseCallback callback,
                            String url,
                            Job job) throws IllegalArgumentException {
        try {
            if (!PipititManager.getConfig(mAppContext).isSendingPushEnabled(mAppContext))
                throw new IllegalAccessException("Sending message is not enabled in config!!!");

            CreateJobRequest createJobRequest = new CreateJobRequest(url, job);
            new PipititApiRequest(new VolleyResponse() {
                @Override
                public void onSuccess(String response) {
                    CreateJobResponse apiResponse = null;
                    try {
                        apiResponse = new Gson().fromJson(response, CreateJobResponse.class);
                    } catch (Exception e) {
                        PipititLogger.e(TAG, "sendMessage", e);
                    }

                    if (apiResponse != null && apiResponse.getJid() != null) {
                        if (callback != null)
                            callback.onSuccess(apiResponse);
                    } else if (callback != null)
                        callback.onError("Response is null or job is not create");
                    PipititLogger.d(TAG, response);
                }

                @Override
                public void onError(int statusCode, String message) {
                    if (callback != null)
                        callback.onError(message);
                }
            }).addJsonRequest(createJobRequest, createJobRequest.getUrl(), mAppContext);
        } catch (IllegalAccessException e) {
            PipititLogger.e(TAG, "", e);
        } catch (Exception e) {
            if (callback != null)
                callback.onError(e.getMessage());
            PipititLogger.e(TAG, "sendMessage", e);
        }
    }

    /**
     * Confirm that job process id delivered
     *
     * @param mAppContext application context
     * @param jid job id
     * @param pid process id
     * @param callback callback to return response
     */
    public void confirmDelivered(final Context mAppContext, final ApiResponseCallback callback,
                                 String pid, String jid) {
        try {
            ConfirmProcessDeliveredRequest confirmProcessDeliveredRequest =
                    new ConfirmProcessDeliveredRequest(
                            PipititManager.getConfig(mAppContext).getUrl(mAppContext) + "/job/", jid,
                            pid);
            new PipititApiRequest(new VolleyResponse() {
                @Override
                public void onSuccess(String response) {
                    if (callback != null)
                        callback.onSuccess(null);
                    PipititLogger.d(TAG, response);
                }

                @Override
                public void onError(int statusCode, String message) {
                    if (callback != null)
                        callback.onError(message);
                }
            }).addJsonRequest(confirmProcessDeliveredRequest,
                    confirmProcessDeliveredRequest.getUrl(),
                    mAppContext);
        } catch (Exception e) {
            if (callback != null)
                callback.onError(e.getMessage());
            PipititLogger.e(TAG, "sendMessage", e);
        }
    }

}
