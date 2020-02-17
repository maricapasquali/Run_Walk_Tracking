package com.run_walk_tracking_gps.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.NotificationWorkout;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.MusicCoach;
import com.run_walk_tracking_gps.model.VoiceCoach;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.builder.WorkoutBuilder;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.utilities.AppUtilities;
import com.run_walk_tracking_gps.utilities.DateHelper;

import androidx.annotation.RequiresApi;

public class WorkoutService extends Service implements MapRouteDraw.OnChangeLocationListener{
    private static final String TAG = WorkoutService.class.getName();

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
            mapRouteDraw.startDrawing(Looper.myLooper());
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
            mapRouteDraw.startDrawing(Looper.myLooper());
        }
        return true;
    }

    private NotificationWorkout notificationWorkout;
    private Workout workout;
    private boolean isRunning = false;
    private boolean isInPause = false;

    /* VOCAL COACH*/
    private VoiceCoach voiceCoach;

    /* MUSIC COACH */
    private MusicCoach musicCoach;

    /* MOVIMENT */

    /* CALORIES AND DISTANCE */
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
            Log.d(TAG, "onSensorChanged");
            if (isRunning && !isInPause) {
                //if (firstTime &&(kcal==0 && km==0)) {
                if (firstTime) {
                    Log.d(TAG, "Reset milestoneStep");
                    firstTime = false;
                    milestoneStep = (int) event.values[0];

                    if (oldValue > 0) milestoneStep -= oldValue;
                }
                oldValue = (int) event.values[0] - milestoneStep;

                // distance and energy
                double km = oldValue * STEPS_TO_KM;
                double kcal = sport.getConsumedEnergy(weight, km);
                // Log.e(TAG, "Distance = " +km + " Km, Energy = " +kcal + " kcal");
                workout.getDistance().setValue(true, km);
                workout.getCalories().setValue(true, kcal);


                sendBroadcast(new Intent(ActionReceiver.DISTANCE_ENERGY_ACTION)
                        .putExtra(KeysIntent.DISTANCE, workout.getDistance().toString(true))
                        .putExtra(KeysIntent.ENERGY, workout.getCalories().toString(true)));

                notificationWorkout.updateDistance(workout.getDistance().toString(true));
                notificationWorkout.updateEnergy(workout.getCalories().toString(true));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged");
        }
    };

    /* MAP */
    private MapRouteDraw mapRouteDraw;

    @Override
    public void addPolyLineOnMap(PolylineOptions options) {
        if(binder!=null)
            sendBroadcast(new Intent(ActionReceiver.DRAWING_MAP_TIMER_ACTION)); //.putExtra(KeysIntent.ROUTE, options));
    }

    private boolean isIndoor = false;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        Context context = getApplicationContext();
        notificationWorkout = NotificationWorkout.create(context);
        workout = WorkoutBuilder.create(context).setDate(DateHelper.create(context).getCalendar().getTime()).build();

        /*VOICE COACH*/
        voiceCoach = VoiceCoach.create(context);
        voiceCoach.start();

        /* MUSIC COACH */
        musicCoach = MusicCoach.create(context);

        /* MOVIMENT */
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        /* MAP */
        this.mapRouteDraw = MapRouteDraw.create(context, this);
        isIndoor = (mapRouteDraw==null);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(()->{
            Looper.prepare();
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

                    case ActionReceiver.STOP_ACTION:
                        stop();
                        break;

                    case ActionReceiver.VOICE:
                        voiceCoach();
                        break;
                }
            }
            Looper.loop();
        }).start();

        return START_REDELIVER_INTENT;
    }


    private void start(Intent intent){
        Log.d(TAG, "START REQUEST");
        weight = intent.getDoubleExtra(KeysIntent.WEIGHT_MORE_RECENT, 0d);
        sport = Sport.valueOf(intent.getStringExtra(KeysIntent.SPORT_DEFAULT));

        final Workout w = intent.getParcelableExtra(KeysIntent.WORKOUT);
        if(w!=null) workout = w.clone();

        musicCoach.start();

        startForeground(NotificationWorkout.NOTIFICATION_ID, notificationWorkout.build());
        notificationWorkout.startClicked();

        sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        if(!isIndoor) this.mapRouteDraw.startDrawing(Looper.myLooper());

    }
    private void pause(Intent intent){
        Log.d(TAG, "PAUSE REQUEST");
        isInPause = true;

        notificationWorkout.pauseClicked();

        sensorManager.unregisterListener(sensorEventListener);
        if(!isIndoor) this.mapRouteDraw.pause();
        if(intent.getBooleanExtra(KeysIntent.FROM_NOTIFICATION,true))
        {
            Log.d(TAG, "SEND REQUEST");
            sendBroadcast(new Intent(ActionReceiver.PAUSE_ACTION).putExtra(KeysIntent.TIMER, getTimeInMillSec()));
        }
    }
    private void restart(Intent intent){
        Log.d(TAG, "RESTART REQUEST");
        isInPause = false;
        notificationWorkout.restartClicked();


        sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        if(!isIndoor) this.mapRouteDraw.restart(Looper.myLooper());
        if(intent.getBooleanExtra(KeysIntent.FROM_NOTIFICATION,true))
        {
            Log.d(TAG, "SEND REQUEST");
            sendBroadcast(new Intent(ActionReceiver.RESTART_ACTION).putExtra(KeysIntent.TIMER, getTimeInMillSec()));
        }

    }
    private void stop(){
        Log.d(TAG, "STOP REQUEST");
        isRunning = false;

        sensorManager.unregisterListener(sensorEventListener);
        if(!isIndoor) this.mapRouteDraw.stopDrawing();

        musicCoach.stop();

        voiceCoach.stop();
        notificationWorkout.stopClicked();
    }

    public void voiceCoach(){
        String timeFormat = notificationWorkout.getTimeStamp();
        Log.d(TAG, timeFormat+ ", distanza = " + workout.getDistance().toString(true)+ ", calorie = " + workout.getCalories().toString(true));
        voiceCoach.speakIfIsActive(timeFormat, workout.getDistance().toString(true), workout.getCalories().toString(true));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    public Workout getWorkout(){
        workout.setMiddleSpeed();
        workout.getDuration().setValue(true, (double)notificationWorkout.getTimeInMillSec()/1000);
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

}
