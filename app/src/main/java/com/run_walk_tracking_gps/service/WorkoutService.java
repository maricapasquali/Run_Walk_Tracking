package com.run_walk_tracking_gps.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.builder.WorkoutBuilder;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.receiver.ReceiverWorkoutElement;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.run_walk_tracking_gps.service.NotificationWorkout.NOTIFICATION_ID;

public class WorkoutService extends Service {

    private static final String TAG = WorkoutService.class.getName();

    private Context context;
    private NotificationWorkout notificationWorkout;
    private BroadcastReceiver receiver;

    private static boolean isWorkoutServiceRunning = false;
    private static boolean isWorkoutServicePause = false;
    @SuppressLint("StaticFieldLeak")
    private static Workout workout;

    /* MAP */
    private MapRouteService mapRouteService;

    /* ENERGY */

    // Deve essere statici
    private static Sport sport;
    private static double weight;

    /* DISTANCE */
    private static final double STEPS_TO_KM = 0.000762;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private boolean firstTime = true;
    private int oldValue = 0;
    private int milestoneStep;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            //Log.d(TAG, "onSensorChanged");
            if(isRunning()&& !isPause()){
                if(firstTime) {
                    Log.d(TAG, "Reset milestoneStep");
                    firstTime = false;
                    milestoneStep = (int)event.values[0];

                    if(oldValue>0) milestoneStep-=oldValue;
                }
                oldValue = (int)event.values[0] - milestoneStep;

                // distance and energy
                final double km = oldValue * STEPS_TO_KM;
                final double kcal = sport.getConsumedEnergy(weight, km);

                workout.getDistance().setValue(true, km);
                workout.getCalories().setValue(true, kcal);
                context.sendBroadcast(new Intent(ActionReceiver.DISTANCE_ENERGY_ACTION)
                                            .putExtra(KeysIntent.DISTANCE, km)
                                            .putExtra(KeysIntent.ENERGY, kcal));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged");
        }
    };

    /* TIMER */
    private static final long TIMER_INTERVAL = 1000;

    // Deve essere statico
    private static int time = 0;

    private Timer mTimer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            //Log.d(TAG, "TimerTask");
            if(isRunning() && !isPause()){
                ++time;
                workout.getDuration().setValue(true, (double) time);
                sendBroadcast(new Intent(ActionReceiver.TIMER_ACTION).putExtra(KeysIntent.SECONDS, time));
            }
        }
    };




    @RequiresApi(api = Build.VERSION_CODES.O)
    public WorkoutService(){
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public WorkoutService(final Context context, double weightInKg, OnReceiverListener broadcastReceiver,
                          final MapRouteService.OnChangeLocationListener onChangeLocationListener){
        this();
        this.context = context;

        workout = WorkoutBuilder.create(context).setDate(Calendar.getInstance().getTime()).build();

        this.notificationWorkout = NotificationWorkout.create(context);
        this.receiver = new ReceiverWorkoutElement(broadcastReceiver, notificationWorkout);

        // distance
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // energy
        WorkoutService.weight = weightInKg;
        WorkoutService.sport = DefaultPreferencesUser.getSportDefault(context);

        this.mapRouteService = new MapRouteService(context, onChangeLocationListener);
    }

    public Workout getWorkout() {
        workout.setMapRoute(mapRouteService.getListCoordinates());
        return workout;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static WorkoutService createService(final Context context, final double weight, final OnReceiverListener broadcastReceiver, final MapRouteService.OnChangeLocationListener onChangeLocationListener){
        return new WorkoutService(context, weight, broadcastReceiver, onChangeLocationListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        this.context = getApplicationContext();
        this.mTimer = new Timer();
        this.notificationWorkout = NotificationWorkout.create(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NotificationWorkout.NOTIFICATION_ID, notificationWorkout.build());
        // Timer
        mTimer.scheduleAtFixedRate(timerTask, 0, TIMER_INTERVAL);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* timer */
        this.mTimer.cancel();
        this.mTimer.purge();
        this.mTimer = null;

        workout = null;
    }

    public void start(){
        Log.d(TAG, "start");
        isWorkoutServiceRunning = true;

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActionReceiver.TIMER_ACTION);
        intentFilter.addAction(ActionReceiver.DISTANCE_ENERGY_ACTION);
        context.registerReceiver(receiver, intentFilter);
        /* distance*/
        sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        /* timer */
        WorkoutService.time = 0;
        /* map */
        mapRouteService.start();

        context.startService(new Intent(context, this.getClass()));
    }

    public void pause(){
        Log.d(TAG, "pause");
        isWorkoutServicePause = true;
        /* map */
        mapRouteService.pause();
        notificationWorkout.pauseClicked();
    }

    public void restart(){
        Log.d(TAG, "restart");
        isWorkoutServicePause = false;
        /* distance*/
        firstTime = true;
        /* map */
        mapRouteService.start();
        notificationWorkout.restartClicked();
    }

    public void stop(){
        Log.d(TAG, "stop");
        isWorkoutServiceRunning = false;
        context.unregisterReceiver(receiver);

        /* distance*/
        oldValue = 0;
        firstTime = true;
        sensorManager.unregisterListener(sensorEventListener);
        /* map */
        mapRouteService.stop();


        notificationWorkout.stopClicked();
        context.stopService(new Intent(context, this.getClass()));
    }

    public void lock() {
        Log.d(TAG, "lock_screen");
        notificationWorkout.lockClicked();
    }

    public void unlock() {
        Log.d(TAG, "unlock_screen");
        notificationWorkout.unlockClicked();
    }

    public boolean isRunning(){
        return isWorkoutServiceRunning;
    }

    public boolean isPause() {
        return isWorkoutServicePause;
    }

    public interface OnReceiverListener{
        void onReceiverDuration(int sec);
        void onReceiverDistance(double distance);
        void onReceiverEnergy( double energy);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
