package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.gui.adapter.spinner.GenderAdapterSpinner;
import com.run_walk_tracking_gps.gui.adapter.spinner.TargetAdapterSpinner;
import com.run_walk_tracking_gps.gui.dialog.HeightDialog;
import com.run_walk_tracking_gps.gui.dialog.WeightDialog;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.model.enumerations.Target;
import com.run_walk_tracking_gps.utilities.EnumUtilities;


import org.json.JSONException;
import org.json.JSONObject;

public class PhysicalDataFragment extends Fragment {

    private final String TAG = PhysicalDataFragment.class.getName();


    private Spinner gender;
    private EditText weight;
    private EditText height;
    private Spinner target;

    private double weightValue;
    private double heightValue;

    private PhysicalDataListener physicalDataListener;

    private boolean isOk = true;

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

        // TODO: 10/5/2019 --  GUI -- FIX BUG: AL PRIMO CLICK IL DropDownView (DIALOG SPINNER) MOSTRA L'ICONA DELLA DESCRIZIONE

        // gender
        gender = view.findViewById(R.id.signup_profile_gender);
        final GenderAdapterSpinner spinnerGenderAdapter = new GenderAdapterSpinner(view.getContext(), true);
        if(gender!=null)gender.setAdapter(spinnerGenderAdapter);


    // weight
        weight = view.findViewById(R.id.signup_profile_weight);
        weight.setOnClickListener(v -> {
            WeightDialog.create(view.getContext(), (weightMeasure) -> {
               if(weightMeasure!=null){
                   ((TextView)v).setText(weightMeasure.toString(getContext()));
                   weightValue = weightMeasure.getValue();
               }
            }).show();
        });

    // height
        height = view.findViewById(R.id.signup_profile_height);
        height.setOnClickListener(v->{
            HeightDialog.create(view.getContext(), (heightMeasure) -> {
                if(heightMeasure!=null){
                    ((TextView)v).setText(heightMeasure.toString(getContext()));
                    heightValue = heightMeasure.getValue();
                }
            }).show();

        });

    // target
        target = view.findViewById(R.id.signup_profile_target);
        final TargetAdapterSpinner spinnerTargetAdapter = new TargetAdapterSpinner(view.getContext(), true);
        if(target!=null)target.setAdapter(spinnerTargetAdapter);


        Log.e(TAG, "Gender = " +gender.getSelectedItem().equals(R.string.gender));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if(!isOk){
            // TODO: 11/13/2019 SISTEMARE ERROR STRING
            if(TextUtils.isEmpty(weight.getText()) || weightValue==0)
                weight.setError("Nome non vuoto");
            if(TextUtils.isEmpty(height.getText()) || heightValue==0)
                height.setError("Nome non vuoto");
            //if(gender.getSelectedItem().equals(R.string.gender)) ((TextView)gender.getSelectedView()).setError("Nome non vuoto");
            //if(target.getSelectedItem().equals(R.string.target)) ((TextView)target.getSelectedView()).setError("Nome non vuoto");
        }
    }

    private boolean isSetAll(){
        return isOk = (!gender.getSelectedItem().equals(R.string.gender) &&
                !target.getSelectedItem().equals(R.string.target) &&
                !TextUtils.isEmpty(weight.getText()) && weightValue>0 &&
                !TextUtils.isEmpty(height.getText()) &&  heightValue>0);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {

            JSONObject physicalData= null;
            if(isSetAll()){
                physicalData = new JSONObject().put(FieldDataBase.GENDER.toName(), EnumUtilities.getEnumFromStrId(Gender.class, (int)gender.getSelectedItem()))
                        .put(FieldDataBase.TARGET.toName(), EnumUtilities.getEnumFromStrId(Target.class, (int) target.getSelectedItem()))
                        .put(FieldDataBase.HEIGHT.toName(), heightValue)
                        .put(FieldDataBase.WEIGHT.toName(), weightValue);
            }

            physicalDataListener.physicalData(physicalData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface PhysicalDataListener{
        void physicalData(JSONObject jsonPhysical);
    }

}
