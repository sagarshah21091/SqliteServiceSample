package com.test.sitephototestapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper
{
    private static SharedPreferences app_preferences;
    private static PreferenceHelper preferenceHelper = new PreferenceHelper();
    private final String NotificationSettings = "NotificationSettings";
    public static PreferenceHelper getInstance(Context context) {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferenceHelper;
    }

    public void setAskNotificationSettings(boolean isAsked) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putBoolean(NotificationSettings, isAsked);
        editor.apply();
    }

    public boolean isNotificationSettingsAsked() {
        return app_preferences.getBoolean(NotificationSettings, false);
    }
}
