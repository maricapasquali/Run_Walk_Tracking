package com.run_walk_tracking_gps.gui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.run_walk_tracking_gps.R;

import androidx.fragment.app.Fragment;

public class NoValueFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_item_no_value, container, true);
    }
}
