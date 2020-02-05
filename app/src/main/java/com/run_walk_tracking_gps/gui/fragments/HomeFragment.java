package com.run_walk_tracking_gps.gui.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.ErrorQueue;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.SqlLiteSettingsDao;
import com.run_walk_tracking_gps.db.dao.SqlLiteStatisticsDao;
import com.run_walk_tracking_gps.gui.BootAppActivity;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.gui.components.dialog.DelayedStartWorkoutDialog;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.service.WorkoutService;
import com.run_walk_tracking_gps.utilities.CollectionsUtilities;
import com.run_walk_tracking_gps.utilities.ColorUtilities;
import com.run_walk_tracking_gps.utilities.LocationUtilities;

import org.json.JSONException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private static final String TAG ="HomeViewFragment";

    private static final String RESTORE_VIEW = "restore";

    private View rootView;

    private TextView sport;
    private FloatingActionButton start;
    private FloatingActionButton pause;
    private FloatingActionButton restart;
    private FloatingActionButton stop;

    private Factory.CustomChronometer workout_duration;
    private TextView workout_distance;
    private TextView workout_energy;

    private ArrayList<Measure> workoutMeasure = new ArrayList<>();

    private OnStopWorkoutClickListener onStopWorkoutClickListener;

    private String restore = null;

    public static HomeFragment createWithArgument(String restore) {
        final HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(RESTORE_VIEW, restore);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            onStopWorkoutClickListener = (OnStopWorkoutClickListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStopWorkoutClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        final Bundle bundle = getArguments();
        restore = (bundle == null ? null : bundle.getString(RESTORE_VIEW));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        start = rootView.findViewById(R.id.start_workout);
        pause = rootView.findViewById(R.id.pause_workout);
        restart = rootView.findViewById(R.id.restart_workout);
        stop = rootView.findViewById(R.id.stop_workout);

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
            sport.setCompoundDrawablesWithIntrinsicBounds(
                    ColorUtilities.colorIcon(getContext(), sport_e.getIconId(), sport.getTextColors().getDefaultColor()), null, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Preferences.Session.logout(getContext());
            getContext().startActivity(new Intent(getContext(), BootAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

        workout_distance.setText(workoutMeasure.get(1).toString(true));
        workout_energy.setText(workoutMeasure.get(2).toString(true));
    }

    private void renderingMap(PolylineOptions polylineOptions){
        if(myService != null && getFragmentManager()!=null){
            Fragment fragment = getFragmentManager().findFragmentById(R.id.map);
            if(fragment instanceof MapFragment)
                ((MapFragment)fragment).addPolyLine(polylineOptions);
        }
    }

    @SuppressLint("RestrictedApi")
    private void startState(){
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().findViewById(R.id.nav_bar).setVisibility(View.GONE);
        start.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void restartState(){
        restart.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void pauseState(){
        pause.setVisibility(View.GONE);
        restart.setVisibility(View.VISIBLE);
        stop.setVisibility(View.VISIBLE);
    }

    private void setListener(){
        workout_duration.setOnChronometerTickListener(chronometer -> {
            long time = (SystemClock.elapsedRealtime() - chronometer.getBase()) /1000;
            String timer = Measure.Utilities.format(time);
            Log.d(TAG, "Timer = "+ timer);
        });

        start.setOnClickListener(v ->{
            DelayedStartWorkoutDialog.create(getContext(), () -> {

                startState();

                workout_duration.start();

                try {
                    registerBroadcastReceiver();
                    bindService();

                    getContext().startService(new Intent(getContext(), WorkoutService.class).setAction(ActionReceiver.START_ACTION)
                            .putExtra(KeysIntent.WEIGHT_MORE_RECENT, SqlLiteStatisticsDao.SqlLiteWeightDao.create(getContext()).getLast())
                            .putExtra(KeysIntent.SPORT_DEFAULT, SqlLiteSettingsDao.create(getContext()).getSportDefault()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }).show();

        });
        pause.setOnClickListener(v ->{
            pauseState();

            getContext().startService(new Intent(getContext(), WorkoutService.class).setAction(ActionReceiver.PAUSE_ACTION)
                    //.putExtra(KeysIntent.TIMER, time)
                    .putExtra(KeysIntent.FROM_NOTIFICATION, false));
            workout_duration.pause();
        });
        restart.setOnClickListener(v ->{
            restartState();

            workout_duration.restart();
            getContext().startService(new Intent(getContext(), WorkoutService.class)
                    .setAction(ActionReceiver.RESTART_ACTION)
                    //.putExtra(KeysIntent.TIMER, time)
                    .putExtra(KeysIntent.FROM_NOTIFICATION, false));
        });
        stop.setOnClickListener(v ->{
            final Workout workout = myService.getWorkout();
            workout_duration.stop();
            getContext().stopService(new Intent(getContext(), WorkoutService.class).setAction(ActionReceiver.STOP_ACTION));
            onStopWorkoutClickListener.OnStopWorkoutClick(workout);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        unBindService();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        bindService();

        ErrorQueue.getErrors(getContext());
        //TODO: 1/3/2020 ADD SYNC SERVICE ONRESUME
        //SyncServiceHandler.create(getContext()).start();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unRegisterBroadcastReceiver();
        unBindService();
    }

    public interface OnStopWorkoutClickListener{
        void OnStopWorkoutClick(Workout workout);
    }

    /* HANDLER SERVICE */

    public FloatingActionButton getStop() {
        return stop;
    }

    public boolean isInWorkout(){
        return  myService!=null && myService.isRunning();
    }

    private void restoreView(){
        Log.d(TAG, "Restore GUI");
        switch (restore){
            case ActionReceiver.STOP_ACTION:
                stop.callOnClick();
                break;
            case ActionReceiver.RUNNING_WORKOUT: {
                HomeFragment.this.setIndoor(!LocationUtilities.isGpsEnable(getContext()));
                HomeFragment.this.startState();

                workout_duration.setTimeInMillSec(myService.getChronoBase(),myService.getTimeInMillSec());
                if(myService.isPause())
                {
                    HomeFragment.this.pauseState();
                    workout_duration.pause();
                }
                else {
                    HomeFragment.this.restartState();
                    workout_duration.restart();
                    // FIXME : QUANDO L'APPLICAZIONE VIENE CHIUSA E POI RIAPERTA CON LA NOTIFICA : CHRONOMETRO NON SETTATO CORRETTAMENTE
                    // - SE E' ATTIVO SETTATO (5 - 10 SEC IN MENO)
                }

            }
            break;
        }
    }

    private WorkoutService myService;
    private boolean isBound = false;
    private boolean isRegister = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            final WorkoutService.LocalBinder binder = (WorkoutService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;

            // RENDERING VIEW
            if(myService.isRunning()){

                if(restore!=null){
                    restoreView();
                }

                final Workout workout = myService.getWorkout();
                if(workout!=null){
                    workout_distance.setText(workout.getDistance().toString(true));
                    workout_energy.setText(workout.getCalories().toString(true));
                    final String map_route = workout.getMapRoute();
                    if(map_route!=null)
                        renderingMap(Factory.CustomPolylineOptions.create().addAll(CollectionsUtilities.convertStringToListLatLng(map_route)));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            isBound = false;
        }
    };

    private MyReceiverBroadCast broadcastReceiver = new MyReceiverBroadCast();

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActionReceiver.DISTANCE_ENERGY_ACTION);
        intentFilter.addAction(ActionReceiver.DRAWING_MAP_TIMER_ACTION);
        intentFilter.addAction(ActionReceiver.PAUSE_ACTION);
        intentFilter.addAction(ActionReceiver.RESTART_ACTION);
        intentFilter.addAction(ActionReceiver.STOP_ACTION);
        if(!isRegister()){
            getContext().registerReceiver(broadcastReceiver, intentFilter);
            isRegister =true;
        }
    }

    public void unRegisterBroadcastReceiver() {
        if(isRegister()){
            getContext().unregisterReceiver(broadcastReceiver);
            isRegister = false;
        }
    }

    private void bindService(){
        if(!isBound())
            getContext().bindService(new Intent(getContext(), WorkoutService.class), connection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService(){
        if(isBound()){
            getContext().unbindService(connection);
            isBound = false;
        }
    }

    private boolean isBound(){
        return isBound;
    }

    private boolean isRegister(){
        return isRegister;
    }

    public class MyReceiverBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getAction()!=null){
                switch (intent.getAction()){
                    case ActionReceiver.PAUSE_ACTION:
                        workout_duration.pause();
                        Log.d(TAG, "PAUSE = " + workout_duration.getText());
                        pauseState();
                        break;
                    case ActionReceiver.RESTART_ACTION:
                        Log.d(TAG, "RESTART = " + workout_duration.getText());
                        workout_duration.restart();
                        restartState();
                        break;

                    case ActionReceiver.DISTANCE_ENERGY_ACTION: {
                        final String distance = intent.getStringExtra(KeysIntent.DISTANCE);
                        final String energyInKcal  = intent.getStringExtra(KeysIntent.ENERGY) ;
                        Log.d(TAG, "Distance  = " + distance + ", Energy (kcal) = "+energyInKcal);
                        workout_energy.setText(energyInKcal);
                        workout_distance.setText(distance);
                    }
                    break;
                    case ActionReceiver.DRAWING_MAP_TIMER_ACTION:
                        final PolylineOptions route = intent.getParcelableExtra(KeysIntent.ROUTE);
                        Log.e(TAG, "Route polyline receive");
                        HomeFragment.this.renderingMap(route);
                        break;
                }
            }
        }
    }

}
