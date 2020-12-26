package com.aayaffe.sailingracecoursemanager.general;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.activities.GoogleMapsActivity;

/**
 * Avi Marine Innovations - www.avimarine.in
 * <p>
 * Created by Amit Y. on 04/10/2015.
 */
public class Notification {
    private NotificationManager mNotifyMgr;


    public void InitNotification(Context c) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c, "AVISAILRACE").
                setSmallIcon(R.mipmap.sailingracecoursemanager_white_ic).
                setContentTitle("AVI is running!").
                setContentText("The app is sending and receiving data.");

        Intent resultIntent = new Intent(c, GoogleMapsActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(c, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        // Sets an ID for the notification
//        String id = "avi_channel_01";
        int mNotificationId = 1;
// Gets an instance of the NotificationManager service
        mNotifyMgr =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    public void cancelAll() {
        mNotifyMgr.cancelAll();
    }
}
