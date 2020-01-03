package com.run_walk_tracking_gps.gui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.exception.NoGPSException;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.gui.components.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.components.dialog.MapTypeDialog;
import com.run_walk_tracking_gps.utilities.CollectionsUtilities;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapFragment.class.getName();

    private static final int MAP_TYPE_DEFAULT = GoogleMap.MAP_TYPE_NORMAL;
    private static final float DEFAULT_ZOOM = 15f;

    private static final String ROUTE = "route";

    private String routeStr;
    private MapView mapView;
    private GoogleMap googleMap;

    private ImageView myLocation;
    private ImageView typeMap;

    public static MapFragment createWithArguments(final String routeStr){
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ROUTE, routeStr);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if(getArguments()!=null){
            routeStr = getArguments().getString(ROUTE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        myLocation = view.findViewById(R.id.mylocation);
        typeMap = view.findViewById(R.id.type_map);

        if(routeStr!=null) ((LinearLayout)myLocation.getParent()).setVisibility(View.GONE);
        setListener();

        return view;
    }

    private void setListener(){
        myLocation.setOnClickListener(v -> {
            Log.d(TAG, "My Location");
            myLocation(getActivity());
        });

        typeMap.setOnClickListener(v -> {
            Log.d(TAG, "Choose Type Map");
            MapTypeDialog.create(getContext(), getMapType(), (ChooseDialog.OnSelectedItemListener<Integer>) (val, description) -> {
                setMapType(val);
                Log.d(TAG, "Map = " + description);
            }).show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        setGoogleMap(getActivity(), googleMap, routeStr==null);
        if(routeStr!=null){
            addPolyLine(routeStr);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mapView.onDestroy();
    }

    private void setMapType(Integer val) {
        if(googleMap!=null) googleMap.setMapType(val);
    }

    private int getMapType() {
        return googleMap.getMapType();
    }

    public void addPolyLine(PolylineOptions options) {
        if(googleMap!=null) googleMap.addPolyline(options);
    }

    private void addPolyLine(String mapRoute) {
        final PolylineOptions polylineOptions = Factory.CustomPolylineOptions.create();
        final List<LatLng> route = CollectionsUtilities.convertStringToListLatLng(mapRoute);
        route.forEach(polylineOptions::add);
        googleMap.addPolyline(polylineOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(0), DEFAULT_ZOOM));
    }

    private void setGoogleMap(Activity activity, GoogleMap gMap, boolean myLocation) {
        try {
            googleMap = gMap;
            googleMap.setMapType(MAP_TYPE_DEFAULT);

            if(myLocation){
                googleMap.setMyLocationEnabled(true);
                myLocation(activity);
            }

        }catch (SecurityException e){
            LocationUtilities.setLocationPermission(activity);
        }
    }

    private void myLocation(Activity activity){
        Log.d(TAG, "viewMap");
        try {
            if (!LocationUtilities.isGpsEnable(activity)) throw new NoGPSException(activity);

            final FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, location -> {
                if (location != null) {
                    Log.d(TAG, "addOnSuccessListener: found location!");
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                }
            });

        }catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (NoGPSException ex) { ex.alert();  }
    }


    public void onBlockScreenClickListener(boolean is) {
        Log.d(TAG, "Block/Unblock into map_fragment");
        myLocation.setClickable(is);
        typeMap.setClickable(is);
    }

}
