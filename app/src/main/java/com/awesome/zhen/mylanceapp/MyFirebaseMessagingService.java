package com.awesome.zhen.mylanceapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by zhen on 3/13/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private static final String TAG = "MyFirebaseMsgService";
    private DatabaseReference mFollower;
    private FirebaseAuth mAuth;
    private String title, body, blogpost;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "FROM:" + remoteMessage.getFrom());

        //Check if message contains data
        if(remoteMessage.getData().size()>0){
            Log.d(TAG, "Message data: " + remoteMessage.getData().get("blogpost"));
            blogpost = remoteMessage.getData().get("blogpost");
        }

        //Check if message contains notification
        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Message body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Message body: " + remoteMessage.getNotification().getTitle());
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        sendNotification(body, title, blogpost);
    }

    private void sendNotification(String body, String title, String blogpost){

            Intent intent = new Intent(this,OtherUser.class);
            intent.putExtra("blog_id", blogpost);
            Log.d(TAG, "blog_id: " + blogpost);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = new
                    NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.red_love)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        }

}
