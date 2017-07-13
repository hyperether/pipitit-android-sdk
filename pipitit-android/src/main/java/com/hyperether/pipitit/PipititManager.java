package com.hyperether.pipitit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.hyperether.pipitit.api.ApiResponseCallback;
import com.hyperether.pipitit.api.PipititApiManager;
import com.hyperether.pipitit.api.VolleyResponse;
import com.hyperether.pipitit.api.request.DeviceCreateRequest;
import com.hyperether.pipitit.api.request.DeviceUpdateRequest;
import com.hyperether.pipitit.api.request.PipititApiRequest;
import com.hyperether.pipitit.api.response.DeviceCreateResponse;
import com.hyperether.pipitit.api.response.PipititServerResponse;
import com.hyperether.pipitit.cache.ActivityHandler;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.cache.PipititThread;
import com.hyperether.pipitit.config.PipititConfig;
import com.hyperether.pipitit.data.CampaignMessage;
import com.hyperether.pipitit.data.Device;
import com.hyperether.pipitit.data.Job;
import com.hyperether.pipitit.notification.NotificationHandler;
import com.hyperether.pipitit.notification.dialogs.PipititBaseDialog;
import com.hyperether.pipitit.push.PipititPushListener;
import com.hyperether.pipitit.push.fcm.FirebaseRegisterManager;
import com.hyperether.pipitit.push.fcm.TokenListener;
import com.hyperether.pipitit.util.SharedPreferenceUtil;
import com.hyperether.pipitit.websocket.PipititWebSocketManager;

import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Pipitit main manager
 *
 * @author Nebojsa Brankovic
 * @author Slobodan Prijic
 * @version 1.0 - 4/27/2017
 */
public class PipititManager {

    private static final String TAG = PipititManager.class.getName();

    protected static PipititManager INSTANCE;
    private Context mAppContext;
    private static PipititConfig config;
    private PipititPushListener listener;
    private String urlCreate;
    private String urlUpdate;
    private String urlJob;
    private String urlJobCreate;
    private String urlWs;
    private Device device;
    private ActivityHandler activityHandler;
    private Application application;

    Observer<Object> observer = new Observer<Object>() {
        @Override
        public void onSubscribe(Disposable d) {
            PipititLogger.d(TAG, "onSubscribe: ");
        }

        @Override
        public void onNext(Object value) {
            PipititLogger.d(TAG, "onNext: " + value);
            if (value instanceof CampaignMessage) {
                onCampaignMessageReceive((CampaignMessage) value);
            } else if (value instanceof Intent) {
                onIntentPushReceived((Intent) value);
            } else if (value instanceof Map) {
                onPushReceived((Map) value);
            }
        }

        @Override
        public void onError(Throwable e) {
            PipititLogger.e(TAG, "onError: ", e);
        }

        @Override
        public void onComplete() {
            PipititLogger.d(TAG, "onComplete: All Done!");
        }
    };

    private PipititManager() {
        PipititApp.getInstance().get_bus().subscribeWith(observer);
    }

    public static synchronized PipititManager getInstance() {
        if (INSTANCE == null) {
            PipititLogger.e(TAG,
                    "Failed to get Pipitit instance, getInstance() called prior to init.");
            return null;
        } else
            return INSTANCE;
    }

    public static void clear(Context context, boolean unregister) {
        PipititApiManager.clear();
        if (unregister)
            FirebaseRegisterManager.clear(context);
        if (INSTANCE != null && INSTANCE.application != null)
            INSTANCE.application.unregisterActivityLifecycleCallbacks(INSTANCE.activityHandler);
        PipititWebSocketManager.clear();
        SharedPreferenceUtil.clear();
        PipititThread.clear();
        INSTANCE.activityHandler = null;
        INSTANCE.application = null;
        INSTANCE = null;
    }

    public static void init(Application app, PipititConfig config) {
        PipititLogger.d(TAG, "Pipitit init");
        checkParameters(app, "context");
        checkParameters(config, "config");
        INSTANCE = new PipititManager();
        INSTANCE.mAppContext = app;
        PipititManager.config = config;
        INSTANCE.activityHandler = ActivityHandler.getInstance();
        INSTANCE.application = app;
        INSTANCE.application.registerActivityLifecycleCallbacks(INSTANCE.activityHandler);
        INSTANCE.start(app.getApplicationContext());
    }

    public static boolean isInitiated() {
        return INSTANCE != null;
    }

    public static PipititConfig getConfig(Context context) {
        if (config == null)
            config = new PipititConfig.Builder().build(context);
        return config;
    }

    /**
     * Throw {@link IllegalAccessException} if object is null and log in logger
     *
     * @param parameter object which we need to check if is null
     * @param name object's name
     */
    private static void checkParameters(Object parameter, String name) {
        if (parameter == null) {
            PipititLogger.e(TAG, name + " can not be null");
            throw new IllegalStateException(name + "can not be null");
        }
    }

    public Device getDevice() {
        return device;
    }

    public Device populateDevice(String deviceId, String token, String WSNode, String email,
                                 String username, long id) {
        if (device == null)
            device = new Device();
        if (deviceId != null)
            device.setDeviceId(deviceId);
        if (token != null)
            device.setToken(token);
        if (WSNode != null)
            device.setWSNode(WSNode);
        if (email != null)
            device.setEmail(email);
        if (username != null)
            device.setUsername(username);
        if (id > 0)
            device.setId(id);

        return device;
    }

    private void start(Context context) {
        checkParameters(config, "Configuration ");
        PipititLogger.enableDebugLogging(config.isDebug(context));
        listener = config.getListener();
        urlCreate = config.getUrl(mAppContext) + "/device";
        urlUpdate = config.getUrl(context) + "/device/";
        urlJob = config.getUrl(context) + "/job/";
        urlJobCreate = config.getUrl(context) + "/job/create";
        urlWs = config.getWebSocketUrl(context);
        createDevice();
    }

    /**
     * Call api call for creating device on Pipitit server.If response is success we will star web
     * socket and register to firebase for push token if different is not set in {@link
     * PipititConfig}
     */
    private void createDevice() {
        final DeviceCreateRequest deviceCreateRequest =
                new DeviceCreateRequest(mAppContext, urlCreate);
        new PipititApiRequest(new VolleyResponse() {
            @Override
            public void onSuccess(String response) {
                DeviceCreateResponse apiResponse = null;
                try {
                    apiResponse = new Gson().fromJson(response, DeviceCreateResponse.class);
                } catch (Exception e) {
                    PipititLogger.e(TAG, "deviceCreateRequest:Gson().fromJson", e);
                }
                if (apiResponse != null && apiResponse.getId() > 0)
                    populateDevice(apiResponse.getDeviceId(), apiResponse.getToken(),
                            apiResponse.getWSNode(), apiResponse.getEmail(),
                            apiResponse.getUsername(), apiResponse.getId());
                PipititLogger.d(TAG, response);

                startWebSocket();

                FirebaseRegisterManager.getInstance().registerInBackground(mAppContext,
                        new TokenListener() {
                            @Override
                            public void onSuccess(String token) {
                                device.setToken(token);
                                updateDevice();
                            }

                            @Override
                            public void onError() {
                                if (PipititConfig.isFcmRegistrationEnabled(mAppContext)) {
                                    PipititLogger.d(TAG,
                                            "Firebase is not enabled and push token is not send to Pipitit server!!!");
                                }
                            }
                        });
            }

            @Override
            public void onError(int statusCode, String message) {
                PipititLogger.e(TAG, message);
            }
        }).addJsonRequest(deviceCreateRequest, deviceCreateRequest.getUrl(), Request.Method.PUT,
                mAppContext);
    }

    /**
     * Start web socket client
     */
    private void startWebSocket() {
        PipititWebSocketManager.getInstance(mAppContext).startWebSocket(urlWs, mAppContext);
    }

    private void onPushReceived(Map message) {
        if (listener != null) {
            listener.onFirebaseMessageReceived(message);
            PipititLogger.d(TAG, "Message from Firebase send to PipititPushListener");
        } else {
            PipititLogger.d(TAG, "PipititPushListener listener is null");
        }
    }

    private void onIntentPushReceived(Intent message) {
        if (listener != null) {
            listener.onGCMMessageReceived(message);
            PipititLogger.d(TAG, "Message from GCM send to PipititPushListener");
        } else {
            PipititLogger.d(TAG, "PipititPushListener listener is null");
        }
    }

    private void onCampaignMessageReceive(final CampaignMessage campaignMessage) {
        final Activity activity = getActivity();
        if (activity != null && campaignMessage != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new PipititBaseDialog(activity, false, campaignMessage).show();
                }
            });
        else {
            PipititLogger.d(TAG, "we receive message but activity is null or message is null");
            if (campaignMessage != null) {
                NotificationHandler.getInstance().showNotification(mAppContext, "",
                        campaignMessage.getPayload().getMessage());
            }

        }
    }


    /**
     * Call APi call for update device
     */
    private void updateDevice() {
        DeviceUpdateRequest deviceUpdateRequest =
                new DeviceUpdateRequest(mAppContext, device, urlUpdate);
        PipititApiManager.getInstance().deviceUpdate(mAppContext, new ApiResponseCallback() {
            @Override
            public void onSuccess(PipititServerResponse response) {
            }

            @Override
            public void onError(String message) {
            }
        }, deviceUpdateRequest);
    }

    /*
    Public methods
     */

    /**
     * Is used when we want to send message via pipitit sever.
     *
     * @param message text message which need to be send
     * @param typeOfMessage can be one of 4 types {@link Job}
     * @param to target device id (pipitit server id)
     */
    public void sendMessage(String message, int typeOfMessage, long to) {

        Job job = new Job(typeOfMessage, message, to == -1 ? device.getId() : to, 0);

        PipititApiManager.getInstance().sendMessage(mAppContext, new ApiResponseCallback() {
            @Override
            public void onSuccess(PipititServerResponse response) {

            }

            @Override
            public void onError(String message) {

            }
        }, urlJobCreate, job);
    }

    /**
     * Set Firebase push token for this device. Token will be send to Pipitit server and if some
     * sent push notification to this {@link Device} this token will be used. If user set null push
     * token we be removed from pipitit server.
     *
     * @param pushToken firebase push token to be set on pipitit server for this device
     */
    public void setPushNotificationToken(final String pushToken) throws NullPointerException {
        if (device == null) {
            final DeviceCreateRequest deviceCreateRequest =
                    new DeviceCreateRequest(mAppContext, urlCreate);
            new PipititApiRequest(new VolleyResponse() {
                @Override
                public void onSuccess(String response) {
                    DeviceCreateResponse apiResponse = null;
                    try {
                        apiResponse = new Gson().fromJson(response, DeviceCreateResponse.class);
                    } catch (Exception e) {
                        PipititLogger.e(TAG, "deviceCreateRequest:Gson().fromJson", e);
                    }
                    if (apiResponse != null && apiResponse.getId() > 0) {
                        populateDevice(apiResponse.getDeviceId(), pushToken,
                                apiResponse.getWSNode(), apiResponse.getEmail(),
                                apiResponse.getUsername(), apiResponse.getId());
                        updateDevice();
                    }
                    PipititLogger.d(TAG, response);
                }

                @Override
                public void onError(int statusCode, String message) {
                }
            }).addJsonRequest(deviceCreateRequest, deviceCreateRequest.getUrl(), Request.Method.PUT,
                    mAppContext);
        } else {
            //this will set token and start sending to pipitit server
            device.setToken(pushToken);
            updateDevice();
        }
    }

    /**
     * Set username for this device on pipitit server.
     *
     * @param username username to be set on pipitit server for this device
     *
     * @throws NullPointerException if username is null or empty
     */
    public void setUsername(final String username) throws NullPointerException {
        if (username == null || username.isEmpty())
            throw new NullPointerException("Username can not be null or empty!");
        if (device == null) {
            final DeviceCreateRequest deviceCreateRequest =
                    new DeviceCreateRequest(mAppContext, urlCreate);
            new PipititApiRequest(new VolleyResponse() {
                @Override
                public void onSuccess(String response) {
                    DeviceCreateResponse apiResponse = null;
                    try {
                        apiResponse = new Gson().fromJson(response, DeviceCreateResponse.class);
                    } catch (Exception e) {
                        PipititLogger.e(TAG, "deviceCreateRequest:Gson().fromJson", e);
                    }
                    if (apiResponse != null && apiResponse.getId() > 0) {
                        populateDevice(apiResponse.getDeviceId(), apiResponse.getToken(),
                                apiResponse.getWSNode(), apiResponse.getEmail(),
                                username, apiResponse.getId());
                        updateDevice();
                    }
                    PipititLogger.d(TAG, response);
                }

                @Override
                public void onError(int statusCode, String message) {
                }
            }).addJsonRequest(deviceCreateRequest, deviceCreateRequest.getUrl(), Request.Method.PUT,
                    mAppContext);
        } else {
            device.setUsername(username);
            updateDevice();
        }
    }

    /**
     * Set email for this device on pipitit server.
     *
     * @param email email to be set on pipitit server for this device
     *
     * @throws NullPointerException if email is null or empty
     */
    public void setEmail(final String email) throws NullPointerException {
        if (email == null || email.isEmpty())
            throw new NullPointerException("Email can not be null or empty!");
        if (device == null) {
            final DeviceCreateRequest deviceCreateRequest =
                    new DeviceCreateRequest(mAppContext, urlCreate);
            new PipititApiRequest(new VolleyResponse() {
                @Override
                public void onSuccess(String response) {
                    DeviceCreateResponse apiResponse = null;
                    try {
                        apiResponse = new Gson().fromJson(response, DeviceCreateResponse.class);
                    } catch (Exception e) {
                        PipititLogger.e(TAG, "deviceCreateRequest:Gson().fromJson", e);
                    }
                    if (apiResponse != null && apiResponse.getId() > 0) {
                        populateDevice(apiResponse.getDeviceId(), apiResponse.getToken(),
                                apiResponse.getWSNode(), email,
                                apiResponse.getUsername(), apiResponse.getId());
                        updateDevice();
                    }
                    PipititLogger.d(TAG, response);
                }

                @Override
                public void onError(int statusCode, String message) {
                }
            }).addJsonRequest(deviceCreateRequest, deviceCreateRequest.getUrl(), Request.Method.PUT,
                    mAppContext);
        } else {
            device.setEmail(email);
            updateDevice();
        }
    }

    /**
     * Set Firebase push token, username and email for this device on pipitit server. If set some
     * parameter to null all parameter will be deleted on pipitit server.
     *
     * @param pushToken firebase push token to be set on pipitit server for this device
     * @param username username to be set on pipitit server for this device
     * @param email email to be set on pipitit server for this device
     */
    public void setDeviceData(final String pushToken, final String username, final String email) {
        if (device == null) {
            final DeviceCreateRequest deviceCreateRequest =
                    new DeviceCreateRequest(mAppContext, urlCreate);
            new PipititApiRequest(new VolleyResponse() {
                @Override
                public void onSuccess(String response) {
                    DeviceCreateResponse apiResponse = null;
                    try {
                        apiResponse = new Gson().fromJson(response, DeviceCreateResponse.class);
                    } catch (Exception e) {
                        PipititLogger.e(TAG, "deviceCreateRequest:Gson().fromJson", e);
                    }
                    if (apiResponse != null && apiResponse.getId() > 0) {
                        populateDevice(apiResponse.getDeviceId(), pushToken,
                                apiResponse.getWSNode(), email,
                                username, apiResponse.getId());
                        updateDevice();
                    }
                    PipititLogger.d(TAG, response);
                }

                @Override
                public void onError(int statusCode, String message) {
                }
            }).addJsonRequest(deviceCreateRequest, deviceCreateRequest.getUrl(), Request.Method.PUT,
                    mAppContext);
        } else {
            device.setEmail(email);
            updateDevice();
        }
    }

    public boolean isAppForegrounded() {
        return activityHandler != null && activityHandler.isAppForegrounded();
    }

    public boolean isActivityStackEmpty() {
        return activityHandler != null && activityHandler.getStackCount() == 0;
    }

    public Activity getActivity() {
        return activityHandler != null ? activityHandler.getActivity() : null;
    }
}
