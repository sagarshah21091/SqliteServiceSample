package com.test.sitephototestapp.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.test.sitephototestapp.BuildConfig;

public class Utils {
    public static final String ACTION_INSERT_EMP_DATA = BuildConfig.APPLICATION_ID+".ACTION_INSERT_EMP_DATA";
    public static final String ACTION_INSERT_LOC_DATA = BuildConfig.APPLICATION_ID+".ACTION_INSERT_LOC_DATA";
    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, 12)
                        .show();
            } else {
                Log.e("Google Paly Service", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }
}
