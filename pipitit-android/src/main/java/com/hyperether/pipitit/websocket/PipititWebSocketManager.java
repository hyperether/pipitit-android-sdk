package com.hyperether.pipitit.websocket;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyperether.pipitit.PipititManager;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.config.PipititConfig;
import com.hyperether.pipitit.util.Constants;
import com.hyperether.pipitit.websocket.model.RegisteredMessage;
import com.hyperether.pipitit.websocket.model.ServerResponseMessage;

/**
 * Manager for Pipitit web socket.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class PipititWebSocketManager implements WebSocketEvent {

    private static final String TAG = PipititWebSocketClient.class.getName();

    private static PipititWebSocketManager INSTANCE;
    private PipititWebSocketClient client;
    private Context mContext;

    private PipititWebSocketManager(Context context) {
        this.mContext = context;
    }

    public static synchronized PipititWebSocketManager getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new PipititWebSocketManager(context);
        return INSTANCE;
    }

    public void startWebSocket(String wsUrl, Context mAppContext) {
        //TODO make logic to keep connected
        if (PipititConfig.isWebSocketEnabled(mAppContext)) {
            client = new PipititWebSocketClient(this, mContext, wsUrl);
            client.connect();
        }
    }

    public static void clear() {
        if (INSTANCE != null) {
            if (INSTANCE.client != null) {
                INSTANCE.client.disconnect();
                INSTANCE.client = null;
            }
            INSTANCE.mContext = null;
        }
        INSTANCE = null;
    }

    @Override
    public void onWebSocketOpen() {
    }

    @Override
    public void onWebSocketMessage(String message) {
        processMessage(message);
    }

    private void processMessage(String message) {
        ServerResponseMessage responseMessage = parseJson(message, ServerResponseMessage.class);
        if (responseMessage != null) {
            switch (responseMessage.getType()) {
                case Constants.SERVER_MESSAGE_TYPE_REGISTER: {
                    RegisteredMessage registerMessage = parseJson(message, RegisteredMessage.class);
                    if (registerMessage != null && PipititManager.isInitiated()) {
                        PipititManager.getInstance().getDevice()
                                .setWSNode(registerMessage.getNodeName());
                    }
                }
            }
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason) {

    }

    @Override
    public void onWebSocketError(String description) {

    }

    private <T> T parseJson(String message, Class<T> classOfT) {
        try {
            return new Gson().fromJson(message, classOfT);
        } catch (JsonSyntaxException e) {
            PipititLogger.e(TAG, "parseJson", e);
        }
        return null;
    }
}
