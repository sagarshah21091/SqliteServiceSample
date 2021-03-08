package com.test.sitephototestapp.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.test.sitephototestapp.R;

/**
 * Created by Sagar on 08-Feb-17.
 */

public abstract class CustomDialogAlert extends Dialog implements View.OnClickListener {
    public TextView tvDialogEdiTextMessage, tvDialogEditTextTitle, btnDialogEditTextLeft,
            btnDialogEditTextRight;

    public CustomDialogAlert(Context context, String titleDialog, String messageDialog,
                             String titleLeftButton, String titleRightButton) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_alert);
        tvDialogEdiTextMessage = (TextView) findViewById(R.id.tvDialogAlertMessage);
        tvDialogEditTextTitle = (TextView) findViewById(R.id.tvDialogAlertTitle);
        btnDialogEditTextLeft = (TextView) findViewById(R.id.btnDialogAlertLeft);
        btnDialogEditTextRight = (TextView) findViewById(R.id.btnDialogAlertRight);


        btnDialogEditTextLeft.setOnClickListener(this);
        btnDialogEditTextRight.setOnClickListener(this);

        tvDialogEditTextTitle.setText(titleDialog);
        tvDialogEdiTextMessage.setText(messageDialog);
        btnDialogEditTextLeft.setText(titleLeftButton);
        btnDialogEditTextRight.setText(titleRightButton);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDialogAlertLeft:
                onClickLeftButton();
                break;
            case R.id.btnDialogAlertRight:
                onClickRightButton();
                break;
            default:
                // do with default
                break;
        }

    }

    public abstract void onClickLeftButton();

    public abstract void onClickRightButton();

}
