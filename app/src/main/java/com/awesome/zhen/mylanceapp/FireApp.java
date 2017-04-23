package com.awesome.zhen.mylanceapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by zhen on 2/11/2017.
 */

public class FireApp extends Application{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //Firebase.setAndroidContext(this);

        if(!FirebaseApp.getApps(this).isEmpty()){

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        FirebaseApp.initializeApp(this);
        Firebase.setAndroidContext(this);
        AppEventsLogger.activateApp(this);
    }
}
