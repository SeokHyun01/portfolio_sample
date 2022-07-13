package com.study.daily.samplebroadcastreceiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.security.Permission;
import java.security.Permissions;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    public static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    BroadcastReceiver br;

    @Override
    protected void onResume() {
        super.onResume();

        br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        this.registerReceiver(br, filter, Manifest.permission.RECEIVE_SMS, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (br != null) {
            unregisterReceiver(br);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] strings) {
        Log.d(TAG, "onDenied: permissions denied: " + strings.length);
    }

    @Override
    public void onGranted(int i, String[] strings) {
        Log.d(TAG, "onGranted: permissions granted: " + strings.length);
    }
}