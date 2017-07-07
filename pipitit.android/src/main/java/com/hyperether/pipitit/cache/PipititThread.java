package com.hyperether.pipitit.cache;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Pipitit handlers class
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class PipititThread {

    private static final String LOGGER_THREAD = "pipitit-log";
    private static final String WEB_SOCKET_THREAD = "pipitit-web-socket";
    private static PipititThread INSTANCE;

    private Handler mLoggerHandler;
    private Handler mWebSocketHandler;

    private PipititThread() {
    }

    public static synchronized PipititThread getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PipititThread();
        return INSTANCE;
    }

    @Override
    protected void finalize() throws Throwable {
        clear();
        super.finalize();
    }

    public static void clear() {
        try {
            clearList();
        } finally {
            INSTANCE = null;
        }
    }

    private static void clearList() {
        if (INSTANCE != null) {
            if (INSTANCE.mLoggerHandler != null) {
                INSTANCE.mLoggerHandler.getLooper().quit();
                INSTANCE.mLoggerHandler = null;
            }
            if (INSTANCE.mWebSocketHandler != null) {
                INSTANCE.mWebSocketHandler.getLooper().quit();
                INSTANCE.mWebSocketHandler = null;
            }
        }
    }

    public Handler getmLoggerHandler() {
        if (mLoggerHandler == null) {
            HandlerThread imThread = new HandlerThread(LOGGER_THREAD);
            imThread.start();
            mLoggerHandler = new Handler(imThread.getLooper());
        }
        return mLoggerHandler;
    }

    public Handler getmWebSocketHandler() {
        if (mWebSocketHandler == null) {
            HandlerThread imThread = new HandlerThread(WEB_SOCKET_THREAD);
            imThread.start();
            mWebSocketHandler = new Handler(imThread.getLooper());
        }
        return mWebSocketHandler;
    }
}
