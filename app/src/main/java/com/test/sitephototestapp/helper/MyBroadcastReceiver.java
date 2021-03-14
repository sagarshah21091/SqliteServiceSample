package com.test.sitephototestapp.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.test.sitephototestapp.MainActivity;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(ConnectivityManager.CONNECTIVITY_ACTION.equalsIgnoreCase(action))
        {
            if(context instanceof MainActivity)
                ((MainActivity) context).onNetworkChange();
        }
        if (Utils.ACTION_INSERT_EMP_DATA.equals(action)) {
            if(context instanceof MainActivity)
                ((MainActivity) context).setRandomEmpCount();
        }
        else if (Utils.ACTION_INSERT_LOC_DATA.equals(action)) {
            if(context instanceof MainActivity)
                ((MainActivity) context).setRandomLocCount();
        }
    }
}
