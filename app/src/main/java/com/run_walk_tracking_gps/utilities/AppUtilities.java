package com.run_walk_tracking_gps.utilities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AppUtilities {

    public static boolean isInForeground(Context context){
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        return myProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
               context instanceof AppCompatActivity;
    }

    @SuppressLint("HardwareIds")
    public static String id(Context context){
        Log.d("MAC", "Mac = " + ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress());
        return CryptographicHashFunctions.md5(
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
        );
    }
}
