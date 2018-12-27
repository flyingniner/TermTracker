package com.termtacker.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.termtacker.R;
import com.termtacker.activities.CourseAlertActivity;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class TermReceiver extends BroadcastReceiver
{
    private static int notificationId;
    private String channelId = "TermTrackerNotice";


    @Override
    public void onReceive(Context context, Intent intent)
    {
        createNotificationChannel(context);


        Notification notification = new NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.ic_notification_24dp)
                .setContentTitle(intent.getStringExtra(CourseAlertActivity.EXTRA_TITLE))
                .setContentText(intent.getStringExtra(CourseAlertActivity.EXTRA_DESCRIPTION)).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId++, notification);

    }

    private void createNotificationChannel(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "TermTrackerNotice";
            String description = "You have a course coming due soon";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId,name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
