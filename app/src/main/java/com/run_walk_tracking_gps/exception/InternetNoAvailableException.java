package com.run_walk_tracking_gps.exception;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.ErrorQueue;

public class InternetNoAvailableException extends BackgroundException {

    public InternetNoAvailableException(Context context) {
        super(context, R.string.internet_not_available_mex,
                InternetNoAvailableException.class.getSimpleName());
    }

    public static InternetNoAvailableException create(Activity context){
        return new InternetNoAvailableException(context);
    }

    @Override
    protected void createAlertDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.internet_not_available_title)
                .setMessage(getMessage())
                .setCancelable(false)
                .setPositiveButton(R.string.setting, (dialog, which) ->{
                    ErrorQueue.getInstance(getContext()).remove(this);
                    getContext().startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                })
                .setNegativeButton(R.string.cancel, super.close())
                .create()
                .show();
    }
}
