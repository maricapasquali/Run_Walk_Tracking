package com.run_walk_tracking_gps.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Helper, utilize per mascherare la creazione di canali su Android Oreo,
 * ed inoltre utilizza le notifiche compat che sono compatibili sia con Oreo che con vecchie versioni.
 */
public final class NotificationHelper {

    public static final String CHANNEL_1 = "Channel_1";
    public static final String CHANNEL_2 = "Channel_2";
    private NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {

        NotificationChannel channel = new NotificationChannel(CHANNEL_1, "Canale Principale", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel channel2 = new NotificationChannel(CHANNEL_2, "Canale Secondario", NotificationManager.IMPORTANCE_DEFAULT);

        channel.setDescription("Canale usato per la ricezione delle notifiche generali");
        channel.setLightColor(Color.BLUE);

        notificationManager.createNotificationChannel(channel);
        notificationManager.createNotificationChannel(channel2);

    }

    public NotificationCompat.Builder getNotificationBuilder(Context context, String channelID) {
        return new NotificationCompat.Builder(context, channelID);
    }

    public NotificationManagerCompat getNotificationManager(Context context) {
        return NotificationManagerCompat.from(context);
    }

}
