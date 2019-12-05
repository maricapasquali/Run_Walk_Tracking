package com.run_walk_tracking_gps.exception;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.run_walk_tracking_gps.R;

public class PasswordNotCorrectException extends AbstractException{


    public PasswordNotCorrectException(Context context){
        super(context, R.string.not_correct_password);
    }


    @Override
    public void alert() {
        Log.e(getContext().getPackageName(), getMessage());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.info)
                .setMessage(getMessage())
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }
}