package com.professionalandroid.apps.starsignpicker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StarSignPickerAdapter
        extends RecyclerView.Adapter<StarSignPickerAdapter.ViewHolder> {

    public interface IAdapterItemClick {
        void onItemClicked(String selectedItem);
    }

    public void setOnAdapterItemClick(IAdapterItemClick adapterItemClickHandler) {
        mAdapterItemClickListener = adapterItemClickHandler;
    }

    IAdapterItemClick mAdapterItemClickListener;

    private String[] mStarSigns = {
            "Aries", "Taurus", "Gemini", "Cancer",
            "Leo", "Virgo", "Libra", "Scorpio",
            "Sagittarius", "Capricorn", "Aquarius",
            "Pisces"
    };

    public StarSignPickerAdapter() {
    }

    @NonNull
    @Override
    public StarSignPickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);

        return new ViewHolder(view, null);
    }

    @Override
    public void onBindViewHolder(@NonNull StarSignPickerAdapter.ViewHolder holder, int position) {
        holder.textView.setText(mStarSigns[position]);

        holder.mListener = view -> {
            if (mAdapterItemClickListener != null) {
                mAdapterItemClickListener.onItemClicked(mStarSigns[position]);
            }
        };
    }

    @Override
    public int getItemCount() {
        return mStarSigns == null ? 0 : mStarSigns.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView textView;
        public View.OnClickListener mListener;

        public ViewHolder(@NonNull View itemView, View.OnClickListener listener) {
            super(itemView);

            mListener = listener;

            textView = itemView.findViewById(R.id.itemTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onClick(view);
            }
        }
    }
}
