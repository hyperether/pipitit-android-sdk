package com.hyperether.pipitit.cache;

import android.util.Log;

/**
 * Pipitit Logger
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class PipititLogger {
    private static boolean sDebugLoggingEnabled;

    public static void enableDebugLogging(boolean enable) {
        sDebugLoggingEnabled = enable;
    }

    public static boolean debugLogging() {
        return sDebugLoggingEnabled;
    }

    public static void v(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging() /*&& Log.isLoggable(TAG, Log.VERBOSE)*/) {
                    Log.v(TAG, message);
                }
            }
        });
    }

    public static void d(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging() /*&& Log.isLoggable(TAG, Log.DEBUG)*/) {
                    Log.d(TAG, message);
                }
            }
        });
    }

    public static void i(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging()/* && Log.isLoggable(TAG, Log.INFO)*/) {
                    Log.i(TAG, message);
                }
            }
        });
    }

    public static void w(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging()/* && Log.isLoggable(TAG, Log.WARN)*/) {
                    Log.w(TAG, message);
                }
            }
        });
    }

    public static void e(final String TAG, final String message, final Throwable t) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging() /*&& Log.isLoggable(TAG, Log.ERROR)*/) {
                    Log.e(TAG, message, t);
                }
            }
        });
    }

    public static void e(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging()/* && Log.isLoggable(TAG, Log.ERROR)*/) {
                    Log.e(TAG, message);
                }
            }
        });
    }
}
