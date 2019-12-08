package com.run_walk_tracking_gps.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

public class DeviceUtilities {
    private final static int STATE_PHONE_PERMISSION_REQUEST_CODE = 111;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getIdDevice(final Context context) throws SecurityException{
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getImei();
    }

    public static void requestStatePhonePermission(final Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, STATE_PHONE_PERMISSION_REQUEST_CODE);
    }
}
