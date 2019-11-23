package com.run_walk_tracking_gps.gui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Service;
import android.content.Context;

import android.content.Intent;
import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;


import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.run_walk_tracking_gps.exception.NoGPSException;
import com.run_walk_tracking_gps.gui.components.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.components.dialog.MapTypeDialog;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.WorkoutBuilder;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.receiver.ReceiverNotificationButtonHandler;
import com.run_walk_tracking_gps.service.WorkoutServiceHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class HomeFragment extends Fragment implements OnMapReadyCallback ,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = HomeFragment.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private static final String WEIGHT = Measure.Type.WEIGHT.toString();

    private static final int MAP_TYPE_DEFAULT = GoogleMap.MAP_TYPE_NORMAL;

    private View rootView;
    private TextView sport;
    private ImageView myLocation;
    private ImageView typeMap;
    private MapView mapView;
    private FloatingActionButton start;
    private static FloatingActionButton pause;
    private static FloatingActionButton restart;
    private static FloatingActionButton stop;
    private FloatingActionButton block_screen;
    private FloatingActionButton unlock_screen;

    private TextView workout_duration;
    private TextView workout_distance;
    private TextView workout_energy;

    private GoogleMap map;
    private boolean requestPermissions=false;
    private FusedLocationProviderClient fusedLocationClient;

    private OnStopWorkoutClickListener onStopWorkoutClickListener;
    private OnBlockScreenClickListener onBlockScreenClickListener;

    private List<Measure> workoutMeasure = new ArrayList<>();

    private Workout workout;

    private WorkoutServiceHandler workoutService;

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
        try {
            onBlockScreenClickListener = (OnBlockScreenClickListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnBlockScreenClickListener");
        }
    }

    public static HomeFragment createWithArgument(double w){
        final HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putDouble(WEIGHT, w);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();
        if(bundle!=null){
            double weight = bundle.getDouble(WEIGHT);

            try {
                if(weight==0.0) throw new Exception(WEIGHT);
                //Toast.makeText(getContext(), "Weight Fragment : "+ weight, Toast.LENGTH_LONG).show();
                workoutService = WorkoutServiceHandler.createService(getContext(), weight , (sec, distance, energy) -> {

                    workout_duration.setText(Measure.DurationUtilities.format(sec));
                    workout.getDuration().setValue((double) sec);

                    workout.getDistance().setValue(distance);
                    workout_distance.setText(workout.getDistance().toString(true));

                    workout.getCalories().setValue(energy);
                    workout_energy.setText(workout.getCalories().toString(true));
                    //Toast.makeText(getContext(), "Duration (sec): "+sec+", Distance passed: "
                    // + distance+" = Calories work: " +energy , Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public static List<FloatingActionButton> getControllerButton(){
        return Arrays.asList(pause, restart, stop);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
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

        sport = rootView.findViewById(R.id.sport);
        workout_distance = rootView.findViewById(R.id.distance_workout);
        workout_energy = rootView.findViewById(R.id.calories_workout);
        workout_duration = rootView.findViewById(R.id.time_workout);

        workoutMeasure.add(Measure.create(getContext(), Measure.Type.DURATION));
        workoutMeasure.add(Measure.create(getContext(), Measure.Type.DISTANCE));
        workoutMeasure.add(Measure.create(getContext(), Measure.Type.ENERGY));

        workout_duration.setText(workoutMeasure.get(0).toString(true));
        workout_distance.setText(workoutMeasure.get(1).toString(true));
        workout_energy.setText(workoutMeasure.get(2).toString(true));

        sport.setText(Sport.defaultForUser(getContext()));

        //initialize map
        initMapView(savedInstanceState);
        setListener();

        workout = WorkoutBuilder.create(getContext()).build();
        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void startWorkoutUpdateGui(View v){
        getActivity().findViewById(R.id.nav_bar).setVisibility(View.GONE);
        v.setVisibility(View.GONE);
        block_screen.setVisibility(View.VISIBLE);
        pause.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void pauseWorkoutUpdateGui(View v){
        v.setVisibility(View.GONE);
        block_screen.setVisibility(View.GONE);

        restart.setVisibility(View.VISIBLE);
        stop.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void restartWorkoutUpdateGui(View v){
        v.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);

        block_screen.setVisibility(View.VISIBLE);
        pause.setVisibility(View.VISIBLE);
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
            startWorkoutUpdateGui(v);
            //START SERVICE
            workoutService.start();
        });

        pause.setOnClickListener(v ->{
            pauseWorkoutUpdateGui(v);
            //PAUSE SERVICE
            workoutService.pause();
        });

        restart.setOnClickListener(v ->{
            restartWorkoutUpdateGui(v);
            //RESTART SERVICE
            workoutService.restart();
        });

        stop.setOnClickListener(v ->{

            // STOP SERVICE
            workoutService.stop();
            // Auto - Workout
            onStopWorkoutClickListener.OnStopWorkoutClick(WorkoutBuilder.create(getContext())
                                                                        .setMapRoute("not null")
                                                                        .setDate(Calendar.getInstance().getTime())
                                                                        .setDistance(workout.getDistance().getValueToGui())
                                                                        .setDuration(workout.getDuration().getValue().intValue())
                                                                        .setCalories(workout.getCalories().getValueToGui())
                                                                        .setMiddleSpeed()
                                                                        .setSport(workout.getSport())
                                                                        .build());
        });

        block_screen.setOnClickListener(v ->{
            v.setVisibility(View.GONE);
            pause.setVisibility(View.GONE);
            unlock_screen.setVisibility(View.VISIBLE);
            setClickable(false);
            // BLOCK (remove controller button) NOTIFICATION SERVICE
            workoutService.block();
        });

        unlock_screen.setOnClickListener(v ->{
            v.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            block_screen.setVisibility(View.VISIBLE);
            setClickable(true);
            // UNBLOCK (add controller button) NOTIFICATION SERVICE
            workoutService.unblock();
        });
    }

    private void setClickable(final boolean is){
        myLocation.setClickable(is);
        typeMap.setClickable(is);
        onBlockScreenClickListener.onBlockScreenClickListener(is);
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
                throw new NoGPSException(getContext());


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
            ex.alert();
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

        workout_distance.setText(workoutMeasure.get(1).toString(true));
        workout_energy.setText(workoutMeasure.get(2).toString(true));
        sport.setText(Sport.defaultForUser(getContext()));

        if(isWorkoutRunning()){
            workout_distance.setText(workout.getDistance().toString(true));
            workout_energy.setText(workout.getCalories().toString(true));
        }



        if(requestPermissions){
            mapView.getMapAsync(this);
            requestPermissions = false;
        }
    }

    public boolean isWorkoutRunning(){
        return workoutService.isRunning();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mapView.onDestroy();
        if(workoutService.isRunning()) workoutService.stop();
    }

    // COMMUNICATION WITH ACTIVITY
    public interface OnStopWorkoutClickListener{
        void OnStopWorkoutClick(Workout workout);
    }

    public interface OnBlockScreenClickListener{
        void onBlockScreenClickListener(boolean isClickable);
    }


}
