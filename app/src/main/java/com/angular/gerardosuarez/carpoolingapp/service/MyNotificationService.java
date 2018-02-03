package com.angular.gerardosuarez.carpoolingapp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.angular.gerardosuarez.carpoolingapp.R;
import com.angular.gerardosuarez.carpoolingapp.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sebastian on 16/01/18.
 */

public class MyNotificationService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = null;
        String notificationBody = null;
        String dataTitle = null;
        String dataMessage = null;

        if(remoteMessage.getData().size()>0){

            System.out.println("Texto payload "+remoteMessage.getData().get("text"));
            dataTitle = remoteMessage.getData().get("title");
            dataMessage = remoteMessage.getData().get("text");
        }
        if(remoteMessage.getNotification()!=null){

            System.out.println("Cuerpo "+remoteMessage.getNotification().getBody()+" title "+remoteMessage.getNotification().getTitle());

            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();


        }

        enviarNotificacion(notificationTitle,notificationBody,dataTitle,dataMessage);

        Log.e(TAG, remoteMessage.getNotification().getBody());

    }

    private void enviarNotificacion(String notificationTitle, String notificationBody, String dataTitle, String dataMessage) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title", dataTitle);
        intent.putExtra("message", dataMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    }

