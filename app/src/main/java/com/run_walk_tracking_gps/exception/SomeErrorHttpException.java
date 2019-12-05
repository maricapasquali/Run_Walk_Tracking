package com.run_walk_tracking_gps.exception;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.run_walk_tracking_gps.R;

public class SomeErrorHttpException extends AbstractException {

    public SomeErrorHttpException(Context context, String mex){
        super(context, mex);
    }

    @Override
    public void alert() {
        Log.e(getContext().getPackageName(), getMessage());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setMessage(getMessage())
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }
}
