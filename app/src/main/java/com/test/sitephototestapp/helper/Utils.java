package com.test.sitephototestapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.test.sitephototestapp.BuildConfig;

public class Utils {
    public static final String ACTION_INSERT_EMP_DATA = BuildConfig.APPLICATION_ID + ".ACTION_INSERT_EMP_DATA";
    public static final String ACTION_DELETE_EMP_DATA = BuildConfig.APPLICATION_ID + ".ACTION_DELETE_EMP_DATA";
    public static final String ACTION_INSERT_LOC_DATA = BuildConfig.APPLICATION_ID + ".ACTION_INSERT_LOC_DATA";
    public static final String ACTION_DELETE_LOC_DATA = BuildConfig.APPLICATION_ID + ".ACTION_DELETE_LOC_DATA";

    public interface constant {
        String key_last_row_id = "last_row_id";

        interface table {
            int Employee = 1;
            int Location = 2;
        }

        String key_fromTable = "fromTable";
    }

    public static final int THRESHOLD_EMP_COUNT_DISPLAY_LIMIT = -1;
    public static final int THRESHOLD_EMP_DATA_INSERT_MAX = 20000;
    public static final int THRESHOLD_EMP_DATA_INSERT_DEFAULT = 1;

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

    public static void showToast(Activity context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static IntentFilter getEmployeeActions()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utils.ACTION_INSERT_EMP_DATA);
        intentFilter.addAction(Utils.ACTION_DELETE_EMP_DATA);
        return intentFilter;
    }

    public static IntentFilter getLocationActions()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utils.ACTION_INSERT_LOC_DATA);
        intentFilter.addAction(Utils.ACTION_DELETE_LOC_DATA);
        return intentFilter;
    }
}
