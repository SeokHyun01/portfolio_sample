package com.professionalandroid.apps.earthquake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeSearchResultActivity extends AppCompatActivity {

    private ArrayList<Earthquake> mEarthquakes = new ArrayList<>();
    private EarthquakeRecyclerViewAdapter mEarthquakeAdapter
            = new EarthquakeRecyclerViewAdapter(mEarthquakes);

    MutableLiveData<String> searchQuery;
    MutableLiveData<String> selectedSearchSuggestionId;

    LiveData<List<Earthquake>> searchResults;
    LiveData<Earthquake> selectedSearchSuggestion;

    private void setSelectedSearchSuggestion(Uri dataString) {
        String id = dataString.getPathSegments().get(1);
        selectedSearchSuggestionId.setValue(id);
    }

    final Observer<Earthquake> selectedSearchSuggestionObserver
            = selectedSearchSuggestion -> {
        if (selectedSearchSuggestion != null) {
            setSearchQuery(selectedSearchSuggestion.getDetails());
        }
    };

    private void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    private final Observer<List<Earthquake>> searchQueryResultObserver
            = updatedEarthquakes -> {
        mEarthquakes.clear();

        if (updatedEarthquakes != null) {
            mEarthquakes.addAll(updatedEarthquakes);
            mEarthquakeAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            setSelectedSearchSuggestion(getIntent().getData());
        } else {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            setSearchQuery(query);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_search_result);

        RecyclerView recyclerView = findViewById(R.id.search_result_list);
        recyclerView.setAdapter(mEarthquakeAdapter);

        searchQuery = new MutableLiveData<>();
        searchQuery.setValue(null);

        searchResults = Transformations.switchMap(searchQuery,
                query ->
                        EarthquakeDatabaseAccessor
                                .getInstance(getApplicationContext())
                                .earthquakeDAO()
                                .searchEarthquakes("%" + query + "%"));

        searchResults.observe(EarthquakeSearchResultActivity.this,
                searchQueryResultObserver);


        selectedSearchSuggestionId = new MutableLiveData<>();
        selectedSearchSuggestionId.setValue(null);

        selectedSearchSuggestion = Transformations.switchMap(selectedSearchSuggestionId,
                id -> EarthquakeDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .earthquakeDAO()
                        .getEarthquake(id));

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            selectedSearchSuggestion.observe(this,
                    selectedSearchSuggestionObserver);

            setSelectedSearchSuggestion(getIntent().getData());
        } else {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            setSearchQuery(query);
        }
    }
}