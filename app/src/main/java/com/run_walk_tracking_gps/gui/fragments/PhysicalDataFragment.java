package com.run_walk_tracking_gps.gui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.adapter.spinner.GenderAdapterSpinner;
import com.run_walk_tracking_gps.gui.adapter.spinner.TargetAdapterSpinner;
import com.run_walk_tracking_gps.gui.dialog.HeightDialog;
import com.run_walk_tracking_gps.gui.dialog.WeightDialog;

public class PhysicalDataFragment extends Fragment {

    private final String TAG = PhysicalDataFragment.class.getName();


    private Spinner gender;
    private TextView weight;
    private TextView height;
    private Spinner target;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_signup_2, container, false);

        // TODO: 10/5/2019 --  GUI -- FIX BUG:
        // TODO: AL PRIMO CLICK IL DropDownView (DIALOG SPINNER) MOSTRA L'ICONA DELLA DESCRIZIONE

        // gender
        gender = view.findViewById(R.id.signup_profile_gender);
        final GenderAdapterSpinner spinnerGenderAdapter = new GenderAdapterSpinner(view.getContext(), true);
        if(gender!=null)gender.setAdapter(spinnerGenderAdapter);


    // weight
        weight = view.findViewById(R.id.signup_profile_weight);
        weight.setOnClickListener(v -> {
            WeightDialog.create(view.getContext(), (weightString, weight) -> {
                ((TextView)v).setText(weightString);
                Log.d(TAG,getString(R.string.weight) +" : " +weight);
            }).show();
        });

    // height
        height = view.findViewById(R.id.signup_profile_height);
        height.setOnClickListener(v->{
            HeightDialog.create(view.getContext(), height -> {
                ((TextView)v).setText(height);
                Log.d(TAG,getString(R.string.height) +" : " +height);
            }).show();

        });

    // target
        target = view.findViewById(R.id.signup_profile_target);
        final TargetAdapterSpinner spinnerTargetAdapter = new TargetAdapterSpinner(view.getContext(), true);
        if(target!=null)target.setAdapter(spinnerTargetAdapter);

        return view;
    }
}
