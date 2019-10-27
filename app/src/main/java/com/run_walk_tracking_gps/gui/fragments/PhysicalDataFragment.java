package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
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
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.model.enumerations.Target;

public class PhysicalDataFragment extends Fragment {

    private final String TAG = PhysicalDataFragment.class.getName();


    private Spinner gender;
    private TextView weight;
    private TextView height;
    private Spinner target;


    private double weightValue;
    private double heightValue;
    private PhysicalDataListener physicalDataListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            physicalDataListener = (PhysicalDataListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement PhysicalDataListener");
        }
    }

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
                weightValue = weight;
            }).show();
        });

    // height
        height = view.findViewById(R.id.signup_profile_height);
        height.setOnClickListener(v->{
            HeightDialog.create(view.getContext(), (heightString, height) -> {
                ((TextView)v).setText(heightString);
                Log.d(TAG,getString(R.string.height) +" : " +height);
                heightValue = height;
            }).show();

        });

    // target
        target = view.findViewById(R.id.signup_profile_target);
        final TargetAdapterSpinner spinnerTargetAdapter = new TargetAdapterSpinner(view.getContext(), true);
        if(target!=null)target.setAdapter(spinnerTargetAdapter);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        physicalDataListener.physicalData((int)gender.getSelectedItem(),(int)target.getSelectedItem(),
                heightValue, weightValue);
    }

    public interface PhysicalDataListener{
        void physicalData(int gender, int target, double height, double weight);
    }

}
