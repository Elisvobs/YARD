package com.elisvobs.yard;

import android.app.Application;

public class YardApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        com.google.firebase.FirebaseApp.initializeApp(getApplicationContext());
    }
}