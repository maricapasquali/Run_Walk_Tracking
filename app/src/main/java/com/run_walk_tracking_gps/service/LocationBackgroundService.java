package com.run_walk_tracking_gps.service;


import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.run_walk_tracking_gps.controller.Preferences;

import java.util.List;

import androidx.annotation.Nullable;

public class LocationBackgroundService extends Service {

    private static final String TAG = LocationBackgroundService.class.getName();
    private FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_LOCATION_BACKGROUND = 0;

    private LocationRequest locationRequest;

    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Preferences.MapLocation.create(context);

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = LocationRequest.create().setInterval(60000).setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            PendingIntent pendingLocation = PendingIntent.getBroadcast(context, REQUEST_LOCATION_BACKGROUND,
                    new Intent(context, RouteBackgroundReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

            fusedLocationClient.requestLocationUpdates(locationRequest, pendingLocation).addOnFailureListener(e -> {
                Log.e(TAG, "BACKGROUND : Exception getting location");
                e.printStackTrace();
            });
            Toast.makeText(context, "Start LOCATION BACKGROUND", Toast.LENGTH_LONG).show();
            Log.d(TAG, "START LOCATION BACKGROUND!");
        }catch (SecurityException e){
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class RouteBackgroundReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Location RECEIVER = "+ LocationResult.hasResult(intent));
            if(LocationResult.hasResult(intent)){
                LocationResult locationResult = LocationResult.extractResult(intent);
                if(locationResult!=null){
                    List<Location> locations = locationResult.getLocations();
                    Log.d(TAG, "Locations = "+ locations);
                    Preferences.MapLocation.addAll(context, locations);
                    Toast.makeText(context, "Locations = " + locations, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
