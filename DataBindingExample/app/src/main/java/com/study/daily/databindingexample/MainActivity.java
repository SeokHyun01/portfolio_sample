package com.study.daily.databindingexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;

import android.os.Bundle;

import com.study.daily.databindingexample.databinding.ActivityMainBinding;
import com.study.daily.databindingexample.databinding.ListItemMovieBinding;

public class MainActivity extends AppCompatActivity {

    private ObservableArrayList<Movie> movieList;
    private MovieRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityMainBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_main
        );

        mAdapter = new MovieRecyclerViewAdapter();
        movieList = new ObservableArrayList<>();

        binding.recyclerView.setAdapter(mAdapter);
        binding.setMovieList(movieList);

        prepareMovieData();
    }

    private void prepareMovieData() {

        movieList.add(new Movie("Mad Max: Fury Road", "Action & Adventure", "2015"));
        movieList.add(new Movie("Inside Out", "Animation, Kids & Family", "2015"));
        movieList.add(new Movie("Star Wars: Episode VII - The Force Awakens", "Action", "2015"));
        movieList.add(new Movie("Shaun the Sheep", "Animation", "2015"));
    }
}