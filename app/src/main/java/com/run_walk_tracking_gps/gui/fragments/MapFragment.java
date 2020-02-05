package com.run_walk_tracking_gps.gui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.exception.NoGPSException;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.gui.components.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.components.dialog.MapTypeDialog;
import com.run_walk_tracking_gps.utilities.CollectionsUtilities;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.run_walk_tracking_gps.utilities.LocationUtilities.LOCATION_PERMISSION_REQUEST_CODE;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapFragment.class.getName();

    private static final int MAP_TYPE_DEFAULT = GoogleMap.MAP_TYPE_NORMAL;
    private static final float DEFAULT_ZOOM = 17f;

    private static final String ROUTE = "route";

    private String routeStr;
    private MapView mapView;
    private GoogleMap googleMap;

    private ImageView myLocation;
    private ImageView typeMap;

    public static MapFragment createWithArguments(final String routeStr) {
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ROUTE, routeStr);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if (getArguments() != null) {
            routeStr = getArguments().getString(ROUTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        myLocation = view.findViewById(R.id.mylocation);
        typeMap = view.findViewById(R.id.type_map);

        if (routeStr != null) ((RelativeLayout) myLocation.getParent()).setVisibility(View.GONE);
        setListener();

        return view;
    }

    private void setListener() {
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
        setGoogleMap(getActivity(), googleMap, routeStr == null);
        if (routeStr != null) {
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
        if (googleMap != null) googleMap.setMapType(val);
    }

    private int getMapType() {
        return googleMap.getMapType();
    }

    public void addPolyLine(PolylineOptions options) {

        if (googleMap != null) {
            googleMap.addPolyline(options);
            Log.d(TAG, "Rendering Map");
        }
    }

    private void addPolyLine(String mapRoute) {
        final PolylineOptions polylineOptions = Factory.CustomPolylineOptions.create();
        final List<LatLng> route = CollectionsUtilities.convertStringToListLatLng(mapRoute);
        route.forEach(polylineOptions::add);
        googleMap.addPolyline(polylineOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(0), DEFAULT_ZOOM));

        googleMap.addMarker(new MarkerOptions().position(route.get(0)).title(getContext().getString(R.string.departure)));
        googleMap.addMarker(new MarkerOptions().position(route.get(route.size() - 1)).title(getContext().getString(R.string.arrivals)));
    }

    private void setGoogleMap(Activity activity, GoogleMap gMap, boolean myLocation) {
        googleMap = gMap;
        googleMap.setMapType(MAP_TYPE_DEFAULT);

        if (myLocation) {
            //googleMap.setMyLocationEnabled(true);
            myLocation(activity);
        }
    }

    @SuppressLint("MissingPermission")
    public void myLocation(Activity activity) {
        Log.d(TAG, "viewMap");

        try {

            if (!LocationUtilities.isGpsEnable(activity))
                throw new NoGPSException(activity);


            final FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

            if (LocationUtilities.hasPermission(getContext())) {

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        Log.d(TAG, "addOnSuccessListener: found location!");
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                    }
                });

            }else{
                LocationUtilities.setLocationPermission(getActivity());
            }


        }catch (NoGPSException ex) { ex.alert();  }
    }

    public void onBlockScreenClickListener(boolean is) {
        Log.d(TAG, "Block/Unblock into map_fragment");
        myLocation.setClickable(is);
        typeMap.setClickable(is);
    }

}
