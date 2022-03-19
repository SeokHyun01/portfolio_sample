package com.professionalandroid.apps.earthquake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EarthquakeListFragment extends Fragment {
    private static final String TAG = "EarthquakeListFragment";

    private ArrayList<Earthquake> mEarthquakes = new ArrayList<>();

    private RecyclerView mRecyclerView;

    private EarthquakeRecyclerViewAdapter mEarthquakeRecyclerViewAdapter =
            new EarthquakeRecyclerViewAdapter(mEarthquakes);

    protected EarthquakeViewModel earthquakeViewModel;

    private SwipeRefreshLayout mSwipeToRefreshView;

    private int mMinimumMagnitude = 0;

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (PreferencesActivity.PREF_MIN_MAG.equals(key)) {
                        List<Earthquake> earthquakes
                                = earthquakeViewModel.getEarthquakes().getValue();
                        if (earthquakes != null) {
                            setEarthquakes(earthquakes);
                        }
                    }
                }
            };

    public interface OnListFragmentInteractionListener {
        void onListFragmentRefreshRequested();
    }

    private OnListFragmentInteractionListener mListener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(mPrefListener);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = view.getContext();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mEarthquakeRecyclerViewAdapter);

        mSwipeToRefreshView = view.findViewById(R.id.swipefresh);
        mSwipeToRefreshView.setOnRefreshListener(this::updateEarthquakes);

        earthquakeViewModel = new ViewModelProvider(getActivity()).get(EarthquakeViewModel.class);
        earthquakeViewModel.getEarthquakes()
                .observe(getViewLifecycleOwner(), earthquakes -> {
                    if (earthquakes != null) {
                        setEarthquakes(earthquakes);
                    }
                });
    }

    protected void updateEarthquakes() {
        if (mListener != null) {
            mListener.onListFragmentRefreshRequested();
        }
    }

    public EarthquakeListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setEarthquakes(List<Earthquake> earthquakes) {
        updateFromPreferences();

        for (Earthquake earthquake : earthquakes) {
            if (earthquake.getMagnitude() >= mMinimumMagnitude) {
                if (!mEarthquakes.contains(earthquake)) {
                    mEarthquakes.add(earthquake);
                    mEarthquakeRecyclerViewAdapter
                            .notifyItemInserted(mEarthquakes.indexOf(earthquake));
                }
            }
        }

        if (mEarthquakes != null && mEarthquakes.size() > 0) {
            for (int i = mEarthquakes.size() - 1; i >= 0; i--) {
                if (mEarthquakes.get(i).getMagnitude() < mMinimumMagnitude) {
                    mEarthquakes.remove(i);
                    mEarthquakeRecyclerViewAdapter.notifyItemRemoved(i);
                }
            }
        }

        mSwipeToRefreshView.setRefreshing(false);
    }

    private void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMinimumMagnitude = Integer.parseInt(
                prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
    }


}
