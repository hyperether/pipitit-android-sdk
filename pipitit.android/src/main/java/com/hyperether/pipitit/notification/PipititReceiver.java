package com.hyperether.pipitit.notification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.hyperether.pipitit.cache.ActivityHandler;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.data.CampaignMessage;
import com.hyperether.pipitit.util.Constants;

/**
 * Receive pipitit events
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 5/18/2017
 */
public final class PipititReceiver extends BroadcastReceiver {

    private static final String TAG = PipititReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        PipititLogger.d(TAG, "onReceive " + intent);
        if (intent != null && (context.getPackageName() + Constants.BROADCAST_ON_PUSH_CLICKED)
                .equalsIgnoreCase(intent.getAction())) {
            PipititLogger.d(TAG, "User clicked on notification");
            final Activity activity = ActivityHandler.getInstance().getActivity();
            CampaignMessage campaignMessage = NotificationHandler.getInstance()
                    .makeCampaignMessage(intent.getStringExtra(Constants.KEY_MESSAGE),
                            intent.getStringExtra(Constants.KEY_MESSAGE),
                            intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, -1));
            if (activity != null) {
                NotificationHandler.getInstance().showCampaignMessage(activity, campaignMessage);
            } else {
                NotificationHandler.getInstance().addCampaignMessage(campaignMessage);
                PipititLogger
                        .d(TAG, "We receive PUSH message but PipititManager is not initiated!!!");
                try {
                    PipititLogger.d(TAG, "Start launch Activity");
                    PackageManager pm = context.getPackageManager();
                    Intent startIntent = pm.getLaunchIntentForPackage(context.getPackageName());
                    startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(startIntent);
                } catch (Exception e) {
                    PipititLogger.e(TAG, "onReceive", e);
                }
            }
        }
    }
}
