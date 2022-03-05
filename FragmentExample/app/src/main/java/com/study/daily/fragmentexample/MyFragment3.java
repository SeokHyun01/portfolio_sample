package com.study.daily.fragmentexample;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class MyFragment3 extends Fragment {

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(TextView textView);
    }

    OnFragmentInteractionListener mListener;

    TextView info;
    Button interaction_button;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public MyFragment3() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        info = view.findViewById(R.id.info);

        interaction_button = view.findViewById(R.id.interaction_button);
        interaction_button.setOnClickListener(button -> {
            onButtonPressed();
        });
    }

    public void onButtonPressed() {

        if (mListener != null) {
            mListener.onFragmentInteraction(info);
        }
    }
}