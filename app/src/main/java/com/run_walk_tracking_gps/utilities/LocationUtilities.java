package com.run_walk_tracking_gps.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.LocationRequest;

public class LocationUtilities {

    private static final String TAG = LocationUtilities.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public static boolean isGpsEnable(Context context) {
        return ((LocationManager) context.getSystemService(Service.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    // TODO: 12/5/2019 DA RIGUARDARE
    public static LocationRequest createLocationRequest() {
        return LocationRequest.create()
                              .setInterval(5000)
                              .setFastestInterval(5000)
                              .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static void setLocationPermission(final Activity activity) {
        final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }
}


