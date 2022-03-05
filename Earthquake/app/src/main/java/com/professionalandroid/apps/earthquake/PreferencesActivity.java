package com.professionalandroid.apps.earthquake;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferencesActivity extends AppCompatActivity {

    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
    public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
    }

    public static class PrefFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.userpreference, null);
        }
    }
}