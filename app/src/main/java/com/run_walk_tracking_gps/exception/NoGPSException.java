package com.run_walk_tracking_gps.exception;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.run_walk_tracking_gps.R;

public class NoGPSException extends AbstractException {

    public NoGPSException(Context context){
        super(context, R.string.gps_disabled);
    }

    @Override
    public void alert() {
        Log.e(getContext().getPackageName(), getMessage());
        new AlertDialog.Builder(getContext())
                .setMessage(getMessage())
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) ->
                        getContext().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.no, null)
                .create()
                .show();

    }
}
