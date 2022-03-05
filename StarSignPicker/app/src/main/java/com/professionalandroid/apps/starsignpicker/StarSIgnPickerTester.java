package com.professionalandroid.apps.starsignpicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StarSIgnPickerTester extends AppCompatActivity {

    public static final int PICK_STARSIGN = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_STARSIGN): {
                if (resultCode == RESULT_OK) {
                    String selectedSign =
                            data.getStringExtra(StarSignPicker.EXTRA_SIGN_NAME);
                    TextView textview = findViewById(R.id.selected_starsign_textview);
                    textview.setText(selectedSign);
                }
                break;
            }

            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_sign_picker_tester);

        Button button = findViewById(R.id.pick_starsigns_button);

        button.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    Uri.parse("starsigns://"));
            startActivityForResult(intent, PICK_STARSIGN);
        });

        TextView linkifyTextView = findViewById(R.id.textView);

        String baseUri = "starsigns://";

        int flags = Pattern.CASE_INSENSITIVE;

        Pattern pattern = Pattern.compile("star sign");

        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "";
            }
        };

        Linkify.addLinks(linkifyTextView, pattern, baseUri, null, transformFilter);
    }
}