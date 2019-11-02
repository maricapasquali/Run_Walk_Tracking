package com.run_walk_tracking_gps.gui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.app.Service;
import android.content.Context;

import android.content.Intent;

import android.location.LocationManager;

import android.os.Bundle;


import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;



import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.dialog.MapTypeDialog;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.WorkoutBuilder;
import com.run_walk_tracking_gps.model.enumerations.Sport;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.stream.Stream;

public class HomeFragment extends Fragment implements OnMapReadyCallback ,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = HomeFragment.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private static final int MAP_TYPE_DEFAULT = GoogleMap.MAP_TYPE_NORMAL;

    private View rootView;
    private ImageView myLocation;
    private ImageView typeMap;
    private MapView mapView;
    private FloatingActionButton start;
    private FloatingActionButton pause;
    private FloatingActionButton restart;
    private FloatingActionButton stop;
    private FloatingActionButton block_screen;
    private FloatingActionButton unlock_screen;

    private GoogleMap map;
    private boolean requestPermissions=false;
    private FusedLocationProviderClient fusedLocationClient;

    private OnStopWorkoutClickListener onStopWorkoutClickListener;

    private boolean inWorkout = false;

    public HomeFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onStopWorkoutClickListener = (OnStopWorkoutClickListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStopWorkoutClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        myLocation = rootView.findViewById(R.id.mylocation);
        typeMap = rootView.findViewById(R.id.type_map);
        start = rootView.findViewById(R.id.start_workout);

        pause = rootView.findViewById(R.id.pause_workout);
        restart = rootView.findViewById(R.id.restart_workout);
        stop = rootView.findViewById(R.id.stop_workout);
        block_screen = rootView.findViewById(R.id.block_screen);
        unlock_screen = rootView.findViewById(R.id.unlock_screen);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        TextView sport = rootView.findViewById(R.id.sport);
        TextView distance_workout = rootView.findViewById(R.id.distance_workout);
        TextView energy_workout = rootView.findViewById(R.id.calories_workout);

        try {
            final String id_user = Preferences.getIdUserLogged(getContext());
            final JSONObject jsonApp = Preferences.getAppJsonUserLogged(getContext(), id_user);

            JSONObject settings = (JSONObject)jsonApp.get(FieldDataBase.SETTINGS.toName());
            JSONObject s_sport = (JSONObject)settings.get(FieldDataBase.SPORT.toName());
            JSONObject s_unit_measure = (JSONObject)settings.get(FieldDataBase.UNIT_MEASURE.toName());

            String s_energy = (String)((JSONObject)s_unit_measure.get(FieldDataBase.ENERGY.toName())).get(FieldDataBase.UNIT.toName());
            String s_distance = (String)((JSONObject)s_unit_measure.get(FieldDataBase.DISTANCE.toName())).get(FieldDataBase.UNIT.toName());

            sport.setText(Sport.valueOf((String)s_sport.get(FieldDataBase.NAME.toName())).getStrId());
            energy_workout.setText(energy_workout.getText() +getString(R.string.space)+ s_energy);
            distance_workout.setText(distance_workout.getText() +getString(R.string.space)+ s_distance);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


        //initialize map
        initMapView(savedInstanceState);
        setListener();
        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void setListener() {
        myLocation.setOnClickListener(v ->{
            Log.d(TAG, "My Location");
            viewMyCurrentLocation();
        });

        typeMap.setOnClickListener(v ->{
            Log.d(TAG, "Choose Type Map");
            MapTypeDialog.create(getContext(), map.getMapType(), (ChooseDialog.OnSelectedItemListener<Integer>) (val, description) -> {
                map.setMapType(val);
                Toast.makeText(getContext(), "Mappa = " + description, Toast.LENGTH_SHORT).show();
            }).show();
        });

        start.setOnClickListener(v ->{
            getActivity().findViewById(R.id.nav_bar).setVisibility(View.GONE);
            v.setVisibility(View.GONE);
            inWorkout = true;

            block_screen.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);
        });

        pause.setOnClickListener(v ->{
            v.setVisibility(View.GONE);
            block_screen.setVisibility(View.GONE);

            restart.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
        });

        restart.setOnClickListener(v ->{
            v.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);

            block_screen.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);

        });

        block_screen.setOnClickListener(v ->{
            v.setVisibility(View.GONE);
            pause.setVisibility(View.GONE);

            unlock_screen.setVisibility(View.VISIBLE);
        });

        unlock_screen.setOnClickListener(v ->{
            v.setVisibility(View.GONE);

            pause.setVisibility(View.VISIBLE);
            block_screen.setVisibility(View.VISIBLE);
        });

        stop.setOnClickListener(v ->{
            inWorkout = false;
            // Esempio Workout
            onStopWorkoutClickListener.OnStopWorkoutClick(WorkoutBuilder.create()
                                                                        .setMapRoute("not null")
                                                                        .setDate(Calendar.getInstance().getTime())
                                                                        .setCalories(400)
                                                                        .setDuration(3600)
                                                                        .setDistance(20)
                                                                        .setMiddleSpeed(9.0)
                                                                        .setSport(Sport.RUN)
                                                                        .build());
        });
    }

    @SuppressLint("RestrictedApi")
    private void resetView() {
        restart.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);
        block_screen.setVisibility(View.GONE);
        unlock_screen.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);

        getActivity().findViewById(R.id.nav_bar).setVisibility(View.VISIBLE);
        start.setVisibility(View.VISIBLE);
    }

    private void initMapView(Bundle savedInstanceState ) {
        Log.d(TAG, "initMapViewView");
        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void viewMyCurrentLocation() {
        Log.d(TAG, "viewMap");
        try {

            final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Service.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                throw new NoGPSException();


            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    Log.d(TAG, "addOnSuccessListener: found location!");
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
                            DEFAULT_ZOOM));
                }

            });

        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
            requestPermissions = true;
            return ;

        } catch (NoGPSException ex) {
            requestPermissions = true;
            ex.alert(getContext());
        }
    }

    private void setLocationPermission() {
        Log.d(TAG, getString(R.string.request_location_permissions));
        final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(getActivity(),
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            Log.d(TAG, "onMapReady");
            map = googleMap;
            map.setMapType(MAP_TYPE_DEFAULT);
            map.setMyLocationEnabled(true);

            viewMyCurrentLocation();

        }catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
            setLocationPermission();
            requestPermissions = true;
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume HomeFragment");
        mapView.onResume();
        super.onResume();

        // TODO: 10/31/2019 RESET GUI SE SETTING SONO CAMBIATI 
        
        if(requestPermissions)
        {
            mapView.getMapAsync(this);
            requestPermissions = false;
        }
        if(!inWorkout) resetView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }

    // COMMUNICATION WITH ACTIVITY
    public interface OnStopWorkoutClickListener{
        void OnStopWorkoutClick(Workout workout);
    }

    // Exception

    private class NoGPSException extends Exception{
        private final static String NO_GPS = "No GPS";
        private final String NO_GPS_MEX = getString(R.string.gps_disabled);

        private void alert(final Context context)
        {
            Log.d(TAG, NO_GPS);
            new AlertDialog.Builder(context)
                    .setMessage(NO_GPS_MEX)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), (dialog, id) ->
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel())
                    .create()
                    .show();

        }

    }
}
