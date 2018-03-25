package com.ruslanlyalko.snoopyapp.presentation.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.expenses.ExpensesActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.messages.details.MessageDetailsActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.mk.MkTabActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.report.ReportActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.splash.SplashActivity;

import java.util.Map;
import java.util.Random;

/**
 * Created by Ruslan Lyalko
 * on 21.01.2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String GROUP_MESSAGES_ID = "group_private_id";
    private static final String GROUP_MESSAGES_NAME = "Повідомлення";
    private static final String GROUP_NOTIFICATIONS_ID = "group_users_id";
    private static final String GROUP_NOTIFICATIONS_NAME = "Сповіщення";
    private static final String CHANEL_COMMENT_ID = "chanel_comment_id";
    private static final String CHANEL_COMMENT_DESC = "Коментарі";
    private static final String CHANEL_REPORT_ID = "chanel_report_id";
    private static final String CHANEL_REPORT_DESC = "Звіти";
    private static final String CHANEL_EXPENSE_ID = "chanel_expense_id";
    private static final String CHANEL_EXPENSE_DESC = "Витрати";
    private static final String CHANEL_MK_ID = "chanel_mk_id";
    private static final String CHANEL_MK_DESC = "Майстер-класи";
    private static final String CHANEL_DEFAULT_ID = "chanel_default_id";
    private static final String CHANEL_DEFAULT_DESC = "Інше";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> payload = remoteMessage.getData();
            showNotification(payload);
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification(final Map<String, String> payload) {
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(payload.get("title"));
        builder.setContentText(payload.get("message"));
        builder.setTicker(payload.get("message"));
        builder.setSmallIcon(R.drawable.ic_stat_main);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        builder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        builder.setAutoCancel(true);
        builder.setVibrate(new long[]{0,
                200, 200,
                200, 300,
                100, 100,
                100, 100,
                100, 100
        });
        Intent resultIntent;
        NotificationType notificationType = NotificationType.findByKey(payload.get("type"));
        String chanelId;
        switch (notificationType) {
            case REPORT:
                resultIntent = ReportActivity.getLaunchIntent(this,
                        payload.get("reportDate"),
                        payload.get("reportUserName"),
                        payload.get("reportUserId"));
                chanelId = CHANEL_REPORT_ID;
                break;
            case COMMENT:
                resultIntent = MessageDetailsActivity.getLaunchIntent(this, payload.get("messageKey"));
                chanelId = CHANEL_COMMENT_ID;
                break;
            case EXPENSE:
                resultIntent = ExpensesActivity.getLaunchIntent(this);
                chanelId = CHANEL_EXPENSE_ID;
                break;
            case MK:
                resultIntent = MkTabActivity.getLaunchIntent(this);
                chanelId = CHANEL_MK_ID;
                break;
            default:
                resultIntent = SplashActivity.getLaunchIntent(this);
                chanelId = CHANEL_DEFAULT_ID;
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerChanelAndGroups();
                builder.setBadgeIconType(Notification.BADGE_ICON_SMALL);
                builder.setNumber(1);
                builder.setChannelId(chanelId);
            }
            notificationManager.notify(new Random().nextInt(250), builder.build());
        }
    }

    private void registerChanelAndGroups() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //group
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_MESSAGES_ID,
                    GROUP_MESSAGES_NAME));
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_NOTIFICATIONS_ID,
                    GROUP_NOTIFICATIONS_NAME));
            //comment
            createNotificationChannel(CHANEL_COMMENT_ID,
                    CHANEL_COMMENT_DESC, CHANEL_COMMENT_DESC, GROUP_MESSAGES_ID);
            //expense
            createNotificationChannel(CHANEL_EXPENSE_ID,
                    CHANEL_EXPENSE_DESC, CHANEL_EXPENSE_DESC, GROUP_NOTIFICATIONS_ID);
            //report
            createNotificationChannel(CHANEL_REPORT_ID,
                    CHANEL_REPORT_DESC, CHANEL_REPORT_DESC, GROUP_NOTIFICATIONS_ID);
            //mk
            createNotificationChannel(CHANEL_MK_ID,
                    CHANEL_MK_DESC, CHANEL_MK_DESC, GROUP_NOTIFICATIONS_ID);
            //default
            createNotificationChannel(CHANEL_DEFAULT_ID,
                    CHANEL_DEFAULT_DESC, CHANEL_DEFAULT_DESC, GROUP_NOTIFICATIONS_ID);
        }
    }

    public void createNotificationChannel(String id, String name, String desc, String groupId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (notificationManager.getNotificationChannel(id) != null) return;
            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationChannel.setShowBadge(true);
            notificationChannel.setBypassDnd(true);
            notificationChannel.setVibrationPattern(new long[]{0, 200, 200, 200, 300, 100, 100, 100, 100, 100, 100});
            notificationChannel.setDescription(desc);
            notificationChannel.setGroup(groupId);
//        notificationChannel.setSound();
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
