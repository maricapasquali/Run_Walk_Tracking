package com.run_walk_tracking_gps.gui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ncorti.slidetoact.SlideToActView;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.controller.ErrorQueue;
import com.run_walk_tracking_gps.db.dao.SqlLiteSettingsDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteStatisticsDao;
import com.run_walk_tracking_gps.gui.BootAppActivity;
import com.run_walk_tracking_gps.gui.components.dialog.DelayedStartWorkoutDialog;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.service.MapRouteDraw;
import com.run_walk_tracking_gps.model.VoiceCoach;
import com.run_walk_tracking_gps.service.WorkoutService;
import com.run_walk_tracking_gps.service.WorkoutServiceHandler;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment
                          implements WorkoutService.OnReceiverListener,
                                     WorkoutServiceHandler.RestoreViewListener {

    private static final String TAG = HomeFragment.class.getName();

    private static final String RESTORE = "restore";

    private View rootView;
    private TextView sport;

    private FloatingActionButton start;
    @SuppressLint("StaticFieldLeak")
    private static FloatingActionButton pause;
    @SuppressLint("StaticFieldLeak")
    private static FloatingActionButton restart;
    private FloatingActionButton stop;
    private FloatingActionButton block_screen;
    private SlideToActView unlock_screen;
    private TextView workout_duration;
    private TextView workout_distance;
    private TextView workout_energy;

    private OnStopWorkoutClickListener onStopWorkoutClickListener;
    private OnBlockScreenClickListener onBlockScreenClickListener;

    private ArrayList<Measure> workoutMeasure = new ArrayList<>();

    private String restore = null;
    private WorkoutServiceHandler serviceHandler;

    public static HomeFragment createWithArgument(String restore) {
        final HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(RESTORE, restore);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            onStopWorkoutClickListener = (OnStopWorkoutClickListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStopWorkoutClickListener");
        }
        try {
            onBlockScreenClickListener = (OnBlockScreenClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnBlockScreenClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        final Bundle bundle = getArguments();
        restore = (bundle == null ? null : bundle.getString(RESTORE));

        serviceHandler = new WorkoutServiceHandler(getContext(),
                SqlLiteStatisticsDao.SqlLiteWeightDao.create(getContext()).getLast(),
                restore == null ? null : this, this);

    }

    @SuppressLint("RestrictedApi")
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
        getFragmentManager().beginTransaction().add(R.id.map, isGps ? new MapFragment() : new IndoorFragment(), TAG).commit();
        setIndoor(!isGps);
        setListener();

        return rootView;
    }

    private void setSport(){

        try {
            final Sport sport_e = Sport.valueOf(SqlLiteSettingsDao.create(getContext()).getSportDefault());
            sport.setText(getString(sport_e.getStrId()));
            sport.setCompoundDrawablesWithIntrinsicBounds(sport_e.getIconId(), 0, 0,0);
            sport.getCompoundDrawables()[0].setColorFilter(sport.getTextColors().getDefaultColor(), PorterDuff.Mode.MULTIPLY);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Preferences.Session.logout(getContext());
            getContext().startActivity(new Intent(getContext(), BootAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if(restore!=null) serviceHandler.bindService();
    }

    public void stop(){
        stop.callOnClick();
    }

    /* -- UPDATE GUI ACTION RUNNING WORKOUTSERVICE -- */
    @SuppressLint("RestrictedApi")
    private void startState(){
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().findViewById(R.id.nav_bar).setVisibility(View.GONE);
        start.setVisibility(View.GONE);
        block_screen.setVisibility(View.VISIBLE);
        pause.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void pauseState(){
        pause.setVisibility(View.GONE);
        block_screen.setVisibility(View.GONE);
        restart.setVisibility(View.VISIBLE);
        stop.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void lockState(){
        block_screen.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);
        unlock_screen.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void unlockState(){
        unlock_screen.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        block_screen.setVisibility(View.VISIBLE);
    }
    /* -- FINE UPDATE GUI ACTION RUNNING WORKOUTSERVICE -- */

    @SuppressLint("RestrictedApi")
    private void setListener() {

        start.setOnClickListener(v ->
                DelayedStartWorkoutDialog.create(getContext(), () -> {
                    startState();
                    //START SERVICE
                    serviceHandler.bindService();
                }).show()
        );

        pause.setOnClickListener(v -> {
            pauseState();
            //PAUSE SERVICE
            serviceHandler.pauseService();
        });

        restart.setOnClickListener(v -> {

            v.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            block_screen.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);
            //RESTART SERVICE
            serviceHandler.restartService();
        });

        stop.setOnClickListener(v -> {
            // STOP SERVICE

            final Workout workout = serviceHandler.getWorkout();
            serviceHandler.stopService();
            onStopWorkoutClickListener.OnStopWorkoutClick(workout);
        });

        block_screen.setOnClickListener(v -> {
            lockState();
            setClickable(false);
            // BLOCK (remove controller button) NOTIFICATION SERVICE
            serviceHandler.lockService();
        });

        unlock_screen.setOnSlideCompleteListener(v ->{
            v.resetSlider();
            unlockState();
            setClickable(true);
            // UNBLOCK (add controller button) NOTIFICATION SERVICE
            serviceHandler.unLockService();
        });
    }

    private void setClickable(final boolean is) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
        if(fragment instanceof MapFragment){
            ((MapFragment)fragment).onBlockScreenClickListener(is);
        }
        onBlockScreenClickListener.onBlockScreenClick(is);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        ErrorQueue.getErrors(getContext());
        //TODO: 1/3/2020 ADD SYNC SERVICE ONRESUME
        //SyncServiceHandler.create(getContext()).start();
        setWorkoutRunning();
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

        if(isIndoor){
            workout_distance = rootView.findViewById(R.id.distance_workout_indoor);
            workout_energy = rootView.findViewById(R.id.calories_workout_indoor);
            workout_duration = rootView.findViewById(R.id.time_workout_indoor);
        }else {
            workout_distance = rootView.findViewById(R.id.distance_workout);
            workout_energy = rootView.findViewById(R.id.calories_workout);
            workout_duration = rootView.findViewById(R.id.time_workout);
        }

        duration.setVisibility(isIndoor ? View.VISIBLE : View.GONE);
        distance.setVisibility(isIndoor ? View.VISIBLE : View.GONE);
        calories.setVisibility(isIndoor ? View.VISIBLE : View.GONE);
        info_workout.setVisibility(isIndoor ? View.GONE : View.VISIBLE);
        info_workout_numbers.setVisibility(isIndoor ? View.GONE : View.VISIBLE);

        setWorkoutRunning();
    }

    private void setWorkoutRunning(){
        if(serviceHandler.isWorkoutRunning()){
            workout_duration.setText(serviceHandler.getWorkout().getDuration().toString(true));
            workout_distance.setText(serviceHandler.getWorkout().getDistance().toString(true) );
            workout_energy.setText(serviceHandler.getWorkout().getCalories().toString(true));
        }else{
            workout_duration.setText(workoutMeasure.get(0).toString(true));
            workout_distance.setText(workoutMeasure.get(1).toString(true));
            workout_energy.setText(workoutMeasure.get(2).toString(true));
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        try{
            serviceHandler.unBindService();
        }catch (IllegalArgumentException e){
            Log.e(TAG, "Service just unbind");
        }
        super.onDestroy();
    }

    public WorkoutServiceHandler getServiceHandler(){
        return serviceHandler;
    }

    public static List<FloatingActionButton> getControllerButton(){
        return Arrays.asList(pause, restart);
    }

    // RECEIVER LISTENER
    @Override
    public void onReceiverDuration(String sec) {
        workout_duration.setText(sec);
    }

    @Override
    public void onReceiverDistance(String distance) {
        workout_distance.setText(distance);
    }

    @Override
    public void onReceiverEnergy(String energy) {
        workout_energy.setText(energy);
    }

    @Override
    public MapRouteDraw.OnChangeLocationListener onReceiverMapRoute() {
        return polylineOptions ->{
            if(getFragmentManager()!=null){
                Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
                if(fragment instanceof MapFragment) ((MapFragment)fragment).addPolyLine(polylineOptions);
            }
        };
    }

    @Override
    public void restoreView(WorkoutService wService) {
        switch (restore){
            case ActionReceiver.STOP_ACTION:
                HomeFragment.this.stop();
                break;
            case ActionReceiver.RUNNING_WORKOUT: {
                HomeFragment.this.setIndoor(!LocationUtilities.isGpsEnable(getContext()));
                HomeFragment.this.startState();
                if(wService.isPause())  HomeFragment.this.pauseState();
                else{
                    if(wService.isLock()){
                        HomeFragment.this.lockState();
                        // TODO: 12/10/2019 NON FUNZIONA perchè menu viene creato dopo
                        //setClickable(false);
                    }else
                        HomeFragment.this.unlockState();
                }
            }
            break;
        }
    }

    // COMMUNICATION WITH ACTIVITY
    public interface OnStopWorkoutClickListener{
        void OnStopWorkoutClick(Workout workout);
    }

    public interface OnBlockScreenClickListener{
        void onBlockScreenClick(boolean isClickable);
    }

}
