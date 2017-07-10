package com.hyperether.pipitit.websocket;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.hyperether.pipitit.PipititManager;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.cache.PipititThread;
import com.hyperether.pipitit.util.Constants;
import com.hyperether.pipitit.util.NetworkConnection;
import com.hyperether.pipitit.websocket.model.ByeMessage;
import com.hyperether.pipitit.websocket.model.Message;
import com.hyperether.pipitit.websocket.model.PingMessage;
import com.hyperether.pipitit.websocket.model.RegisterMessage;
import com.hyperether.pipitit.websocket.model.ServerResponseMessage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

/**
 * Class is used for connection to Pipitit WebServer.
 *
 * @author Nebojsa Brankovic
 * @author Slobodan Prijic
 * @version 1.0 - 4/27/2017
 */
class PipititWebSocketClient extends HandlerThread {

    private static final String TAG = PipititWebSocketClient.class.getName();
    private static final long PING_INTERVAL = 3000000;
    private static final long REMOVE_OLD_MESSAGE_TIME = 30000;
    private static final long RESPONSE_TIME = 5000;

    private final LinkedList<Message> wsSendQueue;
    private final Map<String, Message> wsMap;
    private final Handler handler;
    private WebSocketConnection mWebSocket;
    private WebSocketStatus status = WebSocketStatus.NONE;
    private WebSocketEvent mWebSocketEvent;
    private Context mContext;
    private int messageId;
    private boolean disconnectInitiate = false;
    private long lastTimeMessageReceived = 0;
    private boolean pingSent = false;
    private long lastTimePing = 0;
    private String wsUrl;
    private Runnable sendMessageRunnable = new Runnable() {
        @Override
        public void run() {
            startSendingMessage();
        }
    };

    public PipititWebSocketClient(WebSocketEvent webSocketEvent, Context context, String wsUrl) {
        super("WEB-SOCKET");
        start();
        if (Looper.myLooper() == null)
            Looper.prepare();
        handler = new Handler(this.getLooper());
        this.mWebSocketEvent = webSocketEvent;
        this.wsMap = new HashMap<>();
        this.mContext = context;
        this.wsUrl = wsUrl;
        wsSendQueue = new LinkedList<>();
        messageId = 0;
        System.setProperty("https.protocols", "SSLv3");
    }

    /**
     * Method to connect to Pipitit web server
     */
    private void connectToWebServer() {
        if (NetworkConnection.hasNetworkConnection(mContext)) {
            try {
                if (mWebSocket != null) {
                    if (mWebSocket.isConnected() && !mWebSocket.isReaderNull() &&
                            !mWebSocket.isWriterNull()) {
                        PipititLogger.d(TAG, " WebSocket is already connected!!!");
                        return;
                    }
                    try {
                        PipititLogger.d(TAG, "WebSocket exist, disconnecting");
                        mWebSocket.disconnect();
                    } catch (Exception e) {
                        PipititLogger.e(TAG, "WebSocket error disconnecting existing socket", e);
                    }
                }
                mWebSocket = new WebSocketConnection();

                mWebSocket.connect(wsUrl,
                        new String[]{"pipitit"},
                        new WebSocket.ConnectionHandler() {
                            @Override
                            public void onOpen() {
                                lastTimeMessageReceived = SystemClock.elapsedRealtime();
                                PipititLogger.d(TAG, "onOpen");
                                mWebSocketEvent.onWebSocketOpen();
                                status = WebSocketStatus.CONNECTED;
                                startClient();
                                handler.removeCallbacks(sendMessageRunnable);
                                handler.post(sendMessageRunnable);
                            }

                            @Override
                            public void onClose(int code, String reason) {
                                PipititLogger.d(TAG, "DISCONNECT");
                                mWebSocketEvent.onWebSocketClose(code, reason);
                                status = WebSocketStatus.DISCONNECTED;
                                if (disconnectInitiate) {
                                    clearQueue();
                                    handler.removeCallbacks(sendMessageRunnable);
                                }
                            }

                            @Override
                            public void onTextMessage(String payload) {
                                PipititLogger.d(TAG, "payload = " + payload);
                                procesMessage(payload);
                                lastTimeMessageReceived = SystemClock.elapsedRealtime();
                            }

                            @Override
                            public void onRawTextMessage(byte[] payload) {
                            }

                            @Override
                            public void onBinaryMessage(byte[] payload) {
                            }
                        });
                status = WebSocketStatus.CONNECTING;
            } catch (WebSocketException e) {
                e.printStackTrace();
                PipititLogger.e(TAG, "connect", e);
            }
        } else {
            status = WebSocketStatus.DISCONNECTED;
            PipititLogger.w(TAG, "No Network !!!");
        }
    }

    private void procesMessage(String payload) {
        ServerResponseMessage responseMessage =
                new Gson().fromJson(payload, ServerResponseMessage.class);

        String ack = responseMessage.getAck();
        if (ack != null && !ack.isEmpty()) {
            Message m = wsMap.get(ack);
            if (m != null)
                m.setStatus(Message.Status.ACKNOWLEDGED);
            return;
        }
        String type = responseMessage.getType();
        if (type != null) {
            switch (type) {
                case Constants.SERVER_MESSAGE_TYPE_REGISTER: {
                    mWebSocketEvent.onWebSocketMessage(payload);
                    Message m = wsMap.get(type);
                    if (m != null)
                        m.setStatus(Message.Status.ACKNOWLEDGED);
                }
                case Constants.SERVER_MESSAGE_TYPE_PONG: {
                    pingSent = false;
                    Message m = wsMap.get(type);
                    if (m != null)
                        m.setStatus(Message.Status.ACKNOWLEDGED);
                }
            }
        }
    }

    public void connect() {
        disconnectInitiate = false;
        startClient();
    }

    public void disconnect() {
        disconnectInitiate = true;
        executeCommand(new ByeMessage());
    }

    private void startClient() {
        switch (status) {
            case DISCONNECTED: {
                if (!disconnectInitiate)
                    connectToWebServer();
                break;
            }
            case NONE: {
                //connect to server
                connectToWebServer();
                break;
            }
            case CONNECTING: {
                waitJob();
                startClient();
                break;
            }
            case CONNECTED: {
                //register to server
                register();
                break;
            }
            case REGISTER: {
                //do nothing
                break;
            }
        }
    }

    private void waitJob() {
        try {
            PipititLogger.d(TAG, "waitJob START");
            Thread.currentThread();
            Thread.sleep(1000);
            PipititLogger.d(TAG, "waitJob END");
        } catch (InterruptedException e) {
            e.printStackTrace();
            PipititLogger.e(TAG, "waitJob", e);
        }
    }

    private void register() {
        RegisterMessage registerMessage = new RegisterMessage(
                String.valueOf(PipititManager.getInstance().getDevice().getId()));
        executeCommand(registerMessage);
    }

    public void executeCommand(final Message object) {
        PipititThread.getInstance().getmWebSocketHandler().post(new Runnable() {
            @Override
            public void run() {
                if (object == null)
                    throw new NullPointerException(
                            "Object which need to be sent to web server can not be NULL");
                sendMessage(object);
            }
        });
    }

    private void sendMessage(final Message object) {
        PipititThread.getInstance().getmWebSocketHandler().post(new Runnable() {
            @Override
            public void run() {
                synchronized (wsSendQueue) {
                    PipititLogger.d(TAG,
                            "Putting message in queue: " + object.makeJsonObject().toString());
                    if (object.isFirstPlace())
                        wsSendQueue.addFirst(object);
                    else
                        wsSendQueue.add(object);
                }
            }
        });
    }

    private void sendSocketMessage(String message) {
        try {
            if (mWebSocket != null && message != null) {
                mWebSocket.sendTextMessage(message);
                PipititLogger.d(TAG, "C->WSS: " + message);
            }
        } catch (Exception e) {
            PipititLogger.e(TAG, "error sending socket message", e);
        }
    }

    private void sendMessageFromQueue() {
        synchronized (wsSendQueue) {
            long timeNow = SystemClock.elapsedRealtime();
            for (int i = 0; i < wsSendQueue.size(); i++) {
                Message message = wsSendQueue.get(i);
                Message.Status status = message.getStatus();
                switch (status) {
                    case SENT: {
                        if (timeNow - message.getSendTime() > REMOVE_OLD_MESSAGE_TIME) {
                            wsSendQueue.remove(i);
                            i--;
                            wsMap.remove(message.getSequence());
                        }
                        break;
                    }
                    case ACKNOWLEDGED: {
                        wsSendQueue.remove(i);
                        i--;
                        wsMap.remove(message.getSequence());
                        break;
                    }
                    case WAITING: {
                        long id = messageId++;
                        message.setSendTime(timeNow);
                        message.setSequence(String.valueOf(id));
                        wsMap.put(message.getSequence(), message);
                        message.setStatus(Message.Status.SENT);
                        sendSocketMessage(message.makeJsonObject().toString());
                        break;
                    }
                }
            }
        }
        handlePing(SystemClock.elapsedRealtime());
    }

    private void handlePing(long timeNow) {
        if (mWebSocket != null && mWebSocket.isConnected()) {
            long difference = timeNow - lastTimeMessageReceived;
            if (difference > PING_INTERVAL) {
                if (!pingSent) {
                    PingMessage pingMessage = new PingMessage();
                    executeCommand(pingMessage);
                    pingSent = true;
                    lastTimePing = timeNow;
                } else if (timeNow - lastTimePing > RESPONSE_TIME) {
                    if (mWebSocket != null) {
                        PipititLogger.e(TAG, "Ping missed, reconnecting.... ");
                        mWebSocket.disconnect();
                    }
                }
            }
        }
    }

    void clearQueue() {
        synchronized (wsSendQueue) {
            wsSendQueue.clear();
            wsMap.clear();
        }
    }

    private void startSendingMessage() {
        sendMessageFromQueue();
        handler.postDelayed(sendMessageRunnable, 500);
    }

    private enum WebSocketStatus {
        CONNECTING,
        CONNECTED,
        REGISTER,
        DISCONNECTED,
        NONE;
    }
}
