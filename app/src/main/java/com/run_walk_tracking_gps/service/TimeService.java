package com.run_walk_tracking_gps.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;


import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.receiver.ReceiverWorkoutElement;

import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service {

    private static final String TAG = TimeService.class.getName();

    private static final long NOTIFY_INTERVAL = 1000;

    private NotificationWorkout notificationWorkout;

    /**
     * Devono essere statici
     */
    private static boolean isStart = true;
    private static int time = 0;

    private BroadcastReceiver receiver;
    private Context context;
    private Timer mTimer;
    private TimerTask timerTask =   new TimerTask() {
        @Override
        public void run() {
            if(isStart())
                sendBroadcast(new Intent(ActionReceiver.TIMER_ACTION).putExtra(getApplicationContext().getString(R.string.second), ++time));
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    public TimeService(){
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TimeService(final Context context,
                       final WorkoutServiceHandler.OnReceiverListener broadcastReceiver,
                       final WorkoutServiceHandler.OnDistanceListener onDistanceListener,
                       final WorkoutServiceHandler.OnEnergyListener onEnergyListener){
        this();
        this.context = context;

        this.notificationWorkout = new NotificationWorkout(context, this);
        this.receiver = new ReceiverWorkoutElement(broadcastReceiver, onDistanceListener, onEnergyListener, notificationWorkout);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        this.context = getApplicationContext();
        this.mTimer = new Timer();
        this.notificationWorkout = new NotificationWorkout(context, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.notificationWorkout.startForeground();
        // schedule task
        mTimer.scheduleAtFixedRate(timerTask, 0, NOTIFY_INTERVAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mTimer.cancel();
        this.mTimer.purge();
        this.mTimer = null;
    }

    public NotificationWorkout getNotification() {
        return notificationWorkout;
    }

    public void start(){
        context.registerReceiver(receiver, new IntentFilter(ActionReceiver.TIMER_ACTION));
        TimeService.setStart(true, 0);
        context.startService(new Intent(context, this.getClass()));
    }

    public void pause(){
        setStart(false);
        notificationWorkout.pauseClicked();
    }

    public void restart(){
        setStart(true);
        notificationWorkout.restartClicked();
    }

    public void stop(){
        setStart(false);
        context.unregisterReceiver(receiver);
        notificationWorkout.stopClicked();
        context.stopService(new Intent(context, this.getClass()));
    }

    private boolean isStart(){
        return isStart;
    }

    private static void setStart(boolean isStart, int time){
        setStart(isStart);
        TimeService.time = time;
    }

    private static void setStart(boolean isStart){
        TimeService.isStart = isStart;
    }

}