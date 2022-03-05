package com.study.daily.pendingintentexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class NotificationExample extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_example);

        CharSequence sequence = "";
        int id = 0;
        String extraData = "";

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            id = extras.getInt("NOTIFICATION_ID");
            extraData = extras.getString("EXTRA_DATA");
        }

        TextView notificationTextView = findViewById(R.id.notification_textView);
        sequence = id + ": " + extraData;
        notificationTextView.setText(sequence);

        NotificationManager notificationManager
                = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}