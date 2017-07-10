package com.hyperether.pipitit.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.data.CampaignMessage;
import com.hyperether.pipitit.notification.dialogs.PipititBaseDialog;
import com.hyperether.pipitit.util.Constants;

import java.util.LinkedList;

/**
 * Class for managing incoming notifications
 *
 * @author Slobodan Prijic
 * @version 1.0 - 4/27/2017
 */
public class NotificationHandler {
    public static final String TAG = "NotificationHandler";

    private static NotificationHandler instance;
    private final LinkedList<CampaignMessage> messageList = new LinkedList<>();
    private int notificationID = 0;

    public static NotificationHandler getInstance() {
        if (instance == null)
            instance = new NotificationHandler();
        return instance;
    }

    private NotificationHandler() {

    }

    public int showCustomNotification(Context context, String title, String message) {
        int notId = getNotificationID();
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();

        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constants.KEY_TITLE, title);
        intent.putExtra(Constants.KEY_MESSAGE, message);
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, notId);

        int appIconResId = 0;
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(
                    packageName, PackageManager.GET_META_DATA);
            appIconResId = appInfo.icon;
            if (title == null || title.isEmpty()) {
                title = pm.getApplicationLabel(appInfo).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID,
                intent, PendingIntent.FLAG_ONE_SHOT);

        return showNotification(context, pendingIntent, appIconResId, appIconResId, title, message,
                notId);
    }

    public int showNotification(Context context, String title, String message) {
        int notId = getNotificationID();
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();

        Intent intent = new Intent(packageName + Constants.BROADCAST_ON_PUSH_CLICKED);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(context, PipititReceiver.class);
        intent.putExtra(Constants.KEY_TITLE, title);
        intent.putExtra(Constants.KEY_MESSAGE, message);
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, notId);

        int appIconResId = 0;
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(
                    packageName, PackageManager.GET_META_DATA);
            appIconResId = appInfo.icon;
            if (title == null || title.isEmpty()) {
                title = pm.getApplicationLabel(appInfo).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID,
                intent, PendingIntent.FLAG_ONE_SHOT);

        return showNotification(context, pendingIntent, appIconResId, appIconResId, title, message,
                notId);
    }

    public int showNotification(Context context, PendingIntent pendingIntent, int iconSmall,
                                int iconLarge,
                                String title, String message, int notificationID) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
                .addLine(message);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentText(message)
                .setSmallIcon(iconSmall)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setCategory(Notification.CATEGORY_CALL)
                .setLights(Color.BLUE, 1000, 2000)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setGroupSummary(true)
                .setTicker(title)
                .setGroup(title)
                .setContentTitle(title)
                .setStyle(inboxStyle);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), iconLarge);
        if (largeIcon != null) {
            notificationBuilder.setLargeIcon(largeIcon);
        }

        Notification notification = notificationBuilder.build();
//        // TODO: turn this on for call
//        notification.flags &= Notification.FLAG_ONGOING_EVENT;
        notification.flags &= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        NotificationManager mNM = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.notify(notificationID, notification);
        return notificationID;
    }

    public void removeNotification(Context context, int notId) {
        NotificationManager mNM = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.cancel(notId);
    }

    private int getNotificationID() {
        int n = notificationID;

        notificationID++;
        if (notificationID >= Integer.MAX_VALUE)
            notificationID = 0;

        return n;
    }

    public void addCampaignMessage(CampaignMessage campaignMessage) {
        messageList.add(campaignMessage);
    }

    public CampaignMessage makeCampaignMessage(String message, String title, int notId) {
        CampaignMessage campaignMessage = new CampaignMessage();
        CampaignMessage.Payload payload = campaignMessage.new Payload(message);
        campaignMessage.setPayload(payload);
        campaignMessage.setNotificationID(notId);
        return campaignMessage;
    }

    public void checkIfNeedToShowCampaignMessage(final Activity activity) {
        final CampaignMessage campaignMessage = messageList.poll();
        if (campaignMessage != null) {
            showCampaignMessage(activity, campaignMessage);
        }
    }

    public void showCampaignMessage(final Activity activity,
                                    final CampaignMessage campaignMessage) {
        if (activity == null) {
            PipititLogger.e(TAG, "Show campaign message but activity is null");
            return;
        }

        if (campaignMessage == null) {
            PipititLogger.e(TAG, "Show campaign message but message is null");
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new PipititBaseDialog(activity, false, campaignMessage).show();
            }
        });
    }
}
