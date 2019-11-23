package com.run_walk_tracking_gps.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class DistanceService implements SensorEventListener, WorkoutServiceHandler.OnDistanceListener {

    private static final String TAG = DistanceService.class.getName();
    private static final double STEPS_TO_KM = 0.000762;


    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private boolean running = false;
    private boolean firstTime = true;
    private int oldValue = 0;
    private int milestoneStep;

    public DistanceService(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void start(){
        running = true;
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void pause(){
        running = false;
    }

    public void restart(){
        running = true;
        firstTime = true;
    }

    public void stop(){
        running = false;
        oldValue = 0;
        firstTime = true;
        sensorManager.unregisterListener(this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged");
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged");
        if(running){
            if(firstTime) {
                firstTime = false;
                milestoneStep = (int)event.values[0];

                if(oldValue>0) milestoneStep-=oldValue;
            }
            oldValue = (int)event.values[0] - milestoneStep;
        }
    }

    @Override
    public double getDistanceInKm() {
        return oldValue*STEPS_TO_KM;
    }

}
