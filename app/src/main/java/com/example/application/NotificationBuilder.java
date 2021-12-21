package com.example.application;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationBuilder extends BroadcastReceiver {

    private final static AtomicInteger c = new AtomicInteger(1);

    final static int notificationID = 2;

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    final static String channelID = "channel1";
    final static String titleExtra = "titleExtra";
    final static String messageExtra = "messageExtra";
    final static String jsonDataToNotification = "jsonDataToNotification";
    final static String jsonDataFromNotification = "jsonDataFromNotification";


    public static int getID() {
        return c.incrementAndGet();
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(intent.getStringExtra(titleExtra))
                //.setContentText(intent.getStringExtra(messageExtra))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(intent.getStringExtra(messageExtra)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .build();

        Intent intentData = new Intent(context, AddActivity.class);
        intentData.putExtra(jsonDataFromNotification, intent.getStringExtra(jsonDataToNotification));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intentData, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        notification.contentIntent = pendingIntent;

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(getID(), notification);

    }
}
