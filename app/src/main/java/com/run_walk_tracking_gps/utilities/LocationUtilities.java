package com.run_walk_tracking_gps.utilities;

import android.app.Service;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.location.LocationRequest;

public class LocationUtilities {

    private static final String TAG = LocationUtilities.class.getName();

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS /2;

    private static final long UPDATE_INTERVAL_BACK_IN_MILLISECONDS = 3600000; //60000;
    private static final long FASTEST_UPDATE_INTERVAL_BACK_IN_MILLISECONDS = 6000; // UPDATE_INTERVAL_IN_MILLISECONDS /2;

    public static boolean isGpsEnable(Context context) {
        return ((LocationManager) context.getSystemService(Service.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static LocationRequest createLocationRequestForeground() {
        return LocationRequest.create()
                              .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                              .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                              .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static LocationRequest createLocationRequestBackground() {
        return LocationRequest.create()
                              .setInterval(UPDATE_INTERVAL_BACK_IN_MILLISECONDS)
                              .setMaxWaitTime(2*UPDATE_INTERVAL_BACK_IN_MILLISECONDS)
                              .setFastestInterval(FASTEST_UPDATE_INTERVAL_BACK_IN_MILLISECONDS)
                              .setPriority(LocationRequest.PRIORITY_NO_POWER);

    }

}


