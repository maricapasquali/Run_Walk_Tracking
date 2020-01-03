package com.run_walk_tracking_gps.gui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.db.dao.SqlLiteSettingsDao;
import com.run_walk_tracking_gps.db.tables.SettingsDescriptor;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.receiver.ReceiverNotificationButtonHandler;
import com.run_walk_tracking_gps.utilities.NotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationWorkout {

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationWorkout(Context c){
        this.context = c;

        this.notificationHelper = new NotificationHelper(context);

        this.remoteViewSmall  = new RemoteViews(context.getPackageName(), R.layout.custom_service_workout_running_small);
        this.remoteViewBig = new RemoteViews(context.getPackageName(), R.layout.custom_service_workout_running_big);

        final Intent notificationIntent = new Intent(context, SplashScreenActivity.class).setAction(ActionReceiver.RUNNING_WORKOUT);
        final PendingIntent onClickNotificationIntent = PendingIntent.getActivity(context, REQUEST_CODE_RESUME, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            final JSONObject unitMeasureDefault = SqlLiteSettingsDao.create(c).getUnitMeasureDefault();
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
        setListener();
    }

    private void setListener(){
        Intent pauseClick = new Intent(context, ReceiverNotificationButtonHandler.class).setAction(ActionReceiver.PAUSE_ACTION);
        PendingIntent pausePendingClick = PendingIntent.getBroadcast(context, REQUEST_CODE_PAUSE, pauseClick, 0);

        Intent restartClick = new Intent(context, ReceiverNotificationButtonHandler.class).setAction(ActionReceiver.RESTART_ACTION);
        PendingIntent restartPendingClick = PendingIntent.getBroadcast(context, REQUEST_CODE_RESTART, restartClick, 0);

        Intent stopClick = new Intent(context, SplashScreenActivity.class).setAction(ActionReceiver.STOP_ACTION);
        PendingIntent stopPendingClick = PendingIntent.getActivity(context, REQUEST_CODE_STOP, stopClick, 0);

        remoteViewBig.setOnClickPendingIntent(R.id.pause_workout, pausePendingClick);
        remoteViewBig.setOnClickPendingIntent(R.id.stop_workout, stopPendingClick);
        remoteViewBig.setOnClickPendingIntent(R.id.restart_workout, restartPendingClick);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationWorkout create(Context context){
        return new NotificationWorkout(context);
    }

    public Notification build() {
        return notificationBuilder.build();
    }

    public void updateDuration(String sec) {
        // aggiorno tramite builder il contenuto della notifica
        remoteViewBig.setTextViewText(R.id.time_workout, sec);
        remoteViewSmall.setTextViewText(R.id.time_workout, sec);

        // invio la notifica aggiornata, per indicare che è sempre la stessa basta usere lo stesso ID di notifica.
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void updateDistance(String distanceInKm) {
        // aggiorno tramite builder il contenuto della notifica
        remoteViewBig.setTextViewText(R.id.distance_workout, distanceInKm);
        remoteViewSmall.setTextViewText(R.id.distance_workout, distanceInKm);

        // invio la notifica aggiornata, per indicare che è sempre la stessa basta usere lo stesso ID di notifica.
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void updateEnergy(String energyInKcal) {
        // aggiorno tramite builder il contenuto della notifica
        remoteViewBig.setTextViewText(R.id.calories_workout, energyInKcal);
        remoteViewSmall.setTextViewText(R.id.calories_workout, energyInKcal);

        // invio la notifica aggiornata, per indicare che è sempre la stessa basta usere lo stesso ID di notifica.
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void pauseClicked() {
        setVisibleButton(false);
    }

    public void restartClicked() {
        setVisibleButton(true);
    }

    public void stopClicked() {
        notificationHelper.getNotificationManager(context).cancel(NOTIFICATION_ID);
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    public void lockClicked() {
        remoteViewBig.setViewVisibility(R.id.restart_workout, View.GONE);
        remoteViewBig.setViewVisibility(R.id.stop_workout, View.GONE );
        remoteViewBig.setViewVisibility(R.id.pause_workout, View.GONE);
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void unlockClicked() {
        setVisibleButton(true);
    }

    private void setVisibleButton(final boolean isPause){
        remoteViewBig.setViewVisibility(R.id.restart_workout, isPause ?  View.GONE: View.VISIBLE);
        remoteViewBig.setViewVisibility(R.id.stop_workout, isPause ?  View.GONE: View.VISIBLE );
        remoteViewBig.setViewVisibility(R.id.pause_workout, isPause ? View.VISIBLE : View.GONE);
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
