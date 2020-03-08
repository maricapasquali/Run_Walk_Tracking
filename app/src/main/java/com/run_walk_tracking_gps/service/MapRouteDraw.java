package com.run_walk_tracking_gps.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import java.util.ArrayList;
import java.util.List;

public class MapRouteDraw {

    private static final String TAG = MapRouteDraw.class.getName();

    private final Context context;
    private FusedLocationProviderClient fusedLocationClient;

    private static MapRouteDraw mapRouteDraw;

    private Handler handler;

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
                sendMessage(locationResult.getLocations());
                /*new Thread(()-> {
                    Log.d(TAG, "Foreground : Thread name = " + Thread.currentThread().getName()+
                    ", id ="+Thread.currentThread().getId());
                    List<Location> locations = locationResult.getLocations();
                    Log.d(TAG, "Locations = "+ locations);
                    Preferences.WorkoutInExecution.MapLocation.addAll(context, locations);
                    onReceiverListener.addPolyLineOnMap();
                }).start();*/
            }
        }
    };

    private MapRouteDraw(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public static synchronized MapRouteDraw create(Context context) {
        boolean gpsEnable = LocationUtilities.isGpsEnable(context.getApplicationContext());
        if(mapRouteDraw ==null && gpsEnable)
            mapRouteDraw = new MapRouteDraw(context.getApplicationContext());
        else if(mapRouteDraw !=null && !gpsEnable) mapRouteDraw = null;

        return mapRouteDraw;
    }

    // TODO : DA RIMUOVERE
    private MapRouteDraw(Context context, OnChangeLocationListener onChangeLocationListener) {
        this(context);
        this.onReceiverListener = onChangeLocationListener;
    }
    // TODO : DA RIMUOVERE
    public static synchronized MapRouteDraw create(Context context, OnChangeLocationListener  onChangeLocationListener) {
        boolean gpsEnable = LocationUtilities.isGpsEnable(context.getApplicationContext());
        if(mapRouteDraw ==null && gpsEnable)
            mapRouteDraw = new MapRouteDraw(context.getApplicationContext(), onChangeLocationListener);
        else if(mapRouteDraw !=null && !gpsEnable) mapRouteDraw = null;

        return mapRouteDraw; //LocationUtilities.isGpsEnable(context) ? new MapRouteDraw(context, onChangeLocationListener) : null;
    }

    public void setBackground(boolean isBackground){
        this.isBackground = isBackground;
    }

    public void startDrawing(Handler handler) {
        try {
            LocationRequest locationRequest ;
            this.handler = handler;
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
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, handler.getLooper())
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

    public void restart(Handler handler) {
        Log.d(TAG, "RESTART LOCATION!");
        //Toast.makeText(context, "RESTART LOCATION", Toast.LENGTH_LONG).show();
        startDrawing(handler);
    }

    private void sendMessage(List<Location> locations){
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KeysIntent.ROUTE, new ArrayList<>(locations));
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public class MapRouteBackgroundReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getAction()!=null) {
                switch (intent.getAction()) {
                    case ActionReceiver.UPDATE_CHANGE_LOCATION:
                        if(LocationResult.hasResult(intent)){
                            LocationResult locationResult = LocationResult.extractResult(intent);
                            if(locationResult!=null){
                                sendMessage(locationResult.getLocations());

                               /*new Thread(()-> {
                                   Log.d(TAG, "Background: Thread name = " + Thread.currentThread().getName()+", id ="+Thread.currentThread().getId());
                                   List<Location> locations = locationResult.getLocations();
                                   Log.d(TAG, "Locations = "+ locations);
                                   Preferences.WorkoutInExecution.MapLocation.addAll(context, locations);
                               }).start();*/
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
            context.registerReceiver(broadcastReceiver, intentFilter, null, this.handler);
            isRegister =true;
        }
    }

    private void unRegisterBroadcastReceiver() {
        if(isRegister()){
            context.unregisterReceiver(broadcastReceiver);
            isRegister = false;
        }
    }

    // TODO : DA RIMUOVERE
    public interface OnChangeLocationListener{
        void addPolyLineOnMap();
    }
}
