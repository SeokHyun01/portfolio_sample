package com.professionalandroid.apps.earthquake;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EarthquakeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEarthquakes(List<Earthquake> earthquakes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEarthquake(Earthquake earthquake);

    @Delete
    public void deleteEarthquake(Earthquake earthquake);

    @Query("SELECT * FROM earthquake ORDER BY mDate DESC")
    public LiveData<List<Earthquake>> loadAllEarthquakes();

    @Query("SELECT mId as _id, " +
            "mDetails as suggest_text_1, " +
            "mId as suggest_intent_data_id " +
            "FROM Earthquake " +
            "WHERE mDetails LIKE :query " +
            "ORDER BY mDate DESC")
    public Cursor generateSearchSuggestions(String query);

    @Query("SELECT * " +
            "FROM Earthquake " +
            "WHERE mDetails LIKE :query " +
            "ORDER BY mDate DESC")
    public LiveData<List<Earthquake>> searchEarthquakes(String query);

    @Query("SELECT * " +
            "FROM Earthquake " +
            "WHERE mId = :id " +
            "LIKE 1")
    public LiveData<Earthquake> getEarthquake(String id);
}
