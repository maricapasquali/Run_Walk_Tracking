package com.run_walk_tracking_gps.exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.run_walk_tracking_gps.R;

public class InternetNoAvailableException extends AbstractException {

    public InternetNoAvailableException(Context context){
        super(context);
    }

    @Override
    public void alert() {

        Log.e(getContext().getPackageName(), getContext().getString(R.string.internet_not_available_title));
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.internet_not_available_title)
                .setMessage(R.string.internet_not_available_mex)
                .setCancelable(false)
                .setPositiveButton(R.string.setting, (dialog, which) ->
                        getContext().startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)))
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
}
