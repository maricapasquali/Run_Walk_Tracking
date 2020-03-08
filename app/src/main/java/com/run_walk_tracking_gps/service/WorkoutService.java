package com.run_walk_tracking_gps.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.NotificationWorkout;
import com.run_walk_tracking_gps.model.MusicCoach;
import com.run_walk_tracking_gps.model.VoiceCoach;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.builder.WorkoutBuilder;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.receiver.ActionReceiver;

import java.util.ArrayList;

public class WorkoutService extends Service //implements MapRouteDraw.OnChangeLocationListener
{

    private static final String TAG = WorkoutService.class.getName();

    private static final int REQUEST_RESTART_SERVICE = 311;

    private IBinder binder = null;

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

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind : INTENT = " + intent);
        if(binder==null) binder = new LocalBinder();
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind : INTENT = " + intent);
        if(binder==null) binder = new LocalBinder();
        if(isRunning() &&  !isPause() && !isIndoor ){
            mapRouteDraw.stopDrawing();
            mapRouteDraw.setBackground(false);
            mapRouteDraw.startDrawing(handler);
        }
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind : INTENT = " + intent);
        binder = null;
        if(isRunning() && !isPause() && !isIndoor){
            mapRouteDraw.stopDrawing();
            mapRouteDraw.setBackground(true);
            mapRouteDraw.startDrawing(handler);
        }
        return true;
    }

    private NotificationWorkout notificationWorkout;
    private Workout workout;
    private boolean isRunning = false;
    private boolean isInPause = false;

    private Context context;
    private Thread workoutThread;
    private Handler handler;

    private Handler.Callback handlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Thread = [ id = " +
                    Thread.currentThread().getId() + ", name = "+
                    Thread.currentThread().getName() +" ]");

            if(msg!=null && msg.getData()!=null){
                Bundle bundle = msg.getData();
                Log.d(TAG, bundle.toString());

                String distance = bundle.getString(KeysIntent.DISTANCE);
                String energy = bundle.getString(KeysIntent.ENERGY);
                if(distance!=null && energy!=null){
                    notificationWorkout.updateDistanceAndEnergy(distance, energy);
                    Log.d(TAG, "Update Notification");
                    if(binder!=null) {
                        Log.d(TAG, "Send to activity");
                        sendBroadcast(new Intent(ActionReceiver.DISTANCE_ENERGY_ACTION)
                                .putExtra(KeysIntent.DISTANCE, distance)
                                .putExtra(KeysIntent.ENERGY, energy));
                    }
                }
                ArrayList<Location> locations = bundle.getParcelableArrayList(KeysIntent.ROUTE);
                if(locations!=null){
                    Preferences.WorkoutInExecution.MapLocation.addAll(context, locations);
                    Log.d(TAG, "Update Preferences MAP ROUTE");
                    if(binder!=null){
                        Log.d(TAG, "Send to activity");
                        sendBroadcast(new Intent(ActionReceiver.DRAWING_MAP_ACTION));
                    }
                }

            }
            return false;
        }
    };

    /* MOVIMENT */

    /* CALORIES AND DISTANCE */

    /* ENERGY */
    private Sport sport;
    private double weight;

    /* DISTANCE */
    private static final double STEPS_TO_KM = 0.000762;
    private static final int LATENCY = 1000000;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean firstTime = true;
    private int milestoneStep;
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "onSensorChanged");
            //boolean screenOn = powerManager.isInteractive();if(!screenOn) wakeLock.acquire();
            if (isRunning && !isInPause) {
               // boolean screenOn = powerManager.isInteractive();
               // if(!screenOn) wakeLock.acquire();
                if (firstTime) {
                    if(Preferences.WorkoutInExecution.inExecution(context)){
                        milestoneStep = Preferences.WorkoutInExecution.getMileStoneStep(context);
                        Log.d(TAG, "MilestoneStep in Preferences = " + milestoneStep);
                    }
                    else{
                        milestoneStep = (int) event.values[0];
                        Preferences.WorkoutInExecution.setMileStoneStep(context, milestoneStep);
                        Log.d(TAG, "Reset milestoneStep : " + milestoneStep);
                    }
                    firstTime = false;
                }

                int steps = (int) event.values[0] - milestoneStep;
                // distance and energy
                double km = steps * STEPS_TO_KM;
                double kcal = sport.getConsumedEnergy(weight, km);
                workout.getDistance().setValue(true, km);
                workout.getCalories().setValue(true, kcal);


                String distance = workout.getDistance().toString(true);
                String energy = workout.getCalories().toString(true);

                //if(screenOn) {
                if(powerManager.isInteractive()) {
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString(KeysIntent.DISTANCE, distance);
                    bundle.putString(KeysIntent.ENERGY, energy);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
                else{
                    Log.d(TAG, "Steps = "+ steps + ", Distance = " + distance +" , Calories = " + energy);
                   // wakeLock.release();
                }

                /*if(screenOn) {
                    Log.d(TAG, "Update Notification");
                    notificationWorkout.updateDistanceAndEnergy(distance, energy);
                    if(binder!=null)
                    {
                        Log.d(TAG, "Send to activity");
                        sendBroadcast(new Intent(ActionReceiver.DISTANCE_ENERGY_ACTION)
                                .putExtra(KeysIntent.DISTANCE, distance)
                                .putExtra(KeysIntent.ENERGY, energy));
                    }
                }
                else
                    wakeLock.release();*/

            }
            //if(wakeLock.isHeld()) wakeLock.release();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged : " + sensor + ", accuracy = " + accuracy);
        }
    };

    /* MAP */
    private MapRouteDraw mapRouteDraw;
    private boolean isIndoor = false;
    /*@Override
    public void addPolyLineOnMap() {
        if(binder!=null) {
            Log.d(TAG, "MapRoute : Send to activity ");
            sendBroadcast(new Intent(ActionReceiver.DRAWING_MAP_ACTION));
            //.putExtra(KeysIntent.ROUTE, options));
        }
    }*/


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        this.context = getApplicationContext();
        this.notificationWorkout = NotificationWorkout.create(context);
        this.workout = WorkoutBuilder.create(context)
                        .setDate(Preferences.WorkoutInExecution.getDate(context)).build();
        /* MOVIMENT */
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        this.powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        //this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        Log.d(TAG, "StepCounter sensor is wake up = " + this.stepCounterSensor.isWakeUpSensor());

        /* MAP */
        this.mapRouteDraw = MapRouteDraw.create(context);
        this.isIndoor = (mapRouteDraw==null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        workoutThread = new Thread(()->{
            Looper.prepare();
            Log.d(TAG, "Thread Workout  = [ id = " +
                    Thread.currentThread().getId() + ", name = "+
                    Thread.currentThread().getName() +" ]");

            if(intent!=null && intent.getAction()!=null){
                isRunning = true;
                switch (intent.getAction()){
                    case ActionReceiver.START_ACTION:
                        start(intent);
                        break;
                    case ActionReceiver.PAUSE_ACTION:
                        pause(intent);
                        break;
                    case ActionReceiver.RESTART_ACTION:
                        restart(intent);
                        break;
                    case ActionReceiver.VOICE:
                        voiceCoach();
                        break;
                }
            }
            Looper.loop();
        });
        workoutThread.start();

        return START_NOT_STICKY;
        //return START_REDELIVER_INTENT;
    }

    private void start(Intent intent){
        Log.d(TAG, "START REQUEST");

        handler = new Handler(Looper.myLooper(), handlerCallback);
        weight = intent.getDoubleExtra(KeysIntent.WEIGHT_MORE_RECENT, 0d);
        sport = Sport.valueOf(intent.getStringExtra(KeysIntent.SPORT_DEFAULT));
        workout.setSport(sport);
        Preferences.WorkoutInExecution.setDate(context, workout.getDate());

        MusicCoach.getInstance(context).start();
        VoiceCoach.getInstance(context).start();

        startForeground(NotificationWorkout.NOTIFICATION_ID, notificationWorkout.build());
        notificationWorkout.startClicked(Preferences.WorkoutInExecution.getDuration(context));

        registerUnLockScreenBroadcastReceiver();
        sensorManager.registerListener(sensorEventListener, stepCounterSensor,
                SensorManager.SENSOR_DELAY_UI, LATENCY, handler);
        if(!isIndoor) this.mapRouteDraw.startDrawing(handler);
    }

    private void pause(Intent intent){
        Log.d(TAG, "PAUSE REQUEST");
        isInPause = true;

        notificationWorkout.pauseClicked();
        VoiceCoach.getInstance(context).stop();

        sensorManager.unregisterListener(sensorEventListener);
        if(!isIndoor) this.mapRouteDraw.pause();
        if(intent.getBooleanExtra(KeysIntent.FROM_NOTIFICATION,true))
        {
            Log.d(TAG, "SEND REQUEST");
            sendBroadcast(new Intent(ActionReceiver.PAUSE_ACTION)
                    .putExtra(KeysIntent.TIMER, getTimeInMillSec()));
        }
    }

    private void restart(Intent intent){
        Log.d(TAG, "RESTART REQUEST");
        isInPause = false;
        notificationWorkout.restartClicked();
        VoiceCoach.getInstance(context).start();

        sensorManager.registerListener(sensorEventListener, stepCounterSensor,
                SensorManager.SENSOR_DELAY_UI, LATENCY, handler);
        if(!isIndoor) this.mapRouteDraw.restart(handler);
        if(intent.getBooleanExtra(KeysIntent.FROM_NOTIFICATION,true))
        {
            Log.d(TAG, "SEND REQUEST");
            sendBroadcast(new Intent(ActionReceiver.RESTART_ACTION).putExtra(KeysIntent.TIMER, getTimeInMillSec()));
        }
    }

    private void stop(){
        Log.d(TAG, "STOP REQUEST");
        isRunning = false;
        unRegisterUnLockScreenBroadcastReceiver();
        MusicCoach.getInstance(context).stop();
        MusicCoach.release();
        VoiceCoach.release();

        notificationWorkout.stopClicked();

        workoutThread.interrupt();
    }

    public void voiceCoach(){
        String timeFormat = notificationWorkout.getTimeStamp();
        Preferences.WorkoutInExecution.setDuration(context, SystemClock.elapsedRealtime() - getChronoBase());
        Log.d(TAG, timeFormat+ ", distanza = " +
                workout.getDistance().toString(true)+
                ", calorie = " + workout.getCalories().toString(true));
        VoiceCoach.getInstance(context).speakIfIsActive(timeFormat,
                                                       workout.getDistance().toString(true),
                                                       workout.getCalories().toString(true));
    }

    /*@Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "ON TASK REMOVED");
        Preferences.WorkoutInExecution.setDuration(context, SystemClock.elapsedRealtime() - getChronoBase());

        Intent restartServiceTask = new Intent(context, ReceiverRestartService.class);restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                REQUEST_RESTART_SERVICE, restartServiceTask, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }*/

    @Override
    public void onDestroy() {
        stop();
        Log.d(TAG, "ON DESTROY");
        Preferences.WorkoutInExecution.setDuration(context, SystemClock.elapsedRealtime() - getChronoBase());
        super.onDestroy();
    }

    public Workout getWorkout(){
        workout.getDuration().setValue(true, (double)getTimeInMillSec()/1000);
        workout.setMiddleSpeed();
        return workout;
    }

    public boolean isPause() {
        return isInPause;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getChronoBase() {
        return notificationWorkout.getChronoBase();
    }

    public long getTimeInMillSec() {
        return notificationWorkout.getTimeInMillSec();
    }

    private class ScreenReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getAction()!=null){
                switch (intent.getAction()){
                    case Intent.ACTION_SCREEN_ON:
                        Log.d(TAG, "SCREEN ON");
                        String distance = workout.getDistance().toString(true);
                        String energy = workout.getCalories().toString(true);
                        notificationWorkout.updateDistanceAndEnergy(distance, energy);
                        break;
                }
            }
        }
    }
    private ScreenReceiver screenReceiver = new ScreenReceiver();
    private boolean isRegister = false;
    private boolean isRegister(){
        return isRegister;
    }
    private void registerUnLockScreenBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        if(!isRegister()){
            context.registerReceiver(screenReceiver, intentFilter, null, this.handler);
            isRegister =true;
        }
    }
    private void unRegisterUnLockScreenBroadcastReceiver() {
        if(isRegister()){
            context.unregisterReceiver(screenReceiver);
            isRegister = false;
        }
    }

}
