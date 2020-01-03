package com.run_walk_tracking_gps.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;

public class SyncService extends IntentService {

    private static final String TAG = SyncService.class.getName();
    private Context context;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SyncService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(SyncServiceHandler.TAG, "onCreate Service" );
        context = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(SyncServiceHandler.TAG, "Start Sync Service" );
        NetworkHelper.HttpRequest.getInstance(context).syncInBackground(() -> {
            Log.d(SyncServiceHandler.TAG, "Sincronizzazione avvenuta");
            stopSelf();
        });
    }
}
