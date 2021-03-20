package com.test.sitephototestapp;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.test.sitephototestapp.helper.AppLifecycleObserver;

public class MyApplication extends Application
{
    AppLifecycleObserver appLifecycleObserver;
    @Override
    public void onCreate() {
        super.onCreate();
        appLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }
}
