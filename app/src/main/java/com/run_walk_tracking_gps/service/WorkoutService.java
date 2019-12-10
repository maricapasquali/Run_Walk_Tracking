package com.run_walk_tracking_gps.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.builder.WorkoutBuilder;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.receiver.ReceiverNotificationButtonHandler;
import com.run_walk_tracking_gps.receiver.ReceiverWorkoutElement;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class WorkoutService extends Service {

    private static final String TAG = WorkoutService.class.getName();

    private final IBinder binder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public WorkoutService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WorkoutService.this;
        }
    }

    private boolean isLock = false;
    private boolean isWorkoutServiceRunning = false;
    private boolean isWorkoutServicePause = false;


    private Context context;
    private ReceiverWorkoutElement receiver;
    private ReceiverNotificationButtonHandler receiverPressAction;
    private NotificationWorkout notificationWorkout;
    private Workout workout;

    /* MAP */
    private MapRouteDraw mapRouteDraw;

    /* ENERGY */
    private Sport sport;
    private double weight;

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
               // Log.e(TAG, "Distance = " +km + " Km, Energy = " +kcal + " kcal");
                workout.getDistance().setValue(true, km);
                workout.getCalories().setValue(true, kcal);

                sendBroadcast(new Intent(ActionReceiver.DISTANCE_ENERGY_ACTION)
                                     .putExtra(KeysIntent.DISTANCE, workout.getDistance().toString(true))
                                     .putExtra(KeysIntent.ENERGY, workout.getCalories().toString(true)));

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged");
        }
    };

    /* TIMER */
    private static final long TIMER_INTERVAL = 1000;
    private int time = 0;
    private Timer mTimer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            //Log.d(TAG, "TimerTask");
            if(isRunning() && !isPause()){
                ++time;
               // Log.e(TAG, "Timer = " +time);
                workout.getDuration().setValue(true, (double) time);
                sendBroadcast(new Intent(ActionReceiver.TIMER_ACTION).putExtra(KeysIntent.SECONDS, Measure.Utilities.format(time)));
            }
        }
    };

    public WorkoutService(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setOnReceiverListener(Context context, final OnReceiverListener onReceiverListener){
        if(this.receiver==null){
            this.notificationWorkout = NotificationWorkout.create(context);
            this.receiver = new ReceiverWorkoutElement(notificationWorkout);
        }
        if(this.mapRouteDraw ==null) this.mapRouteDraw = new MapRouteDraw(context);

        this.receiver.setBroadcastReceiver(onReceiverListener);
        this.mapRouteDraw.setOnChangeLocationListener(onReceiverListener.onReceiverMapRoute());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        this.context = getApplicationContext();
        this.workout = WorkoutBuilder.create(context).setDate(Calendar.getInstance().getTime()).build();

        /* notifiation */
        this.notificationWorkout = NotificationWorkout.create(context);
        this.receiver = new ReceiverWorkoutElement(notificationWorkout);
        this.receiverPressAction = new ReceiverNotificationButtonHandler();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActionReceiver.TIMER_ACTION);
        intentFilter.addAction(ActionReceiver.DISTANCE_ENERGY_ACTION);
        context.registerReceiver(receiver, intentFilter);

        final IntentFilter intentFilterPress = new IntentFilter();
        intentFilter.addAction(ActionReceiver.PAUSE_ACTION);
        intentFilter.addAction(ActionReceiver.RESTART_ACTION);
        context.registerReceiver(receiverPressAction, intentFilterPress);

        /* map */
        this.mapRouteDraw = new MapRouteDraw(context);
        /* distance*/
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        /* timer */
        this.mTimer = new Timer();
        this.time = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        if(intent!=null){
            weight = intent.getDoubleExtra(KeysIntent.WEIGHT_MORE_RECENT, 0d);
            sport = Sport.valueOf(intent.getStringExtra(KeysIntent.SPORT_DEFAULT));
        }
        if(isWorkoutServiceRunning) return super.onStartCommand(intent, flags, startId);
        isWorkoutServiceRunning = true;
        /* map */
        mapRouteDraw.start();
        /* notification */
        startForeground(NotificationWorkout.NOTIFICATION_ID, notificationWorkout.build());
        /* timer */
        mTimer.scheduleAtFixedRate(timerTask, 0, TIMER_INTERVAL);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        isWorkoutServiceRunning = false;
        /* timer */
        this.mTimer.cancel();
        this.mTimer.purge();
        this.mTimer = null;
        /* distance*/
        oldValue = 0;
        firstTime = true;
        context.unregisterReceiver(receiver);
        context.unregisterReceiver(receiverPressAction);
        sensorManager.unregisterListener(sensorEventListener);
        /* map */
        mapRouteDraw.stop();
        /* notification */
        notificationWorkout.stopClicked();
    }

    public Workout getWorkout() {
        workout.setMapRoute(mapRouteDraw.getListCoordinates());
        return workout;
    }

    public void pause(){
        Log.d(TAG, "pause");
        isWorkoutServicePause = true;
        /* map */
        mapRouteDraw.pause();
        /* notification */
        notificationWorkout.pauseClicked();
    }

    public void restart(){
        Log.d(TAG, "restart");
        isWorkoutServicePause = false;
        /* distance*/
        firstTime = true;
        /* map */
        mapRouteDraw.start();
        /* notification */
        notificationWorkout.restartClicked();
    }

    public void lock() {
        Log.d(TAG, "lock_screen");
        isLock = true;
        /* notification */
        notificationWorkout.lockClicked();
    }

    public void unlock() {
        Log.d(TAG, "unlock_screen");
        isLock = false;
        /* notification */
        notificationWorkout.unlockClicked();
    }

    public boolean isRunning(){
        return isWorkoutServiceRunning;
    }

    public boolean isLock(){
        return isLock;
    }

    public boolean isPause() {
        return isWorkoutServicePause;
    }

    public interface OnReceiverListener{
        void onReceiverDuration(String sec);
        void onReceiverDistance(String distance);
        void onReceiverEnergy(String energy);
        MapRouteDraw.OnChangeLocationListener onReceiverMapRoute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
