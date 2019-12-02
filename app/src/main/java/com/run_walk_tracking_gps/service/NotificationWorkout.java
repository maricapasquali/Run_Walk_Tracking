package com.run_walk_tracking_gps.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.receiver.ActionReceiver;
import com.run_walk_tracking_gps.receiver.ReceiverNotificationButtonHandler;
import com.run_walk_tracking_gps.utilities.NotificationHelper;

public class NotificationWorkout {

    public static final int NOTIFICATION_ID = 1;

    // REQUEST
    private static final int REQUEST_CODE_RESUME = 5;
    private static final int REQUEST_CODE_RESTART = 2;
    private static final int REQUEST_CODE_PAUSE = 3;
    private static final int REQUEST_CODE_STOP = 4;

    private Context context;
    private Service service;

    private NotificationHelper notificationHelper;
    private NotificationCompat.Builder notificationBuilder;

    private RemoteViews remoteViewSmall;
    private RemoteViews remoteViewBig;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationWorkout(Context c, Service service){
        this.context = c;
        this.service = service;
        this.notificationHelper = new NotificationHelper(context);

        this.remoteViewSmall  = new RemoteViews(context.getPackageName(), R.layout.custom_service_workout_running_small);
        this.remoteViewBig = new RemoteViews(context.getPackageName(), R.layout.custom_service_workout_running_big);


        Intent notificationIntent = new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent onClickNotificationIntent = PendingIntent.getActivity(context, REQUEST_CODE_RESUME, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


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

        Intent stopClick = new Intent(context, ReceiverNotificationButtonHandler.class).setAction(ActionReceiver.STOP_ACTION);
        PendingIntent stopPendingClick = PendingIntent.getBroadcast(context, REQUEST_CODE_STOP, stopClick, 0);

        remoteViewBig.setOnClickPendingIntent(R.id.pause_workout, pausePendingClick );
        remoteViewBig.setOnClickPendingIntent(R.id.stop_workout, stopPendingClick );
        remoteViewBig.setOnClickPendingIntent(R.id.restart_workout, restartPendingClick );
    }

    public void startForeground() {
        service.startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void update(int sec, double distanceInKm, double energyInKcal) {
        // aggiorno tramite builder il contenuto della notifica
        updateRemoteView(remoteViewBig, sec, distanceInKm, energyInKcal);
        updateRemoteView(remoteViewSmall, sec, distanceInKm, energyInKcal);

        // invio la notifica aggiornata, per indicare che è sempre la stessa basta usere lo stesso ID di notifica.
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void updateRemoteView(RemoteViews view, int sec, double distanceInKm, double energyInKcal){
        view.setTextViewText(R.id.time_workout, Measure.DurationUtilities.format(sec));
        view.setTextViewText(R.id.distance_workout, Measure.create(context, Measure.Type.DISTANCE , distanceInKm).toString(true));
        view.setTextViewText(R.id.calories_workout, Measure.create(context, Measure.Type.ENERGY , energyInKcal).toString(true));
    }

    public void cancel(){
        notificationHelper.getNotificationManager(context).cancel(NOTIFICATION_ID);
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    public void pauseClicked() {
        setVisibleButton(false);
    }

    public void restartClicked() {
        setVisibleButton(true);
    }

    private void setVisibleButton(final boolean isPause){
        remoteViewBig.setViewVisibility(R.id.restart_workout, isPause ?  View.GONE: View.VISIBLE);
        remoteViewBig.setViewVisibility(R.id.stop_workout, isPause ?  View.GONE: View.VISIBLE );
        remoteViewBig.setViewVisibility(R.id.pause_workout, isPause ? View.VISIBLE : View.GONE);
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void stopClicked() {
        cancel();
    }

    public void setInvisibleButton() {
        remoteViewBig.setViewVisibility(R.id.restart_workout, View.GONE);
        remoteViewBig.setViewVisibility(R.id.stop_workout, View.GONE );
        remoteViewBig.setViewVisibility(R.id.pause_workout, View.GONE);
        notificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
