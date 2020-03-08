package com.run_walk_tracking_gps.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionUtilities {

    // TODO: 26/02/2020 SISTEMARE BENE I PERMESSI NELLE VARIE ACTIVITY
    // TODO: 26/02/2020 RICHIEDERE IN TUTTE LE ACTIVITY CHE LE UTILIZZANO

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public final static int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 7;

    private static boolean isGranted(int[] grantResults){
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    public static void onRequestPermissionsResult(int[] grantResults, OnPermissionListener onPermissionListener){
        // If request is cancelled, the result arrays are empty.
        if(isGranted(grantResults)){
            // permission was granted, yay! Do the contacts-related task you need to do.
            onPermissionListener.onGranted();
        }else {
            // permission denied, boo! Disable the functionality that depends on this permission.
            onPermissionListener.onDenied();
        }
    }

    public static boolean hasLocationPermission(final Context context){
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasReadExternalStoragePermission(final Context context) {
        return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void setLocationPermission(final Activity activity) {
        final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    public static void setReadExternalStoragePermission(final Activity activity) {
        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(activity, permissions, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    public interface OnPermissionListener{
        void onGranted();
        void onDenied();
    }

}
