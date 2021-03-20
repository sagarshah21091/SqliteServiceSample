package com.test.sitephototestapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.test.sitephototestapp.MainActivity;
import com.test.sitephototestapp.R;
import com.test.sitephototestapp.helper.IncomingHandlerLocData;
import com.test.sitephototestapp.helper.IncomingHandlerEmpData;
import com.test.sitephototestapp.helper.LocationHelper;
import com.test.sitephototestapp.helper.Utils;
import com.test.sitephototestapp.helper.db.DbManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestContinuousService extends Service
        implements LocationHelper.OnLocationReceived
{
    public static final String START_FOREGROUND_ACTION = "provider" +
            ".startforeground";
    public static final String STOP_FOREGROUND_ACTION = "provider" +
            ".stopforeground";
    public static final String TAG = "TestContinousService";
    public static final String CHANNEL_ID = "channel_01";
    public static final int FOREGROUND_NOTIFICATION_ID = 1111;
    public static final int SCHEDULED_SECONDS = 10;
    public static final int SLEEP_SECONDS = 2000;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean isScheduledStart;
//    private IncomingHandlerLocData handlerLocData;
//    private IncomingHandlerEmpData handlerEmpData;
    private LocationHelper locationHelper;
    private Location currentLocation;
    private Runnable runnableEmployee;
    private DbManager dbManager;

    @Override
    public void onCreate() {
        super.onCreate();
        dbManager = new DbManager(this);
//        handlerLocData = new IncomingHandlerLocData(TestContinuousService.this);
//        handlerEmpData = new IncomingHandlerEmpData(TestContinuousService.this);
        initLocationHelper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            if (intent.getAction().equals(START_FOREGROUND_ACTION)) {
                Log.e(TAG, "Received Start Foreground Intent");
                startForeground(FOREGROUND_NOTIFICATION_ID,
                        getNotification(getResources().getString(R.string.app_name)));
            } else if (intent.getAction().equals(STOP_FOREGROUND_ACTION)) {
                Log.e(TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
            }
        }
        startUpdateSchedule();
        checkPermission();
        /*new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Log.e(TAG, "doInBackground");
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG, "onPostExecute");
            }
        }.execute();*/
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "Separate Thread Start");
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    Log.e(TAG, "Separate Thread Task Done");
                }
            }
        }).start();*/

        return START_STICKY;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
                Log.e(TAG, "Permission not granted");
//                stopForeground(true);
//                stopSelf();
        } else {
            locationHelper.onStart();
        }
    }


    @Override
    public void onDestroy() {
        locationHelper.onStop();
        stopUpdateSchedule();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public void startUpdateSchedule() {

        if (!isScheduledStart) {

            Runnable runnableLocation = new Runnable() {
                @Override
                public void run() {
//                    Log.e(TAG, "runnableLocation");
//                    Message message = handlerLocData.obtainMessage();
//                    handlerLocData.sendMessage(message);
//                    -----------------------------------
                    removeForegroundNotification();
                    handleLocationData();
                }
            };
            runnableEmployee = new Runnable() {
                @Override
                public void run() {
//                    Log.e(TAG, "runnableEmployee");
//                    ------ WORKS GREAT WITH THREAD.SLEEP ----
                    try {
//                        Log.e(TAG, "Separate Thread Start");
                        Thread.sleep(SLEEP_SECONDS);
                        handleEmpData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
//                        Log.e(TAG, "Thread InterruptedException");
                    }
                    /*finally {
                        Log.e(TAG, "Separate Thread Task Done");
                        handleEmpData();
                    }*/
//                    ----------------(Not Useful) This Handle Msg blocks UI -----------
//                    Message message = handlerEmpData.obtainMessage();
//                    handlerEmpData.sendMessage(message);
//                    ----------------------------------------------------------
                }
            };
            scheduledExecutorService = Executors.newScheduledThreadPool(2);
            scheduledExecutorService.execute(runnableEmployee);
//            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(runnableLocation, 0,
                    SCHEDULED_SECONDS, TimeUnit
                            .SECONDS);
            Log.e(TAG, "Schedule Start");
            isScheduledStart = true;
        }
    }

    public void stopUpdateSchedule() {
        if (isScheduledStart) {
            Log.e(TAG, "Schedule Stop");
            scheduledExecutorService.shutdown(); // Disable new tasks from being submitted
            // Wait a while for existing tasks to terminate
            try {
                if (!scheduledExecutorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduledExecutorService.shutdownNow(); // Cancel currently executing
                    // tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!scheduledExecutorService.awaitTermination(60, TimeUnit.SECONDS))
                        Log.e(TAG, "Pool did not terminate");

                }
            } catch (InterruptedException e) {
                Log.e(TAG, "" + e);
                // (Re-)Cancel if current thread also interrupted
                scheduledExecutorService.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
            isScheduledStart = false;
        }

    }

    /**
     * this method get Notification object which help to notify user as foreground service
     *
     * @param notificationDetails
     * @return
     */
    private Notification getNotification(String notificationDetails) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent exitIntent = new Intent(this, TestContinuousService.class);
        exitIntent.setAction(STOP_FOREGROUND_ACTION);
        PendingIntent pexitIntent = PendingIntent.getService(this, 0,
                exitIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        getNotificationIcon()))
                .setContentTitle(notificationDetails)
                .setContentText(getResources().getString(R.string.msg_service))
                .setContentIntent(notificationPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        builder.setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.addAction(R.mipmap.ic_launcher,
                    getResources().getString(R.string.text_exit).toUpperCase(), pexitIntent);
        }
        return builder.build();
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build
                .VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }

    private void initLocationHelper() {
        locationHelper = new LocationHelper(this);
        locationHelper.setLocationReceivedLister(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        String latlong = location.getLatitude()+","+location.getLongitude();
        Log.e(TAG, "onLocationChanged - "+latlong);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "GoogleClientConnected");
        locationHelper.getLastLocation(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {
                    currentLocation = location;
                    String latlong = location.getLatitude() +"," + location.getLongitude();
                    Log.e(TAG, "getLastLocation - " + latlong);
                }
            }
        });
        locationHelper.startLocationUpdate();
        startUpdateSchedule();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "GoogleClientConnectionFailed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleClientConnectionSuspended");
    }

    public void handleLocationData() {
//        Log.e(TAG, "handleLocationData");
        String now = new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date());
        Log.e(TAG, "timestamp-Location "+now);
        if(Utils.isInternetConnected(this))
        {
            // Get last record from Location table
            // Data exist >> delete data one by one and update count on UI
            // Data don't exist >> do nothing.
            int lastRowIdLoc = dbManager.fetchLastLocId();
            if(dbManager.deleteLocData(lastRowIdLoc)) {
                Intent intentLocDel = new Intent(Utils.ACTION_DELETE_LOC_DATA);
                intentLocDel.putExtra(Utils.constant.key_last_row_id, lastRowIdLoc);
                sendBroadcast(intentLocDel);
            }
        }
        else
        {
            if(currentLocation!=null) {
                dbManager.insertLocationData(currentLocation);
                String latlong = currentLocation.getLatitude() +"," + currentLocation.getLongitude();
                Log.e(TAG, "currentLocation - " + latlong);
            }
        }
    }

    public void handleEmpData() {
//        Log.e(TAG, "handleEmpData");
        String now = new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date());
        Log.e(TAG, "timestamp-Employee "+now);

        if(Utils.isInternetConnected(this))
        {
            // Get last record from Employee table
            // Data exist >> delete data one by one and update count on UI
            // Data don't exist >> do nothing.
            int lastRowIdEmp = dbManager.fetchLastEmpId();
            if(dbManager.deleteEmpData(lastRowIdEmp)) {
                Intent intentEmpDel = new Intent(Utils.ACTION_DELETE_EMP_DATA);
                intentEmpDel.putExtra(Utils.constant.key_last_row_id, lastRowIdEmp);
                sendBroadcast(intentEmpDel);
            }
        }
        else
        {
            // Insert 1 data at a time
            dbManager.insertRandomEmpData(Utils.THRESHOLD_EMP_DATA_INSERT_DEFAULT);
        }
        scheduledExecutorService.execute(runnableEmployee);
    }

    public void removeForegroundNotification()
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(isNotificationVisible(notificationManager))
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    notificationManager.deleteNotificationChannel(TestContinuousService.CHANNEL_ID);
                }
            },650);
        }
    }

    private boolean isNotificationVisible(NotificationManager notificationManager) {
        StatusBarNotification[] sbN =notificationManager.getActiveNotifications();
        for(StatusBarNotification binNotifica : sbN)
        {
            if(binNotifica.getId() == TestContinuousService.FOREGROUND_NOTIFICATION_ID)
                return true;
        }
        return false;
    }
}
