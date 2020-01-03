package com.run_walk_tracking_gps.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.db.dao.SqlLiteSettingsDao;
import com.run_walk_tracking_gps.model.Workout;


import org.json.JSONException;

public class WorkoutServiceHandler implements ServiceConnection {
    private static final String TAG = WorkoutServiceHandler.class.getName();

    private WorkoutServiceHandler handler;
    private Context context;

    private double weight;
    private WorkoutService wService;

    private WorkoutService.OnReceiverListener  onReceiverListener;

    private RestoreViewListener restoreViewListener;

    public WorkoutServiceHandler(Context context, double weight, RestoreViewListener restoreViewListener, WorkoutService.OnReceiverListener  onReceiverListener){
        this.context = context;
        this.weight = weight;
        this.restoreViewListener = restoreViewListener;
        this.onReceiverListener = onReceiverListener;
    }


    public WorkoutServiceHandler create(Context context, double weight, RestoreViewListener restoreViewListener, WorkoutService.OnReceiverListener  onReceiverListener){
        if(handler==null)
            handler = new WorkoutServiceHandler(context, weight, restoreViewListener, onReceiverListener);
        return handler;
    }

    public Workout getWorkout(){
        return wService.getWorkout();
    }

    public void bindService(){
        context.bindService(new Intent(context, WorkoutService.class), this,  Context.BIND_AUTO_CREATE);
    }

    public void unBindService(){
        context.unbindService(this);
    }

    private void startService(){
        try {
            context.startService(new Intent(context, wService.getClass())
                    .putExtra(KeysIntent.WEIGHT_MORE_RECENT, weight)
                    .putExtra(KeysIntent.SPORT_DEFAULT, SqlLiteSettingsDao.create(context).getSportDefault()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void pauseService(){
        wService.pause();
    }

    public void restartService(){
        wService.restart();
    }

    public void lockService(){
        wService.lock();
    }

    public void unLockService(){
        wService.unlock();
    }

    public void stopService(){
        context.unbindService(this);
        context.stopService(new Intent(context, WorkoutService.class));
    }

    public boolean isWorkoutRunning(){
        return wService!=null && wService.isRunning();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        final WorkoutService.LocalBinder binder = (WorkoutService.LocalBinder) service;
        wService = binder.getService();

        wService.setOnReceiverListener(context, onReceiverListener);

        if(restoreViewListener!=null)
            restoreViewListener.restoreView(wService);
        else
            startService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        context.stopService(new Intent(context, WorkoutService.class));
    }

    public interface RestoreViewListener {
        void restoreView(WorkoutService wService);
    }

}
