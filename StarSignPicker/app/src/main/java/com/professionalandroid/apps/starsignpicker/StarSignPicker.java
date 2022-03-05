package com.professionalandroid.apps.starsignpicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

public class StarSignPicker extends AppCompatActivity {

    public static final String EXTRA_SIGN_NAME = "SIGN_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_sign_picker);

        StarSignPickerAdapter adapter = new StarSignPickerAdapter();

        adapter.setOnAdapterItemClick(
                selectedItem -> {
                    Intent outData = new Intent();
                    outData.putExtra(EXTRA_SIGN_NAME, selectedItem);
                    setResult(RESULT_OK, outData);
                    finish();
                }
        );

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }
}