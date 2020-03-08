package com.run_walk_tracking_gps.utilities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class AppUtilities {

    private static final String TAG = AppUtilities.class.getName();

    public static boolean isInForeground(Context context){
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        return myProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
               context instanceof AppCompatActivity;
    }

    @SuppressLint("HardwareIds")
    public static String id(Context context){
        Log.d(TAG, "Android-ID = " + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        return CryptographicHashFunctions.md5(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    }
}
