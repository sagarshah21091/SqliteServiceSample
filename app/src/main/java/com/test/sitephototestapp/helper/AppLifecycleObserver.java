package com.test.sitephototestapp.helper;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class AppLifecycleObserver implements LifecycleObserver {

    public static boolean isAppVisible;
    public static final String TAG = AppLifecycleObserver.class.getName();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        isAppVisible = true;
        Log.e("APP", "onEnterForeground: "+isAppVisible);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        isAppVisible = false;
        Log.e("APP", "onEnterBackground: "+isAppVisible);
    }

}