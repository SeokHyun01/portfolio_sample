package com.study.daily.databindingexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.study.daily.databindingexample.databinding.ListItemMovieBinding;

import java.util.ArrayList;
import java.util.List;

public class MovieRecyclerViewAdapter
        extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    private List<Movie> movieList;

    public MovieRecyclerViewAdapter() {
        this.movieList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemMovieBinding binding = ListItemMovieBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.binding.setMovie(movie);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    void setItem(List<Movie> movieList) {
        if (movieList == null) {
            return;
        }

        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListItemMovieBinding binding;

        public ViewHolder(ListItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
