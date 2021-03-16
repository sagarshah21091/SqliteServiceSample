package com.test.sitephototestapp.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.test.sitephototestapp.ListActivity;
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
                ((MainActivity) context).setEmployeeCount();
            else if(context instanceof ListActivity)
                ((ListActivity)context).addNewEmployeeData();
        }
        else if(Utils.ACTION_DELETE_EMP_DATA.equals(action))
        {
            if(context instanceof MainActivity)
                ((MainActivity) context).setEmployeeCount();
            else if(context instanceof ListActivity)
            {
                if(intent.getExtras()!=null && intent.getExtras().containsKey(Utils.constant.key_last_row_id))
                {
                    int lastRowId = intent.getExtras().getInt(Utils.constant.key_last_row_id);
                    ((ListActivity) context).removeLastEmployeeData(lastRowId);
                }
            }
        }
        else if (Utils.ACTION_INSERT_LOC_DATA.equals(action)) {
            if(context instanceof MainActivity)
                ((MainActivity) context).setLocationCount();
            else if(context instanceof ListActivity)
                ((ListActivity)context).addNewLocationData();
        }
        else if(Utils.ACTION_DELETE_LOC_DATA.equals(action))
        {
            if(context instanceof MainActivity)
                ((MainActivity) context).setLocationCount();
            else if(context instanceof ListActivity)
            {
                if(intent.getExtras()!=null && intent.getExtras().containsKey(Utils.constant.key_last_row_id))
                {
                    int lastRowId = intent.getExtras().getInt(Utils.constant.key_last_row_id);
                    ((ListActivity) context).removeLastLocationData(lastRowId);
                }
            }
        }
    }
}
