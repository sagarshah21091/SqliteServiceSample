package com.test.sitephototestapp.helper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.test.sitephototestapp.service.TestContinuousService;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IncomingHandler extends Handler
{
    public static String CONST_DATE = "DATE";
    private final WeakReference<TestContinuousService> mService;

    public IncomingHandler(TestContinuousService service) {
        mService = new WeakReference<TestContinuousService>(service);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        TestContinuousService service = mService.get();
        if (service != null) {
            Bundle bundle = new Bundle();
            bundle.putString(CONST_DATE, new SimpleDateFormat("ddMMMyy HH:mm:ss").format(new Date()));
            msg.setData(bundle);
            service.handleMessage(msg);
        }
    }
}
