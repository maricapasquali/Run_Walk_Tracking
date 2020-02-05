package com.run_walk_tracking_gps.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;

import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.receiver.ReceiverWorkoutElement;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import java.util.ArrayList;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MapRouteDraw {

    private static final String TAG = MapRouteDraw.class.getName();
    private static final String NAME = "LocationThread";

    private final Context context;

    private PolylineOptions polylineOptions = Factory.CustomPolylineOptions.create();
    private ArrayList<LatLng> route = new ArrayList<>();

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;

    private OnChangeLocationListener onReceiverListener;

    // TODO: 12/6/2019 RIGUARDARE 
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // Perform your long-running tasks here.
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                // Update UI with location data
                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                polylineOptions.add(latLng);
                route.add(latLng);
                Log.e(TAG, latLng.toString());
                //Toast.makeText(context, latLng.toString(), Toast.LENGTH_LONG).show();
            }
            onReceiverListener.addPolyLineOnMap(polylineOptions);
        }
    };


    private MapRouteDraw(Context context) {
        //super(NAME);
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.locationRequest = LocationUtilities.createLocationRequest();
    }

    public static MapRouteDraw create(Context context) {
        return LocationUtilities.isGpsEnable(context) ? new MapRouteDraw(context) : null;
    }

    private MapRouteDraw(Context context, OnChangeLocationListener onChangeLocationListener) {
        this(context);
        this.onReceiverListener = onChangeLocationListener;
    }

    public static MapRouteDraw create(Context context, OnChangeLocationListener  onChangeLocationListener) {
        return LocationUtilities.isGpsEnable(context) ? new MapRouteDraw(context, onChangeLocationListener) : null;
    }

    public void startDrawing() {
        try {
            Log.d(TAG, "Start Thread Location!");
            //start();

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (fusedLocationClient != null) {
            Log.d(TAG, "Pause Thread Location!");
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public void restart() {
        try {
            if (fusedLocationClient != null) {
                Log.d(TAG, "Restart Thread Location!");
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopDrawing(){
        //quit();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            route.clear();
        }
    }


    public String getListCoordinates() {
        return route.isEmpty() ? null : route.toString();
    }

    public void setRoute(ArrayList<LatLng> mapRoute) {
        route = mapRoute;
        polylineOptions.addAll(route);
    }

    public interface OnChangeLocationListener{
        void addPolyLineOnMap(PolylineOptions options);
    }
}
