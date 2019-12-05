package com.run_walk_tracking_gps.exception;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.run_walk_tracking_gps.R;

public class NoGPSException extends AbstractException {

    public NoGPSException(Context context){
        super(context);
    }

    @Override
    public void alert() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.gps_disabled)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) ->
                        getContext().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.no, null)
                .create()
                .show();

    }
}
