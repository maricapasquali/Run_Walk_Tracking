package com.run_walk_tracking_gps.gui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.builder.WorkoutBuilder;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.service.MapRouteService;
import com.run_walk_tracking_gps.service.WorkoutServiceHandler;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class HomeFragment extends Fragment implements MapRouteService.OnChangeLocationListener{

    private static final String TAG = HomeFragment.class.getName();

    private static final String WEIGHT = Measure.Type.WEIGHT.toString();

    private View rootView;
    private TextView sport;

    private FloatingActionButton start;
    private static FloatingActionButton pause;
    private static FloatingActionButton restart;
    private static FloatingActionButton stop;
    private FloatingActionButton block_screen;
    private FloatingActionButton unlock_screen;
    private TextView workout_duration;
    private TextView workout_distance;
    private TextView workout_energy;

    private OnStopWorkoutClickListener onStopWorkoutClickListener;
    private OnBlockScreenClickListener onBlockScreenClickListener;

    private ArrayList<Measure> workoutMeasure = new ArrayList<>();

    private Workout workout;
    private WorkoutServiceHandler workoutService;

     public static HomeFragment createWithArgument(double w) {
        final HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putDouble(WEIGHT, w);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onStopWorkoutClickListener = (OnStopWorkoutClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStopWorkoutClickListener");
        }
        try {
            onBlockScreenClickListener = (OnBlockScreenClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnBlockScreenClickListener");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d(TAG, "onCreate");
        final Bundle bundle = getArguments();
        if (bundle != null) {
                double weight = bundle.getDouble(WEIGHT);

                workoutService = WorkoutServiceHandler.createService(getContext(), weight, (sec, distance, energy) -> {
                    workout_duration.setText(Measure.DurationUtilities.format(sec));
                    workout.getDuration().setValue(true, (double) sec);

                    workout.getDistance().setValue(true, distance);
                    workout_distance.setText(workout.getDistance().toString(true));

                    workout.getCalories().setValue(true, energy);
                    workout_energy.setText(workout.getCalories().toString(true));
                    //Toast.makeText(getContext(), "Duration (sec): "+sec+", Distance passed: " + distance+" = Calories work: " +energy , Toast.LENGTH_SHORT).show();
                }, this);
            }
     }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        start = rootView.findViewById(R.id.start_workout);
        pause = rootView.findViewById(R.id.pause_workout);
        restart = rootView.findViewById(R.id.restart_workout);
        stop = rootView.findViewById(R.id.stop_workout);
        block_screen = rootView.findViewById(R.id.block_screen);
        unlock_screen = rootView.findViewById(R.id.unlock_screen);

        sport = rootView.findViewById(R.id.sport);
        setSport();

        workoutMeasure.add(Measure.create(getContext(), Measure.Type.DURATION));
        workoutMeasure.add(Measure.create(getContext(), Measure.Type.DISTANCE));
        workoutMeasure.add(Measure.create(getContext(), Measure.Type.ENERGY));

        //initialize map
        final boolean isGps = LocationUtilities.isGpsEnable(getContext());
        getFragmentManager().beginTransaction()
                            .add(R.id.map, isGps? new MapFragment() : new IndoorFragment(), TAG)
                            .commit();
        setIndoor(!isGps);

        setListener();

        workout = WorkoutBuilder.create(getContext()).build();
        return rootView;
    }

    private void setSport(){
         final Sport sport_e = DefaultPreferencesUser.getSportDefault(getContext());
         sport.setText(getString(sport_e.getStrId()));
         sport.setCompoundDrawablesWithIntrinsicBounds(sport_e.getIconId(), 0, 0,0);
         sport.getCompoundDrawables()[0].setColorFilter(sport.getTextColors().getDefaultColor(), PorterDuff.Mode.MULTIPLY);
    }

    @SuppressLint("RestrictedApi")
    private void setListener() {

        start.setOnClickListener(v -> {
            getActivity().findViewById(R.id.nav_bar).setVisibility(View.GONE);
            v.setVisibility(View.GONE);
            block_screen.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);

            //START SERVICE
            workoutService.start();
        });

        pause.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            block_screen.setVisibility(View.GONE);
            restart.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);

            //PAUSE SERVICE
            workoutService.pause();
        });

        restart.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            block_screen.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);

            //RESTART SERVICE
            workoutService.restart();
        });

        stop.setOnClickListener(v -> {
            String listCoord = workoutService.getMapRouteService().getListCoordinates();
            // STOP SERVICE
            workoutService.stop();
            // Auto - Workout
            onStopWorkoutClickListener.OnStopWorkoutClick(WorkoutBuilder.create(getContext())
                    .setMapRoute(listCoord)
                    .setDate(Calendar.getInstance().getTime())
                    .setDistance(workout.getDistance().getValue(false))
                    .setDuration(workout.getDuration().getValue(true).intValue())
                    .setCalories(workout.getCalories().getValue(false))
                    .setMiddleSpeed()
                    .setSport(workout.getSport())
                    .build());
        });

        block_screen.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            pause.setVisibility(View.GONE);
            unlock_screen.setVisibility(View.VISIBLE);
            setClickable(false);
            // BLOCK (remove controller button) NOTIFICATION SERVICE
            workoutService.block();
        });

        unlock_screen.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            block_screen.setVisibility(View.VISIBLE);
            setClickable(true);
            // UNBLOCK (add controller button) NOTIFICATION SERVICE
            workoutService.unblock();
        });
    }

    private void setClickable(final boolean is) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
        if(fragment instanceof MapFragment){
            ((MapFragment)fragment).onBlockScreenClickListener(is);
        }
        onBlockScreenClickListener.onBlockScreenClickListener(is);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        setSport();

        setIndoor(!LocationUtilities.isGpsEnable(getContext()));

        if(getFragmentManager()!=null){
            if(getFragmentManager().findFragmentByTag(TAG) instanceof MapFragment &&
                    !LocationUtilities.isGpsEnable(getContext())){
                Log.d(TAG, "Change to IndoorFragment");
                setIndoor(true);
                getFragmentManager().beginTransaction().replace(R.id.map, new IndoorFragment(), TAG).commit();
            }

            if(getFragmentManager().findFragmentByTag(TAG) instanceof IndoorFragment &&
                    LocationUtilities.isGpsEnable(getContext())){
                Log.d(TAG, "Change to MapFragment");
                setIndoor(false);
                getFragmentManager().beginTransaction().replace(R.id.map, new MapFragment(), TAG).commit();
            }
        }
    }

    private void setIndoor(final boolean isIndoor){
        final LinearLayout duration = rootView.findViewById(R.id.duration_layout);
        final LinearLayout distance = rootView.findViewById(R.id.distance_layout);
        final LinearLayout calories = rootView.findViewById(R.id.calories_layout);
        final LinearLayout info_workout = rootView.findViewById(R.id.info_workout);
        final LinearLayout info_workout_numbers = rootView.findViewById(R.id.info_workout_numbers);

        workout_distance = rootView.findViewById(R.id.distance_workout_indoor);
        workout_energy = rootView.findViewById(R.id.calories_workout_indoor);
        workout_duration = rootView.findViewById(R.id.time_workout_indoor);

        workout_distance = rootView.findViewById(R.id.distance_workout);
        workout_energy = rootView.findViewById(R.id.calories_workout);
        workout_duration = rootView.findViewById(R.id.time_workout);

        duration.setVisibility(isIndoor?View.VISIBLE : View.GONE);
        distance.setVisibility(isIndoor?View.VISIBLE : View.GONE);
        calories.setVisibility(isIndoor?View.VISIBLE : View.GONE);
        info_workout.setVisibility(isIndoor?View.GONE : View.VISIBLE);
        info_workout_numbers.setVisibility(isIndoor?View.GONE : View.VISIBLE);

        workout_duration.setText(isWorkoutRunning() ? workout.getDuration().toString(true): workoutMeasure.get(0).toString(true));
        workout_distance.setText(isWorkoutRunning() ? workout.getDistance().toString(true) : workoutMeasure.get(1).toString(true));
        workout_energy.setText(isWorkoutRunning() ? workout.getCalories().toString(true): workoutMeasure.get(2).toString(true));

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if(isWorkoutRunning()) workoutService.stop();
    }

    public boolean isWorkoutRunning(){
        return workoutService.isRunning();
    }

    public static List<FloatingActionButton> getControllerButton() {
        return Arrays.asList(pause, restart, stop);
    }

    // COMMUNICATION WITH MAP FRAGMENT
    @Override
    public void addPolyLineOnMap(PolylineOptions options) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
        if(fragment instanceof MapFragment){
            ((MapFragment)fragment).addPolyLine(options);
        }
    }

    // COMMUNICATION WITH ACTIVITY
    public interface OnStopWorkoutClickListener{
        void OnStopWorkoutClick(Workout workout);
    }

    public interface OnBlockScreenClickListener{
        void onBlockScreenClickListener(boolean isClickable);
    }
}
