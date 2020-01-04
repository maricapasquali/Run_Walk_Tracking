package com.run_walk_tracking_gps.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.ErrorQueue;

public class SyncServiceHandler {
    public static final String TAG = SyncServiceHandler.class.getName();

    private static final int START_SERVICE_REQUEST_CODE = 1000;

    private static SyncServiceHandler handler;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private SyncServiceHandler(Context context){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, RequestSyncBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, START_SERVICE_REQUEST_CODE, serviceIntent,0);
    }

    public static synchronized SyncServiceHandler create(Context context){
        if(handler == null){
            Log.e(TAG,"SYNC created!");
            handler = new SyncServiceHandler(context.getApplicationContext());
        }
        return handler;
    }

    public void start(){
        if(handler!=null){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
            Log.d(TAG, "Start SYNC!");
        }
    }

    public void stop(){
        if(handler!=null){
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Stop SYNC!");
        }
    }

    public static class RequestSyncBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(SyncServiceHandler.TAG, "Richiesta Sync");
            NetworkHelper.HttpRequest.getInstance(context).syncInBackground(() -> {
                Log.d(SyncServiceHandler.TAG, "Sincronizzazione avvenuta");
                ErrorQueue.getInstance(context).removeAll();
            });
        }
    }
}
