package com.professionalandroid.apps.earthquake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

public class EarthquakeNotificationWorker extends Worker {
    public static final String KEY_LARGEST_EARTHQUAKE_ID = "largest_earthquake_id";
    public static final String KEY_MINIMUM_MAGNITUDE = "minimum_magnitude";

    private static final String NOTIFICATION_CHANNEL = "earthquake";
    private static final int NOTIFICATION_ID = 1;

    public EarthquakeNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String largestEarthquakeId = getInputData().getString(KEY_LARGEST_EARTHQUAKE_ID);

        if (largestEarthquakeId != null) {
            int minimumMagnitude = getInputData().getInt(KEY_MINIMUM_MAGNITUDE, -1);

            Earthquake largestEarthquake =
                    EarthquakeDatabaseAccessor
                            .getInstance(getApplicationContext())
                            .earthquakeDAO()
                            .getEarthquake(largestEarthquakeId)
                            .getValue();

            if (largestEarthquake != null &&
                    minimumMagnitude > -1 &&
                    largestEarthquake.getMagnitude() >= minimumMagnitude) {
                broadcastNotification(largestEarthquake);
            }
        }

        return Result.success();
    }

    private void createNotificationChannel() {
        Context context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.earthquake_channel_name);

            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    name,
                    NotificationManager.IMPORTANCE_HIGH);

            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void broadcastNotification(Earthquake earthquake) {
        createNotificationChannel();

        Context context = getApplicationContext();
        Intent startActivityIntent = new Intent(context, EarthquakeMainActivity.class);

        PendingIntent launchIntent =
                PendingIntent.getActivity(context, 0,
                        startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder earthquakeNotificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL);

        earthquakeNotificationBuilder
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.design_default_color_primary))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(launchIntent)
                .setAutoCancel(true)
                .setShowWhen(true);

        earthquakeNotificationBuilder
                .setWhen(earthquake.getDate().getTime())
                .setContentTitle("M: " + earthquake.getMagnitude())
                .setContentText(earthquake.getDetails())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(earthquake.getDetails()));

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        notificationManager.notify(NOTIFICATION_ID,
                earthquakeNotificationBuilder.build());
    }
}
