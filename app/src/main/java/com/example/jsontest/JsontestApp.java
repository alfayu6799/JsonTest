package com.example.jsontest;

import android.app.Application;

public class JsontestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ApiProxy.initial(this);
    }
}
