package com.run_walk_tracking_gps.gui;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.RemoteViews;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.db.tables.SettingsDescriptor;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.service.WorkoutService;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationWorkout {

    private static final String TAG = NotificationWorkout.class.getName();

    public static final int NOTIFICATION_ID = 1;
    // REQUEST
    private static final int REQUEST_CODE_RESUME = 5;
    private static final int REQUEST_CODE_RESTART = 2;
    private static final int REQUEST_CODE_PAUSE = 3;
    private static final int REQUEST_CODE_STOP = 4;


    private Context context;

    private NotificationHelper notificationHelper;
    private NotificationCompat.Builder notificationBuilder;

    private RemoteViews remoteViewSmall;
    private RemoteViews remoteViewBig;

    private Chronometer chronometer;
    private long timeWhenPaused = 0;

    private NotificationWorkout(Context c){
        this.context = c;

        this.notificationHelper = new NotificationHelper(context);

        this.remoteViewSmall  = new RemoteViews(context.getPackageName(), R.layout.custom_service_workout_running_small);
        this.remoteViewBig = new RemoteViews(context.getPackageName(), R.layout.custom_service_workout_running_big);

        final Intent notificationIntent = new Intent(context, ApplicationActivity.class).setAction(ActionReceiver.RUNNING_WORKOUT);
        final PendingIntent onClickNotificationIntent = PendingIntent.
                getActivity(context, REQUEST_CODE_RESUME, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            final JSONObject unitMeasureDefault = DaoFactory.getInstance(c).getSettingDao().getUnitMeasureDefault();
            final Measure.Unit distance = Measure.Unit.valueOf(unitMeasureDefault.getString(SettingsDescriptor.UnitMeasureDefault.DISTANCE));
            final Measure.Unit energy = Measure.Unit.valueOf(unitMeasureDefault.getString(SettingsDescriptor.UnitMeasureDefault.ENERGY));

            final String defaultDistance = Measure.Utilities.formatMeasure(0d, distance);
            final String defaultEnergy = Measure.Utilities.formatMeasure(0d, energy);
            remoteViewBig.setTextViewText(R.id.distance_workout, defaultDistance);
            remoteViewSmall.setTextViewText(R.id.distance_workout, defaultDistance);

            remoteViewBig.setTextViewText(R.id.calories_workout, defaultEnergy);
            remoteViewSmall.setTextViewText(R.id.calories_workout, defaultEnergy);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.notificationBuilder = this.notificationHelper.getNotificationBuilder(context, NotificationHelper.CHANNEL_1)
                                                          .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                                          .setCustomContentView(remoteViewSmall)
                                                          .setCustomBigContentView(remoteViewBig)
                                                          .setSmallIcon(R.drawable.ic_runtracking)
                                                          .setContentIntent(onClickNotificationIntent)
                                                          .setAutoCancel(true)
                                                          .setColorized(true)
                                                          .setColor(context.getColor(R.color.colorPrimary))
                                                          .setOnlyAlertOnce(true);
                                                          //.setWhen(SystemClock.elapsedRealtime());
        setListener();

    }

    private void setListener(){

        Intent pauseClick = new Intent(context, WorkoutService.class).setAction(ActionReceiver.PAUSE_ACTION);
        PendingIntent pausePendingClick = PendingIntent.getService(context, REQUEST_CODE_PAUSE, pauseClick, 0);

        Intent restartClick = new Intent(context, WorkoutService.class).setAction(ActionReceiver.RESTART_ACTION);
        PendingIntent restartPendingClick = PendingIntent.getService(context, REQUEST_CODE_RESTART, restartClick, 0);

        Intent stopClick = new Intent(context, SplashScreenActivity.class).setAction(ActionReceiver.STOP_ACTION);
        PendingIntent stopPendingClick = PendingIntent.getActivity(context, REQUEST_CODE_STOP, stopClick, 0);

        remoteViewBig.setOnClickPendingIntent(R.id.pause_workout, pausePendingClick);
        remoteViewBig.setOnClickPendingIntent(R.id.stop_workout, stopPendingClick);
        remoteViewBig.setOnClickPendingIntent(R.id.restart_workout, restartPendingClick);
    }

    public static NotificationWorkout create(Context context){
        return new NotificationWorkout(context);
    }

    public Notification build() {
        return notificationBuilder.build();
    }

    public void updateDistanceAndEnergy(String distanceInKm, String energyInKcal) {
        // aggiorno tramite builder il contenuto della notifica
        remoteViewBig.setTextViewText(R.id.distance_workout, distanceInKm);
        remoteViewSmall.setTextViewText(R.id.distance_workout, distanceInKm);
        remoteViewBig.setTextViewText(R.id.calories_workout, energyInKcal);
        remoteViewSmall.setTextViewText(R.id.calories_workout, energyInKcal);
        // invio la notifica aggiornata, per indicare che è sempre la stessa basta usere lo stesso ID di notifica.
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void startClicked(long time) {
        timeWhenPaused = time;
        chronometer = new Chronometer(context);
        chronometer.setFormat(null);
        long elapsed = SystemClock.elapsedRealtime() - timeWhenPaused;
        chronometer.setBase(elapsed);
        start();
    }

    public void pauseClicked() {
        setVisibleButton(false);

        long elapsed = SystemClock.elapsedRealtime();
        timeWhenPaused  = elapsed - chronometer.getBase();
        stop();
    }

    public void restartClicked() {
        setVisibleButton(true);

        long elapsed = SystemClock.elapsedRealtime();
        chronometer.setBase(elapsed - timeWhenPaused);
        start();
    }

    public void stopClicked() {
        notificationHelper.getNotificationManager(context).cancel(NOTIFICATION_ID);
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void setVisibleButton(final boolean isPause){
        remoteViewBig.setViewVisibility(R.id.restart_workout, isPause ?  View.GONE: View.VISIBLE);
        remoteViewBig.setViewVisibility(R.id.stop_workout, isPause ?  View.GONE: View.VISIBLE );
        remoteViewBig.setViewVisibility(R.id.pause_workout, isPause ? View.VISIBLE : View.GONE);
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void start() {
        remoteViewBig.setChronometer(R.id.time_workout, chronometer.getBase(), null, true);
        remoteViewSmall.setChronometer(R.id.time_workout, chronometer.getBase(), null, true);
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void stop() {

        remoteViewBig.setChronometer(R.id.time_workout, chronometer.getBase(), null, false);
        remoteViewSmall.setChronometer(R.id.time_workout, chronometer.getBase(), null, false);
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public String getTimeStamp(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup localView = (ViewGroup)inflater.inflate(remoteViewBig.getLayoutId(), null);
        remoteViewBig.reapply(context.getApplicationContext(), localView);
        return Measure.Utilities.format(((Chronometer)localView.findViewById(R.id.time_workout)).getText().toString());
    }

    public long getTimeInMillSec(){
        return timeWhenPaused;
    }

    public long getChronoBase(){
        return chronometer!=null ? chronometer.getBase() : SystemClock.elapsedRealtime();
    }

/*
    public void lockClicked() {
        remoteViewBig.setViewVisibility(R.id.restart_workout, View.GONE);
        remoteViewBig.setViewVisibility(R.id.stop_workout, View.GONE );
        remoteViewBig.setViewVisibility(R.id.pause_workout, View.GONE);
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void unlockClicked() {
        setVisibleButton(true);
    }
*/

    private class NotificationHelper {

        private static final String CHANNEL_1 = "Channel_1";
        private static final String CHANNEL_2 = "Channel_2";
        private NotificationManager notificationManager;


        private NotificationHelper(Context context) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            createChannels();
        }

        @SuppressLint("WrongConstant")
        private void createChannels() {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1, "Canale Principale", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2, "Canale Secondario", NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("Canale usato per la ricezione delle notifiche generali");
            channel.setLightColor(Color.BLUE);

            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel2);
        }

        private NotificationCompat.Builder getNotificationBuilder(Context context, String channelID) {
            return new NotificationCompat.Builder(context, channelID);
        }

        private NotificationManagerCompat getNotificationManager(Context context) {
            return NotificationManagerCompat.from(context);
        }

    }

}
