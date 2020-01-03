package com.run_walk_tracking_gps.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class SyncServiceHandler {
    public static final String TAG = SyncServiceHandler.class.getName();

    private static final int START_SERVICE_REQUEST_CODE = 1000;
    private static final long REPEAT_TIME = 60000;

    private static SyncServiceHandler handler;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private SyncServiceHandler(Context context){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, SyncService.class);
        pendingIntent = PendingIntent.getService( context,
                                                                START_SERVICE_REQUEST_CODE,
                                                                serviceIntent,
                                                                0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
        Log.e(TAG,"Repeating SYNC created!");
    }

    public static synchronized SyncServiceHandler create(Context context){
        if(handler == null)
            handler = new SyncServiceHandler(context);

        return handler;
    }

    public static void createDelayed(Context context){
        new Handler().postDelayed(()-> SyncServiceHandler.create(context), REPEAT_TIME);
    }

    public void stop(){
        if(handler!=null){
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Stop SYNC!");
        }

    }


/*
    private Context context = null;

    private SyncServiceHandler(Context context){
       this.context = context;
    }

    public static synchronized SyncServiceHandler create(Context context){
        if(handler == null)
            handler = new SyncServiceHandler(context);
        return handler;
    }

    public void start(){
        context.startService(new Intent(context, SyncService.class));
    }
*/
}
