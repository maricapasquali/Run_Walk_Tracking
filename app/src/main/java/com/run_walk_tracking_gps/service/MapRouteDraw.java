package com.run_walk_tracking_gps.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import java.util.List;

public class MapRouteDraw {

    private static final String TAG = MapRouteDraw.class.getName();

    private final Context context;
    private FusedLocationProviderClient fusedLocationClient;

    private static MapRouteDraw handler;


    /* BACKGROUND */
    private static final int REQUEST_LOCATION_BACKGROUND = 0;
    private boolean isBackground = false;
    private PendingIntent pendingLocation;
    private MapRouteBackgroundReceiver broadcastReceiver = new MapRouteBackgroundReceiver();
    private boolean isRegister = false;

    /* FOREGROUND */
    private OnChangeLocationListener onReceiverListener;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Preferences.MapLocation.addAll(context, locationResult.getLocations());
                onReceiverListener.addPolyLineOnMap(Preferences.MapLocation.getPolylineOptions(context));
            }
        }
    };

    private MapRouteDraw(Context context) {
        this.context = context;

        Preferences.MapLocation.create(context);

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public static MapRouteDraw create(Context context) {
        return LocationUtilities.isGpsEnable(context) ? new MapRouteDraw(context) : null;
    }

    private MapRouteDraw(Context context, OnChangeLocationListener onChangeLocationListener) {
        this(context);
        this.onReceiverListener = onChangeLocationListener;
    }

    public static synchronized MapRouteDraw create(Context context, OnChangeLocationListener  onChangeLocationListener) {
        boolean gpsEnable = LocationUtilities.isGpsEnable(context.getApplicationContext());
        if(handler==null && gpsEnable)
            handler = new MapRouteDraw(context.getApplicationContext(), onChangeLocationListener);
        else if(handler!=null && !gpsEnable) handler = null;

        return handler; //LocationUtilities.isGpsEnable(context) ? new MapRouteDraw(context, onChangeLocationListener) : null;
    }

    public void setBackground(boolean isBackground){
        this.isBackground = isBackground;
    }

    public void startDrawing(Looper looper) {
        try {

            LocationRequest locationRequest ;
            if(isBackground){
                registerBroadcastReceiver();
                locationRequest =  LocationUtilities.createLocationRequestBackground();
                pendingLocation = PendingIntent.getBroadcast(context, REQUEST_LOCATION_BACKGROUND,
                        new Intent(ActionReceiver.UPDATE_CHANGE_LOCATION).setPackage(context.getPackageName()),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                fusedLocationClient.requestLocationUpdates(locationRequest ,pendingLocation)
                        .addOnFailureListener(e -> { Log.e(TAG, "BACKGROUND : Exception getting location"); e.printStackTrace();});
                //Toast.makeText(context, "Start LOCATION BACKGROUND", Toast.LENGTH_LONG).show();
                Log.d(TAG, "START LOCATION BACKGROUND!");


            }else{
                locationRequest = LocationUtilities.createLocationRequestForeground();
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper)
                        .addOnFailureListener(e -> { Log.e(TAG, "FOREGROUND: Exception getting location"); e.printStackTrace();});
                //Toast.makeText(context, "Start LOCATION FOREGROUND", Toast.LENGTH_LONG).show();
                Log.d(TAG, "START LOCATION FOREGROUND!");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "NON HAI I PERMESSI X LA LOCALIZZAZIONE");
            e.printStackTrace();
        }

    }

    public void stopDrawing(){
        if (fusedLocationClient != null) {
            unRegisterBroadcastReceiver();
            if(isBackground)
                fusedLocationClient.removeLocationUpdates(pendingLocation);
            else
                fusedLocationClient.removeLocationUpdates(locationCallback);


            Log.d(TAG, "STOP LOCATION!");
            //Toast.makeText(context, "STOP LOCATION", Toast.LENGTH_LONG).show();
        }
    }

    public void pause() {
        Log.d(TAG, "PAUSE LOCATION!");
        //Toast.makeText(context, "PAUSE LOCATION", Toast.LENGTH_LONG).show();
        stopDrawing();
    }

    public void restart(Looper looper) {
        Log.d(TAG, "RESTART LOCATION!");
        //Toast.makeText(context, "RESTART LOCATION", Toast.LENGTH_LONG).show();
        startDrawing(looper);
    }

    public class MapRouteBackgroundReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent!=null && intent.getAction()!=null) {
                switch (intent.getAction()) {
                    case ActionReceiver.UPDATE_CHANGE_LOCATION:
                        Log.d(TAG, "Location RECEIVER = "+LocationResult.hasResult(intent));
                        if(LocationResult.hasResult(intent)){
                            LocationResult locationResult = LocationResult.extractResult(intent);
                            if(locationResult!=null){
                                List<Location> locations = locationResult.getLocations();
                                Log.d(TAG, "Locations = "+ locations);
                                Preferences.MapLocation.addAll(context, locations);
                                Toast.makeText(context, "Locations = " + locations, Toast.LENGTH_SHORT).show();
                                locations.clear();
                            }
                        }

                        break;
                }
            }
        }
    }

    private boolean isRegister(){
        return isRegister;
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(ActionReceiver.UPDATE_CHANGE_LOCATION);
        if(!isRegister()){
            context.registerReceiver(broadcastReceiver, intentFilter);
            isRegister =true;
        }
    }

    private void unRegisterBroadcastReceiver() {
        if(isRegister()){
            context.unregisterReceiver(broadcastReceiver);
            isRegister = false;
        }
    }

    public interface OnChangeLocationListener{
        void addPolyLineOnMap(PolylineOptions options); // TODO: 2/15/2020 RIMUOVERE ARGOMENTO
    }
}
