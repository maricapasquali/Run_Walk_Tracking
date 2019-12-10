package com.run_walk_tracking_gps.service;

import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import java.util.ArrayList;
import java.util.List;

public class MapRouteDraw {

    private static final String TAG = MapRouteDraw.class.getName();

    private PolylineOptions polylineOptions = Factory.CustomPolylineOptions.create();
    private List<LatLng> route = new ArrayList<>();

    private LocationRequest locationRequest;

    private FusedLocationProviderClient fusedLocationClient;

    private OnChangeLocationListener onChangeLocationListener;
    // TODO: 12/6/2019 RIGUARDARE 
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                // Update UI with location data
                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                polylineOptions.add(latLng);
                route.add(latLng);
                //Log.e(TAG, latLng.toString()); Toast.makeText(context, latLng.toString(), Toast.LENGTH_LONG).show();
            }
            onChangeLocationListener.addPolyLineOnMap(polylineOptions);
        }
    };

    public MapRouteDraw(){}

    public MapRouteDraw(Context context){
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.locationRequest = LocationUtilities.createLocationRequest();
    }

    public void setOnChangeLocationListener(OnChangeLocationListener onChangeLocationListener) {
        this.onChangeLocationListener = onChangeLocationListener;
    }

    public void start(){
        try {
            if (fusedLocationClient != null) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    public void pause(){
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public void stop(){
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            route.clear();
        }
    }

    public String getListCoordinates() {
        return route.isEmpty() ? null : route.toString();
    }

    public interface OnChangeLocationListener{
        void addPolyLineOnMap(PolylineOptions options);
    }
}
