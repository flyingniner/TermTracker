package com.termtacker.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.termtacker.R;
import com.termtacker.activities.AssessmentAddEditActivity;
import com.termtacker.activities.AssessmentAlertActivity;
import com.termtacker.activities.CourseAddEditActivity;
import com.termtacker.activities.CourseAlertActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TermReceiver extends BroadcastReceiver
{
    private static int notificationId;
    private String channelId = "TermTrackerNotice";


    @Override
    public void onReceive(Context context, Intent intent)
    {
        createNotificationChannel(context);

        if (intent.hasExtra(CourseAlertActivity.EXTRA_TITLE)) {

            Intent data = new Intent(context, CourseAddEditActivity.class);
            data.putExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_ID, intent.getIntExtra(CourseAlertActivity.EXTRA_COURSE_ID, 0));

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, data, 0);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification_24dp)
                    .setContentTitle(intent.getStringExtra(CourseAlertActivity.EXTRA_TITLE))
                    .setContentText(intent.getStringExtra(CourseAlertActivity.EXTRA_DESCRIPTION))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            //(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId++, notification.build());
        }

        if (intent.hasExtra(AssessmentAlertActivity.EXTRA_ASSESSMENT_ID)) {

            Intent data = new Intent(context, AssessmentAddEditActivity.class);
            data.putExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_ID,
                    intent.getIntExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_ID, 0));

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 3, data, 0);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification_24dp)
                    .setContentTitle(intent.getStringExtra(AssessmentAlertActivity.EXTRA_ASSESSMENT_TITLE))
                    .setContentText(intent.getStringExtra(AssessmentAlertActivity.EXTRA_ASSESSMENT_DESC))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            //(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId++, notification.build());
        }


    }

    private void createNotificationChannel(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = context.getString(R.string.notification_name);
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
