package com.test.sitephototestapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.sitephototestapp.adapter.AdapterEmployee;
import com.test.sitephototestapp.adapter.AdapterLocation;
import com.test.sitephototestapp.helper.MyBroadcastReceiver;
import com.test.sitephototestapp.helper.Utils;
import com.test.sitephototestapp.helper.db.DbManager;
import com.test.sitephototestapp.helper.model.BinEmpData;
import com.test.sitephototestapp.helper.model.BinLocationData;
import com.test.sitephototestapp.service.TestContinuousService;

import java.util.ArrayList;
import java.util.Collections;

import static com.test.sitephototestapp.helper.Utils.THRESHOLD_EMP_COUNT_DISPLAY_LIMIT;
import static com.test.sitephototestapp.helper.Utils.THRESHOLD_EMP_DATA_INSERT_MAX;
import static com.test.sitephototestapp.service.TestContinuousService.START_FOREGROUND_ACTION;


public class ListActivity extends AppCompatActivity {
    public String TAG;
    private IntentFilter intentFilter;
    private RecyclerView rcvData;
    private LinearLayout ivEmpty;
    private AdapterEmployee adapterEmployee;
    private AdapterLocation adapterLocation;
    private int fromTable;
    DbManager dbManager;
    ArrayList<BinEmpData> listDataEmp = new ArrayList<>();
    ArrayList<BinLocationData> listDataLoc = new ArrayList<>();
    MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        TAG = this.getClass().getSimpleName();
        fromTable = getIntent().getExtras().getInt(Utils.constant.key_fromTable);
        dbManager = new DbManager(this);
        rcvData = (RecyclerView)findViewById(R.id.rcvData);
        ivEmpty = (LinearLayout) findViewById(R.id.ivEmpty);
        setData();

        switch (fromTable)
        {
            case Utils.constant.table.Employee:
                intentFilter = Utils.getEmployeeActions();
                break;
            case Utils.constant.table.Location:
                intentFilter = Utils.getLocationActions();
                break;
        }
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }

    private void setData() {
        switch (fromTable)
        {
            case Utils.constant.table.Employee:
                initRcvEmpData();
                fetchUpdateEmpData();
                break;
            case Utils.constant.table.Location:
                initRcvLocData();
                fetchUpdateLocData();
                break;
        }
    }

    private void initRcvEmpData() {
        adapterEmployee = new AdapterEmployee(this, listDataEmp);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        rcvData.setAdapter(adapterEmployee);
    }

    private void initRcvLocData() {
        adapterLocation = new AdapterLocation(this, listDataLoc);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        rcvData.setAdapter(adapterLocation);
    }

    private void fetchUpdateEmpData() {
        fetchDataAsyncEmployee();
//        listDataEmp.addAll(dbManager.fetchEmpData());
//        adapterEmployee.notifyDataSetChanged();
//        setEmptyUi(listDataEmp.size()==0);
    }

    private void fetchUpdateLocData() {
        fetchDataAsyncLocation();
//        listDataLoc.addAll(dbManager.fetchLocData());
//        adapterLocation.notifyDataSetChanged();
//        setEmptyUi(listDataLoc.size()==0);
    }

    private void setEmptyUi(boolean haveData)
    {
        /*switch (fromTable)
        {
            case Utils.constant.table.Employee:
                ivEmpty.setVisibility(haveData? View.VISIBLE:View.GONE);
                break;
            case Utils.constant.table.Location:
                ivEmpty.setVisibility(haveData? View.VISIBLE:View.GONE);
                break;
        }*/
        ivEmpty.setVisibility(haveData? View.VISIBLE:View.GONE);
    }

    private void fetchDataAsyncEmployee() {

        new AsyncTask<Void, Void, ArrayList<BinEmpData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "Starting async to fetch data...");
            }

            @Override
            protected ArrayList<BinEmpData> doInBackground(Void... voids) {
                return dbManager.fetchEmpData();
            }

            @Override
            protected void onPostExecute(ArrayList<BinEmpData> aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG, "End async data fetch...");
                listDataEmp.addAll(aVoid);
                adapterEmployee.notifyDataSetChanged();
                setEmptyUi(listDataEmp.size()==0);
            }
        }.execute();
    }

    private void fetchDataAsyncLocation() {
        new AsyncTask<Void, Void, ArrayList<BinLocationData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "Starting async to fetch data...");
            }

            @Override
            protected ArrayList<BinLocationData> doInBackground(Void... voids) {
                return dbManager.fetchLocData();
            }

            @Override
            protected void onPostExecute(ArrayList<BinLocationData> aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG, "End async data fetch...");
                listDataLoc.addAll(aVoid);
                adapterLocation.notifyDataSetChanged();
                setEmptyUi(listDataLoc.size()==0);
            }
        }.execute();
    }

    public void addNewEmployeeData() {
        /*Collections.reverse(listDataEmp);
        listDataEmp.add(dbManager.fetchLastsEmpData());
        Collections.reverse(listDataEmp);
        adapterEmployee.notifyDataSetChanged();*/
//        ===========================================
        new AsyncTask<Void, Void, ArrayList<BinEmpData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "Starting async...");
            }

            @Override
            protected ArrayList<BinEmpData> doInBackground(Void... voids) {
                Collections.reverse(listDataEmp);
                listDataEmp.add(dbManager.fetchLastEmpData());
                Collections.reverse(listDataEmp);
                return listDataEmp;
            }

            @Override
            protected void onPostExecute(ArrayList<BinEmpData> aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG, "End async data fetch...");
                adapterEmployee.notifyDataSetChanged();
                setEmptyUi(aVoid.size()==0);
            }
        }.execute();
//        ===========================================
    }

    public void addNewLocationData() {
        /*Collections.reverse(listDataLoc);
        listDataLoc.add(dbManager.fetchLastLocData());
        Collections.reverse(listDataLoc);
        adapterLocation.notifyDataSetChanged();*/
//        ============================================
        new AsyncTask<Void, Void, ArrayList<BinLocationData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "Starting async...");
            }

            @Override
            protected ArrayList<BinLocationData> doInBackground(Void... voids) {
                Collections.reverse(listDataLoc);
                listDataLoc.add(dbManager.fetchLastLocData());
                Collections.reverse(listDataLoc);
                return listDataLoc;
            }

            @Override
            protected void onPostExecute(ArrayList<BinLocationData> aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG, "End async data...");
                adapterLocation.notifyDataSetChanged();
                setEmptyUi(aVoid.size()==0);
            }
        }.execute();
    }

    public void removeLastEmployeeData(int _id)
    {
        /*BinEmpData binEmpDataToDel = null;
        for(BinEmpData binEmpData : listDataEmp)
        {
            if(binEmpData.getId() == _id)
            {
                binEmpDataToDel = binEmpData;
                break;
            }
        }
        if(binEmpDataToDel!=null) {
            listDataEmp.remove(binEmpDataToDel);
            adapterEmployee.notifyDataSetChanged();
        }*/
//        ========================================
        new AsyncTask<Void, Void, BinEmpData>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "Starting async...");
            }

            @Override
            protected BinEmpData doInBackground(Void... voids) {
                BinEmpData binEmpDataToDel = null;
                for(BinEmpData binEmpData : listDataEmp)
                {
                    if(binEmpData.getId() == _id)
                    {
                        binEmpDataToDel = binEmpData;
                        break;
                    }
                }
                return binEmpDataToDel;
            }

            @Override
            protected void onPostExecute(BinEmpData aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG, "End async data fetch...");
                if(aVoid!=null) {
                    listDataEmp.remove(aVoid);
                    adapterEmployee.notifyDataSetChanged();
                }
                setEmptyUi(listDataEmp.size()==0);
            }
        }.execute();
//        ========================================
    }

    public void removeLastLocationData(int _id)
    {
        /*BinLocationData binLocationDataToDel = null;
        for(BinLocationData binLocationData : listDataLoc)
        {
            if(binLocationData.getId() == _id)
            {
                binLocationDataToDel = binLocationData;
                break;
            }
        }
        if(binLocationDataToDel!=null) {
            listDataLoc.remove(binLocationDataToDel);
            adapterLocation.notifyDataSetChanged();
        }*/
//        =========================================
        new AsyncTask<Void, Void, BinLocationData>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "Starting async...");
            }

            @Override
            protected BinLocationData doInBackground(Void... voids) {
                BinLocationData binLocationDataToDel = null;
                for(BinLocationData binLocationData : listDataLoc)
                {
                    if(binLocationData.getId() == _id)
                    {
                        binLocationDataToDel = binLocationData;
                        break;
                    }
                }
                return binLocationDataToDel;
            }

            @Override
            protected void onPostExecute(BinLocationData aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG, "End async data...");
                if(aVoid!=null) {
                    listDataLoc.remove(aVoid);
                    adapterLocation.notifyDataSetChanged();
                }
                setEmptyUi(listDataLoc.size()==0);
            }
        }.execute();
//        =========================================
    }

    public void onNetworkChange() {
        boolean isInternetAvail = Utils.isInternetConnected(this);
        String networkMessage = isInternetAvail ? "Internet Connected...":"Internet DisConnected...";
//        Utils.showToast(this, networkMessage);
        Log.e(TAG, networkMessage);
        startMyService();
    }

    public void startMyService() {
//        if (!isMyServiceRunning(TestContinuousService.class)) {
        Intent intent = new Intent(this, TestContinuousService.class);
        intent.setAction(START_FOREGROUND_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
//        }
    }
}
