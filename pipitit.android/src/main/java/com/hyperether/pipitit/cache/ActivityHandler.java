package com.hyperether.pipitit.cache;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.hyperether.pipitit.notification.NotificationHandler;

import java.util.HashMap;

/**
 * Class for managing activity lifecycle
 *
 * @author Slobodan Prijic
 * @version 1.0 - 4/27/2017
 */
public class ActivityHandler implements Application.ActivityLifecycleCallbacks {
    private static final int IN_FOREGROUND = 2;
    private static final int IN_BACKGROUND = 1;
    private static final String TAG = ActivityHandler.class.getSimpleName();
    private Activity activity;
    private static ActivityHandler instance;
    private volatile int applicationStatus = IN_BACKGROUND;

    private HashMap<Activity, Integer> activities = new HashMap<>();

    private ActivityHandler() {
    }

    public static ActivityHandler getInstance() {
        if (instance == null)
            instance = new ActivityHandler();
        return instance;
    }

    private void addActivity(Activity a) {
        activities.put(a, 1);
    }

    private void removeActivity(Activity a) {
        activities.remove(a);
        PipititLogger.d(TAG, "removeActivity: " + " count = " + getStackCount() + "   activity: " +
                a.getClass().toString());
    }

    public int getStackCount() {
        return activities.size();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        addActivity(activity);
        applicationStatus = IN_FOREGROUND;
        PipititLogger.d(TAG, "applicationStatus = " + IN_FOREGROUND);
        PipititLogger.d(TAG, "onActivityStarted: " + " count = " + getStackCount() +
                "   activity: " + activity.getClass().toString());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.activity = activity;
        PipititLogger.d(TAG, "activity is " + activity.getClass().toString());
        NotificationHandler.getInstance().checkIfNeedToShowCampaignMessage(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        this.activity = null;
        PipititLogger.d(TAG, "activity is null ");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        removeActivity(activity);
        if (activities.size() == 0) {
            applicationStatus = IN_BACKGROUND;
            PipititLogger.d(TAG, "applicationStatus = " + IN_BACKGROUND);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public int getApplicationStatus() {
        return applicationStatus;
    }

    public boolean isAppForegrounded() {
        return applicationStatus == IN_FOREGROUND;
    }

    public boolean isAppBackgrounded() {
        return applicationStatus == IN_BACKGROUND;
    }

    public Activity getActivity() {
        return activity;
    }
}
