package com.run_walk_tracking_gps.exception;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.run_walk_tracking_gps.R;

public class NoGPSException extends Exception {

    private Context context;
    private final String NO_GPS_MEX ;

    public NoGPSException(Context context){
        NO_GPS_MEX = context.getString(R.string.gps_disabled);
        this.context = context;
    }

    public void alert()
    {
        new AlertDialog.Builder(context)
                .setMessage(NO_GPS_MEX)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes), (dialog, id) ->
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(context.getString(R.string.no), (dialog, id) -> dialog.cancel())
                .create()
                .show();

    }


}
