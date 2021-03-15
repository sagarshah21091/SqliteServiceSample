package com.test.sitephototestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.test.sitephototestapp.helper.CustomDialogAlert;
import com.test.sitephototestapp.helper.LocationHelper;
import com.test.sitephototestapp.helper.MyBroadcastReceiver;
import com.test.sitephototestapp.helper.Utils;
import com.test.sitephototestapp.helper.db.DbManager;
import com.test.sitephototestapp.service.TestContinuousService;

import static com.test.sitephototestapp.helper.LocationHelper.REQUEST_CHECK_SETTINGS;
import static com.test.sitephototestapp.service.TestContinuousService.START_FOREGROUND_ACTION;
import static com.test.sitephototestapp.service.TestContinuousService.STOP_FOREGROUND_ACTION;
// Wait for User to take action on fetching current location.
// On User action ask for location permission
// On permission granted start the service which will continuously fetch user's current lat-long
public class MainActivity extends AppCompatActivity implements LocationHelper.OnLocationReceived {
    public String TAG;
    public static final int PERMISSION_FOR_LOCATION = 2;
    Activity activity;
    private CustomDialogAlert customDialogEnable;
    TextView txtCurrentLocation, txtStartRandomEmpData, txtStartLocationData,
            txtCountEmpData, txtCountLocData, txtDeleteEmpData, txtDeleteLocData;
    private LocationHelper locationHelper;
    //        --------- NOT REQUIRED ----------
//    private Location currentLocation;
    //        --------- NOT REQUIRED ----------
    DbManager dbManager;
    MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = this.getClass().getSimpleName();
        activity = this;
        dbManager = new DbManager(this);
        initLocationHelper();
        txtCurrentLocation = findViewById(R.id.txtView);
        txtStartRandomEmpData = findViewById(R.id.txtStartRandomData);
        txtStartLocationData = findViewById(R.id.txtStartLocationData);
        txtCountEmpData = findViewById(R.id.txtCountEmpData);
        txtCountLocData = findViewById(R.id.txtCountLocData);
        txtDeleteEmpData = findViewById(R.id.txtDeleteEmpData);
        txtDeleteLocData = findViewById(R.id.txtDeleteLocData);

        txtCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"onClick txtCurrentLocation");
                /*locationHelper.setLocationSettingRequest(activity, REQUEST_CHECK_SETTINGS,
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.e(TAG,"OnLocationSuccessListener");
                            }
                        }, new LocationHelper.NoGPSDeviceFoundListener() {
                            @Override
                            public void noFound() {
                                Log.e(TAG,"NoGPSDeviceFoundListener");
                            }
                        });*/
            }
        });
        txtStartRandomEmpData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"onClick txtStartRandomEmpData");
                startAsyncForInsertEmpData();
//                dbManager.insertRandomEmpData();
            }
        });
        //        --------- NOT REQUIRED ----------
        /*txtStartLocationData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLocation!=null)
                    dbManager.insertLocationData(currentLocation);
            }
        });*/
        //        --------- NOT REQUIRED ----------
        txtDeleteEmpData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDelete = dbManager.deleteEmpData(dbManager.fetchLastEmpId());
                Log.e(TAG, "DeleteEmpData: "+isDelete);
//                if(isDelete)
//                    setRandomEmpCount();
            }
        });
        txtDeleteLocData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDelete = dbManager.deleteLocData(dbManager.fetchLastLocId());
                Log.e(TAG, "DeleteLocData: "+isDelete);
//                if(isDelete)
//                    setRandomLocCount();
            }
        });
        txtCountEmpData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"onClick txtCountEmpData");
//                Intent intent = new Intent(MainActivity.this, ListActivity.class);
//                intent.putExtra(Utils.constant.key_fromTable, Utils.constant.table.Employee);
//                startActivity(intent);
            }
        });
        txtCountLocData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"onClick txtCountLocData");
//                Intent intent = new Intent(MainActivity.this, ListActivity.class);
//                intent.putExtra(Utils.constant.key_fromTable, Utils.constant.table.Location);
//                startActivity(intent);
            }
        });
        setRandomEmpCount();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utils.ACTION_INSERT_EMP_DATA);
        intentFilter.addAction(Utils.ACTION_INSERT_LOC_DATA);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);

        startMyService();
        checkPermission();
    }

    private void initLocationHelper() {
        locationHelper = new LocationHelper(this);
        locationHelper.setLocationReceivedLister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationHelper.onStop();
    }

    @Override
    protected void onDestroy() {
//        stopMyService();
        closedPermissionDialog();
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer
                .MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startMyService() {
        if (!isMyServiceRunning(TestContinuousService.class)) {
            Intent intent = new Intent(this, TestContinuousService.class);
            intent.setAction(START_FOREGROUND_ACTION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }

    public void stopMyService() {
        if (isMyServiceRunning(TestContinuousService.class)) {
            Intent intent = new Intent(this, TestContinuousService.class);
            intent.setAction(STOP_FOREGROUND_ACTION);
            stopService(intent);
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission
                            .ACCESS_FINE_LOCATION, android.Manifest.permission
                            .ACCESS_COARSE_LOCATION},
                    PERMISSION_FOR_LOCATION);
        }
        else
        {
//            startMyService();
            txtCurrentLocation.performClick();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case PERMISSION_FOR_LOCATION:
                    goWithLocationPermission(grantResults);
                    break;
                default:
                    //do with default
                    break;
            }
        }
    }

    private void goWithLocationPermission(int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Do the stuff that requires permission...
            txtCurrentLocation.performClick();
            locationHelper.startLocationUpdate();
//            startMyService();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest
                    .permission.ACCESS_COARSE_LOCATION) && ActivityCompat
                    .shouldShowRequestPermissionRationale(this, android.Manifest
                            .permission.ACCESS_FINE_LOCATION)) {
                openPermissionDialog();
            } else {
                closedPermissionDialog();
            }
        }
    }

    private void closedPermissionDialog() {
        if (customDialogEnable != null && customDialogEnable.isShowing()) {
            customDialogEnable.dismiss();
            customDialogEnable = null;
        }
    }

    private void openPermissionDialog() {
        if (!isDestroyed() && customDialogEnable != null && customDialogEnable.isShowing()) {
            return;
        }
        customDialogEnable = new CustomDialogAlert(this, getResources().getString(R.string
                .text_attention), getResources().getString(R.string
                .msg_reason_for_permission_location), getString(R.string.text_i_am_sure), getString
                (R.string.text_re_try)) {
            @Override
            public void onClickLeftButton() {
                closedPermissionDialog();
                finishAffinity();
            }

            @Override
            public void onClickRightButton() {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
                        .Manifest
                        .permission
                        .ACCESS_FINE_LOCATION, android.Manifest.permission
                        .ACCESS_COARSE_LOCATION}, PERMISSION_FOR_LOCATION);
                closedPermissionDialog();
            }

        };
        if (!isFinishing()) {
            customDialogEnable.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
//                        checkPermission();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.e(TAG, "RESULT_CANCELED");
                        txtCurrentLocation.performClick();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //        --------- NOT REQUIRED ----------
//        currentLocation = location;
//        String latlong = location.getLatitude()+","+location.getLongitude();
//        Log.e(TAG, "onLocationChanged - "+latlong);
        //        --------- NOT REQUIRED ----------
    }

    @Override
    public void onConnected(Bundle bundle) {
        setRandomLocCount();
//        --------- NOT REQUIRED ----------
            /*locationHelper.getLastLocation(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        String latlong = location.getLatitude() + "," + location.getLongitude();
                        Log.e(TAG, "getLastLocation - " + latlong);
                    }
                }
            });*/
        //        --------- NOT REQUIRED ----------
        locationHelper.startLocationUpdate();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "GoogleClientConnectionFailed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleClientConnectionSuspended");
    }

    public void setRandomEmpCount() {
        txtCountEmpData.setText("("+dbManager.fetchRandomEmpCount()+")\nView\nRandom Data");
        int empId = dbManager.fetchLastEmpId();
        Log.e(TAG, "Last EmpId: "+empId);
//        dbManager.insertRandomEmpData();
    }

    public void setRandomLocCount() {
        txtCountLocData.setText("("+dbManager.fetchRandomLocCount()+")\nView\nLocation Data");
        int locId = dbManager.fetchLastLocId();
        Log.e(TAG, "Last LocId: "+locId);
//        if(currentLocation!=null)
//            dbManager.insertLocationData(currentLocation);
    }


    public void onNetworkChange() {
        String networkMessage = Utils.isInternetConnected(this)?"Internet Connected...":"Internet DisConnected...";
        Utils.showToast(this, networkMessage);
        Log.e(TAG, networkMessage);
    }

    private void startAsyncForInsertEmpData() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "Starting async to insert emp data...");
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                return dbManager.insertRandomEmpData();
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG, "End async to insert emp data...");
            }
        }.execute();
    }
}
