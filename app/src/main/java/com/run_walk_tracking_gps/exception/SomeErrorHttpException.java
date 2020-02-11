package com.run_walk_tracking_gps.exception;

import android.content.Context;

import com.run_walk_tracking_gps.R;

import androidx.appcompat.app.AlertDialog;

public class SomeErrorHttpException extends BackgroundException{

    public SomeErrorHttpException(Context context, String mex) {
        super(context, mex, SomeErrorHttpException.class.getSimpleName());
    }

    public static SomeErrorHttpException create(Context context, String mex){
        return new SomeErrorHttpException(context, mex);
    }

    @Override
    protected void createAlertDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setMessage(getMessage())
                .setPositiveButton(R.string.ok, super.close())
                .create()
                .show();
    }

}
