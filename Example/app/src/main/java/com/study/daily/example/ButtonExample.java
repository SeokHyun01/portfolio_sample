package com.study.daily.example;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class ButtonExample extends AppCompatActivity {
    private static final String TAG = "BUTTON_EXAMPLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_example);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(view -> {
            Log.d(TAG, "log1");
        });

        button.setOnClickListener(view -> {
            Log.d(TAG, "log2");
        });
    }
}