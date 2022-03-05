package com.study.daily.fragmentexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button fragment1_button;
    private Button fragment2_button;

    private Button display_fragment1_button;
    private Button display_fragment2_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1_button = findViewById(R.id.fragment_button);
        fragment2_button = findViewById(R.id.fragment2_button);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragment1_button.setOnClickListener(button -> {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,
                    new MyFragment()).addToBackStack("fragment1").commit();
        });

        fragment2_button.setOnClickListener(button -> {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,
                    new MyFragment2()).addToBackStack("fragment2").commit();
        });

        display_fragment1_button = findViewById(R.id.display_fragment1_button);
        display_fragment1_button.setOnClickListener(button -> {
            fragmentManager.popBackStack("fragment1", 0);
        });

        display_fragment2_button = findViewById(R.id.display_fragment2_button);
        display_fragment2_button.setOnClickListener(button -> {
            fragmentManager.popBackStack("fragment2", 0);
        });
    }
}