/******************************************************************************
 * Copyright 2011-2012 Tavendo GmbH
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package de.tavendo.autobahn;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Pair;

import com.hyperether.pipitit.cache.PipititLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
 * SSL docs:
 * http://docs.oracle.com/javase/1.5.0/docs/api/javax/net/ssl/SSLEngine.html
 * http://docs.oracle.com/javase/1.5.0/docs/guide/security/jsse/samples/sslengine/SSLEngineSimpleDemo.java
 */
public class WebSocketConnection implements WebSocket {

    private static final String TAG = WebSocketConnection.class.getName();
    protected static MasterHandler mMasterHandler;
    protected WebSocketReader mReader;
    protected WebSocketWriter mWriter;
    protected HandlerThread mWriterThread;
    protected SocketChannel mTransportChannel;
    protected WebSocketOptions mOptions;
    protected SSLEngine mSSLEngine;
    private URI mWsUri;
    private String mWsScheme;
    private String mWsHost;
    private int mWsPort;
    private String mWsPath;
    private String mWsQuery;
    private String[] mWsSubprotocols;
    private List<Pair> mWsHeaders;
    private WebSocket.ConnectionHandler mWsHandler;
    private boolean mActive;
    private boolean mPrevConnected;

    public WebSocketConnection() {
        PipititLogger.d(TAG, "created");

        // create WebSocket master handler
        createHandler();

        // set initial values
        mActive = false;
        mPrevConnected = false;
    }

    protected SSLContext getSSLContext() throws KeyManagementException, NoSuchAlgorithmException {
        if (!mOptions.getVerifyCertificateAuthority()) {
            // Create a trust manager that does not validate certificate chains
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{tm}, null);

            PipititLogger.d(TAG, "trusting all certificates");
            return sslContext;

        } else {
            PipititLogger.d(TAG, "NOT trusting all certificates");
            return SSLContext.getDefault();
        }
    }

    public void sendTextMessage(String payload) {
        mWriter.forward(new WebSocketMessage.TextMessage(payload));
    }

    public void sendRawTextMessage(byte[] payload) {
        mWriter.forward(new WebSocketMessage.RawTextMessage(payload));
    }

    public void sendBinaryMessage(byte[] payload) {
        mWriter.forward(new WebSocketMessage.BinaryMessage(payload));
    }

    public boolean isConnected() {
        return mTransportChannel != null && mTransportChannel.isConnected();
    }

    public boolean isWriterNull() {
        return mWriter == null;
    }

    public boolean isReaderNull() {
        return mReader == null;
    }

    private void failConnection(int code, String reason) {

        PipititLogger.d(TAG, "fail connection [code = " + code + ", reason = " + reason);

        if (mReader != null) {
            mReader.quit();
            try {
                mReader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //mReader = null;
        } else {
            PipititLogger.d(TAG, "mReader already NULL");
        }

        if (mWriter != null) {
            //mWriterThread.getLooper().quit();
            mWriter.forward(new WebSocketMessage.Quit());
            try {
                mWriterThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //mWriterThread = null;
        } else {
            PipititLogger.d(TAG, "mWriter already NULL");
        }

        if (mTransportChannel != null) {
            try {
                mTransportChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //mTransportChannel = null;
        } else {
            PipititLogger.d(TAG, "mTransportChannel already NULL");
        }

        onClose(code, reason);

        PipititLogger.d(TAG, "worker threads stopped");
    }

    public void connect(String wsUri, WebSocket.ConnectionHandler wsHandler)
            throws WebSocketException {
        connect(wsUri, null, wsHandler, new WebSocketOptions(), null);
    }

    public void connect(String wsUri, WebSocket.ConnectionHandler wsHandler,
                        WebSocketOptions options) throws WebSocketException {
        connect(wsUri, null, wsHandler, options, null);
    }

    public void connect(String wsUri, String[] wsSubprotocols,
                        WebSocket.ConnectionHandler wsHandler)
            throws WebSocketException {
        connect(wsUri, wsSubprotocols, wsHandler, new WebSocketOptions(), null);
    }

    public void connect(String wsUri, String[] wsSubprotocols,
                        WebSocket.ConnectionHandler wsHandler, WebSocketOptions options,
                        List<Pair> headers) throws WebSocketException {

        // don't connect if already connected .. user needs to disconnect first
        //
        if (mTransportChannel != null && mTransportChannel.isConnected()) {
            throw new WebSocketException("already connected");
        }

        // parse WebSockets URI
        //
        try {
            mWsUri = new URI(wsUri);

            if (!mWsUri.getScheme().equals("ws") && !mWsUri.getScheme().equals("wss")) {
                throw new WebSocketException("unsupported scheme for WebSockets URI");
            }

            mWsScheme = mWsUri.getScheme();

            if (mWsUri.getPort() == -1) {
                if (mWsScheme.equals("ws")) {
                    mWsPort = 80;
                } else {
                    mWsPort = 443;
                }
            } else {
                mWsPort = mWsUri.getPort();
            }

            if (mWsUri.getHost() == null) {
                throw new WebSocketException("no host specified in WebSockets URI");
            } else {
                mWsHost = mWsUri.getHost();
            }

            if (mWsUri.getRawPath() == null || mWsUri.getRawPath().equals("")) {
                mWsPath = "/";
            } else {
                mWsPath = mWsUri.getRawPath();
            }

            if (mWsUri.getRawQuery() == null || mWsUri.getRawQuery().equals("")) {
                mWsQuery = null;
            } else {
                mWsQuery = mWsUri.getRawQuery();
            }

        } catch (URISyntaxException e) {

            throw new WebSocketException("invalid WebSockets URI");
        }

        mWsSubprotocols = wsSubprotocols;
        mWsHeaders = headers;
        mWsHandler = wsHandler;

        // make copy of options!
        mOptions = new WebSocketOptions(options);

        // set connection active
        mActive = true;

        // use asynch connector on short-lived background thread
        new WebSocketConnector().execute();
    }

    public ConnectionHandler getmWsHandler() {
        return mWsHandler;
    }

    public void setmWsHandler(ConnectionHandler mWsHandler) {
        this.mWsHandler = mWsHandler;
    }

    public void disconnect() {
        if (mWriter != null) {
            mWriter.forward(new WebSocketMessage.Close(1000));
        } else {
            PipititLogger.d(TAG, "could not send Close .. writer already NULL");
        }
//        if (mReader != null) {
//            mReader.quit();
//        } else {
//             PipititLogger.d(TAG, "could not send Close .. reader already NULL");
//        }
        mActive = false;
        mPrevConnected = false;
    }

    /**
     * Reconnect to the server with the latest options
     *
     * @return true if reconnection performed
     */
    public boolean reconnect() {
        if (!isConnected() && (mWsUri != null)) {
            new WebSocketConnector().execute();
            return true;
        }
        return false;
    }

    /**
     * Perform reconnection
     *
     * @return true if reconnection was scheduled
     */
    protected boolean scheduleReconnect() {
        /**
         * Reconnect only if:
         *  - connection active (connected but not disconnected)
         *  - has previous success connections
         *  - reconnect interval is set
         */
        int interval = mOptions.getReconnectInterval();
        boolean need = mActive && mPrevConnected && (interval > 0);
        if (need) {
            PipititLogger.d(TAG, "Reconnection scheduled");
            mMasterHandler.postDelayed(new Runnable() {

                public void run() {
                    PipititLogger.d(TAG, "Reconnecting...");
                    reconnect();
                }
            }, interval);
        }
        return need;
    }

    /**
     * Common close handler
     *
     * @param code Close code.
     * @param reason Close reason (human-readable).
     */
    private void onClose(int code, String reason) {
        boolean reconnecting = false;

        if ((code == ConnectionHandler.CLOSE_CANNOT_CONNECT) ||
                (code == ConnectionHandler.CLOSE_CONNECTION_LOST)) {
            reconnecting = scheduleReconnect();
        }


        if (mWsHandler != null) {
            try {
                if (reconnecting) {
                    mWsHandler.onClose(ConnectionHandler.CLOSE_RECONNECT, reason);
                } else {
                    mWsHandler.onClose(code, reason);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //mWsHandler = null;
        } else {
            PipititLogger.d(TAG, "mWsHandler already NULL");
        }
    }

    /**
     * Create master message handler.
     */
    protected void createHandler() {

        mMasterHandler = new MasterHandler() {

            public void handleMessage(Message msg) {

                if (msg.obj instanceof WebSocketMessage.TextMessage) {

                    WebSocketMessage.TextMessage textMessage =
                            (WebSocketMessage.TextMessage) msg.obj;

                    if (mWsHandler != null) {
                        mWsHandler.onTextMessage(textMessage.mPayload);
                    } else {

                        PipititLogger.d(TAG,
                                "could not call onTextMessage() .. handler already NULL");
                    }

                } else if (msg.obj instanceof WebSocketMessage.RawTextMessage) {

                    WebSocketMessage.RawTextMessage rawTextMessage =
                            (WebSocketMessage.RawTextMessage) msg.obj;

                    if (mWsHandler != null) {
                        mWsHandler.onRawTextMessage(rawTextMessage.mPayload);
                    } else {

                        PipititLogger.d(TAG,
                                "could not call onRawTextMessage() .. handler already NULL");
                    }

                } else if (msg.obj instanceof WebSocketMessage.BinaryMessage) {

                    WebSocketMessage.BinaryMessage binaryMessage =
                            (WebSocketMessage.BinaryMessage) msg.obj;

                    if (mWsHandler != null) {
                        mWsHandler.onBinaryMessage(binaryMessage.mPayload);
                    } else {

                        PipititLogger.d(TAG,
                                "could not call onBinaryMessage() .. handler already NULL");
                    }

                } else if (msg.obj instanceof WebSocketMessage.TriggerWrite) {

                    PipititLogger.d(TAG, "Trigger Write received");

                    // forward trigger to writer
                    mWriter.forward(msg.obj);

                } else if (msg.obj instanceof WebSocketMessage.Ping) {

                    WebSocketMessage.Ping ping = (WebSocketMessage.Ping) msg.obj;
                    PipititLogger.d(TAG, "WebSockets Ping received");

                    // reply with Pong
                    WebSocketMessage.Pong pong = new WebSocketMessage.Pong();
                    pong.mPayload = ping.mPayload;
                    mWriter.forward(pong);

                } else if (msg.obj instanceof WebSocketMessage.Pong) {

                    @SuppressWarnings("unused")
                    WebSocketMessage.Pong pong = (WebSocketMessage.Pong) msg.obj;

                    PipititLogger.d(TAG, "WebSockets Pong received");

                } else if (msg.obj instanceof WebSocketMessage.Close) {

                    WebSocketMessage.Close close = (WebSocketMessage.Close) msg.obj;

                    PipititLogger.d(TAG,
                            "WebSockets Close received (" + close.mCode + " - " + close.mReason +
                                    ")");

                    final int tavendoCloseCode =
                            (close.mCode == 1000) ? ConnectionHandler.CLOSE_NORMAL :
                                    ConnectionHandler.CLOSE_CONNECTION_LOST;

                    if (mActive) {
                        mWriter.forward(new WebSocketMessage.Close(1000));
                    } else {
                        // we've initiated disconnect, so ready to close the channel
                        try {
                            mTransportChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    onClose(tavendoCloseCode, close.mReason);

                } else if (msg.obj instanceof WebSocketMessage.ServerHandshake) {

                    WebSocketMessage.ServerHandshake serverHandshake =
                            (WebSocketMessage.ServerHandshake) msg.obj;

                    PipititLogger.d(TAG, "opening handshake received");

                    if (serverHandshake.mSuccess) {
                        if (mWsHandler != null) {
                            mWsHandler.onOpen();
                        } else {

                            PipititLogger
                                    .d(TAG, "could not call onOpen() .. handler already NULL");
                        }
                    }

                } else if (msg.obj instanceof WebSocketMessage.ConnectionLost) {

                    @SuppressWarnings("unused")
                    WebSocketMessage.ConnectionLost connnectionLost =
                            (WebSocketMessage.ConnectionLost) msg.obj;
                    failConnection(WebSocketConnectionHandler.CLOSE_CONNECTION_LOST,
                            "WebSockets connection lost");

                } else if (msg.obj instanceof WebSocketMessage.ProtocolViolation) {

                    @SuppressWarnings("unused")
                    WebSocketMessage.ProtocolViolation protocolViolation =
                            (WebSocketMessage.ProtocolViolation) msg.obj;
                    failConnection(WebSocketConnectionHandler.CLOSE_PROTOCOL_ERROR,
                            "WebSockets protocol violation");

                } else if (msg.obj instanceof WebSocketMessage.Error) {

                    WebSocketMessage.Error error = (WebSocketMessage.Error) msg.obj;
                    failConnection(WebSocketConnectionHandler.CLOSE_INTERNAL_ERROR,
                            "WebSockets internal error (" + error.mException.toString() + ")");

                } else if (msg.obj instanceof WebSocketMessage.ServerError) {

                    WebSocketMessage.ServerError error = (WebSocketMessage.ServerError) msg.obj;
                    failConnection(WebSocketConnectionHandler.CLOSE_SERVER_ERROR,
                            "Server error " + error.mStatusCode + " (" + error.mStatusMessage +
                                    ")");

                } else {

                    processAppMessage(msg.obj);

                }
            }
        };
    }

    protected void processAppMessage(Object message) {
    }

    /**
     * Create WebSockets background writer.
     */
    protected void createWriter() {

        mWriterThread = new HandlerThread("WebSocketWriter");
        mWriterThread.start();
        mWriter = new WebSocketWriter(mWriterThread.getLooper(), mMasterHandler, mTransportChannel,
                mOptions, mSSLEngine);
        mWriter.maybeStartSSL();

        PipititLogger.d(TAG, "WS writer created and started");
    }

    /**
     * Create WebSockets background reader.
     */
    protected void createReader() {

        mReader =
                new WebSocketReader(mMasterHandler, mTransportChannel, mOptions, "WebSocketReader",
                        mSSLEngine);
        mReader.start();

        PipititLogger.d(TAG, "WS reader created and started");
    }

    public static class MasterHandler extends Handler {
        public boolean mWriterHasData = false;

        public boolean getWriterHasData() {
            synchronized (this) {
                return mWriterHasData;
            }
        }

        public void setWriterHasData(boolean hasWriterData) {
            synchronized (this) {
                mWriterHasData = hasWriterData;
            }
        }
    }

    /**
     * Asynch socket connector.
     */
    private class WebSocketConnector extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            Thread.currentThread().setName("WebSocketConnector");

            // connect TCP socket
            // http://developer.android.com/reference/java/nio/channels/SocketChannel.html
            //
            try {
                mTransportChannel = SocketChannel.open();
                //mTransportChannel.configureBlocking(true);

                // the following will block until connection was established or an error occurred!
                mTransportChannel.socket().connect(new InetSocketAddress(mWsHost, mWsPort),
                        mOptions.getSocketConnectTimeout());

                // before doing any data transfer on the socket, set socket options
                //mTransportChannel.socket().setSoTimeout(mOptions.getSocketReceiveTimeout());
                //mTransportChannel.socket().setTcpNoDelay(mOptions.getTcpNoDelay());

                return null;

            } catch (IOException e) {
                PipititLogger.e(TAG, "IOException", e);
                return e.getMessage();
            } catch (OutOfMemoryError e) {
                PipititLogger.e(TAG, "OutOfMemory for WebSocketConnector", e);
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String reason) {

            if (reason != null) {

                onClose(WebSocketConnectionHandler.CLOSE_CANNOT_CONNECT, reason);

            } else if (mTransportChannel.isConnected()) {

                try {

                    PipititLogger.d(TAG, "WS Scheme: " + mWsScheme);

                    if (mWsScheme.equals("wss")) {
                        SSLContext ctxt = getSSLContext();
                        //mSSLEngine = ctxt.createSSLEngine(mWsHost, mWsPort);
                        mSSLEngine = ctxt.createSSLEngine();
                        mSSLEngine.setUseClientMode(true);
                        PipititLogger.d(TAG, "SSLEngine created");
                    } else {
                        mSSLEngine = null;
                    }

                    // create & start WebSocket reader
                    createReader();

                    // create & start WebSocket writer
                    createWriter();

                    // start WebSockets handshake
                    WebSocketMessage.ClientHandshake hs =
                            new WebSocketMessage.ClientHandshake(mWsHost + ":" + mWsPort);
                    hs.mPath = mWsPath;
                    hs.mQuery = mWsQuery;
                    hs.mSubprotocols = mWsSubprotocols;
                    hs.mHeaderList = mWsHeaders;
                    mWriter.forward(hs);

                    mPrevConnected = true;

                } catch (Exception e) {
                    onClose(WebSocketConnectionHandler.CLOSE_INTERNAL_ERROR,
                            e.getMessage());
                    return;
                } catch (OutOfMemoryError e) {
                    PipititLogger.d(TAG, "OutOfMemory for WebSocketConnector");
                    onClose(WebSocketConnectionHandler.CLOSE_INTERNAL_ERROR,
                            e.getMessage());
                }
            } else {
                onClose(WebSocketConnectionHandler.CLOSE_CANNOT_CONNECT,
                        "Could not connect to WebSocket server");
                return;
            }
        }

    }
}
