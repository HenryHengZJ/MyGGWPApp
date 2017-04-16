package com.awesome.zhen.mylanceapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgeIntentService extends IntentService {

    private int notificationId = 0;

    public BadgeIntentService() {
        super("BadgeIntentService");
    }

    private NotificationManager mNotificationManager;

    private static final String TAG = "Badge";

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int badgeCount = intent.getIntExtra("badgeCount", 0);
            String UserName = intent.getExtras().getString("UserName");
            String OnlyLike = intent.getExtras().getString("OnlyLike");
            String Blogkey = intent.getExtras().getString("Blogkey");
            String Location = intent.getExtras().getString("Location");
            String Category = intent.getExtras().getString("Category");
            String CommentMessage = intent.getExtras().getString("CommentMessage");

            Log.d(TAG, "Blogkey: " + Blogkey);
            Log.d(TAG, "Location: " + Location);
            Log.d(TAG, "Category: " + Category);
            Log.d(TAG, "OnlyLike: " + OnlyLike);
            Log.d(TAG, "UserName: " + UserName);
            Log.d(TAG, "CommentMessage: " + CommentMessage);

            mNotificationManager.cancel(notificationId);
            notificationId++;

            if(OnlyLike.equals("Yes")){
                Intent myIntent = new Intent(BadgeIntentService.this, SinglePost.class);
                myIntent.putExtra("blog_id", Blogkey);
                myIntent.putExtra("location_id", Location);
                myIntent.putExtra("category_id", Category);
                PendingIntent contentIntent = PendingIntent.getActivity(BadgeIntentService.this,
                        0, myIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Notification.Builder builder = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Lance")
                        .setContentText(UserName+" has liked your post.")
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.lance_icon);
                Notification notification = builder.build();
                ShortcutBadger.applyNotification(getApplicationContext(), notification, badgeCount);
                mNotificationManager.notify(notificationId, notification);
            }
            else if (OnlyLike.equals("No")){
                Intent myIntent = new Intent(BadgeIntentService.this, SinglePost.class);
                myIntent.putExtra("blog_id", Blogkey);
                myIntent.putExtra("location_id", Location);
                myIntent.putExtra("category_id", Category);
                PendingIntent contentIntent = PendingIntent.getActivity(BadgeIntentService.this,
                        0, myIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Notification.Builder builder = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Lance")
                        .setContentText(UserName+ CommentMessage)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setSmallIcon(R.mipmap.lance_icon);
                Notification notification = builder.build();
                ShortcutBadger.applyNotification(getApplicationContext(), notification, badgeCount);
                mNotificationManager.notify(notificationId, notification);
            }
        }
    }
}