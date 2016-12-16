package com.aayaffe.sailingracecoursemanager.general;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Map_Layer.GoogleMapsActivity;

/**
 * Created by aayaffe on 04/10/2015.
 */
public class Notification {
    private NotificationManager mNotifyMgr;


    public void InitNotification(Context c){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c).setSmallIcon(R.mipmap.sailingracecoursemanager_white_ic).setContentTitle("AVI is running!").setContentText("The app is sending and receiving data.");
        Intent resultIntent = new Intent(c,GoogleMapsActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(c,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        // Sets an ID for the notification
        int mNotificationId = 1;
// Gets an instance of the NotificationManager service
        mNotifyMgr =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public void cancelAll(){
        mNotifyMgr.cancelAll();
    }
}
