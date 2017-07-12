package com.hyperether.pipitit.cache;

import android.util.Log;

import com.hyperether.pipitit.util.Constants;

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
                if (debugLogging()) {
                    Log.v(Constants.PIPITIT_TAG + TAG, message);
                }
            }
        });
    }

    public static void d(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging()) {
                    Log.d(Constants.PIPITIT_TAG + TAG, message);
                }
            }
        });
    }

    public static void i(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging()) {
                    Log.i(Constants.PIPITIT_TAG + TAG, message);
                }
            }
        });
    }

    public static void w(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging()) {
                    Log.w(Constants.PIPITIT_TAG + TAG, message);
                }
            }
        });
    }

    public static void e(final String TAG, final String message, final Throwable t) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                if (debugLogging()) {
                    Log.e(Constants.PIPITIT_TAG + TAG, message, t);
                }
            }
        });
    }

    public static void e(final String TAG, final String message) {
        PipititThread.getInstance().getmLoggerHandler().post(new Runnable() {
            @Override
            public void run() {
                Log.e(Constants.PIPITIT_TAG + TAG, message);

            }
        });
    }
}
