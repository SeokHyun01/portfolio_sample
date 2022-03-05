package com.study.daily.databindingexample;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterBindings {

    @BindingAdapter("bind:item")
    public static void bindItem(RecyclerView recyclerView, ObservableArrayList<Movie> movie) {
        MovieRecyclerViewAdapter adapter = (MovieRecyclerViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setItem(movie);
        }
    }
}
