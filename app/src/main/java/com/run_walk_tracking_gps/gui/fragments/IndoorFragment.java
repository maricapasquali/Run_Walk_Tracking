package com.run_walk_tracking_gps.gui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.run_walk_tracking_gps.R;

import androidx.fragment.app.Fragment;

public class IndoorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_indoor, container, false);
    }
}
