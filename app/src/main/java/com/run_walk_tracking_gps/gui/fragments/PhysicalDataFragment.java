package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.GenderAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.TargetAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.dialog.HeightDialog;
import com.run_walk_tracking_gps.gui.components.dialog.WeightDialog;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.model.enumerations.Target;
import com.run_walk_tracking_gps.utilities.EnumUtilities;


import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class PhysicalDataFragment extends Fragment {

    private final String TAG = PhysicalDataFragment.class.getName();


    private View view;
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
        view = inflater.inflate(R.layout.fragment_signup_2, container, false);

        // TODO: 10/5/2019 --  GUI -- FIX BUG: AL PRIMO CLICK IL DropDownView (DIALOG SPINNER) MOSTRA L'ICONA DELLA DESCRIZIONE

        // gender
        gender = view.findViewById(R.id.signup_profile_gender);
        final GenderAdapterSpinner spinnerGenderAdapter = new GenderAdapterSpinner(view.getContext(), true);
        if(gender!=null) gender.setAdapter(spinnerGenderAdapter);

        weight = view.findViewById(R.id.signup_profile_weight);

        height = view.findViewById(R.id.signup_profile_height);

           target = view.findViewById(R.id.signup_profile_target);
        final TargetAdapterSpinner spinnerTargetAdapter = new TargetAdapterSpinner(view.getContext(), true);
        if(target!=null) target.setAdapter(spinnerTargetAdapter);

        Log.d(TAG, "Gender = " +gender.getSelectedItem().equals(R.string.gender));


        setActionListener();

        return view;
    }

    private void setActionListener(){

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        weight.setOnClickListener(v -> {
            WeightDialog.create(view.getContext(), (weightMeasure) -> {
                if(weightMeasure!=null){
                    ((TextView)v).setText(weightMeasure.toString());
                    weightValue = weightMeasure.getValue(true);
                }
                validation();
            }).show();
        });

        height.setOnClickListener(v->{
            HeightDialog.create(view.getContext(), (heightMeasure) -> {
                if(heightMeasure!=null){
                    ((TextView)v).setText(heightMeasure.toString());
                    heightValue = heightMeasure.getValue(true);
                    height.setError(null);
                }
                validation();
            }).show();

        });

        target.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void validation(){
        physicalDataListener.next(isSetAll());
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        validation();
        /*if(!isOk){
            if(TextUtils.isEmpty(weight.getText()) || weightValue==0)
                weight.setError(getString(R.string.weight_not_empty));
            if(TextUtils.isEmpty(height.getText()) || heightValue==0)
                height.setError(getString(R.string.height_not_empty));
            if(gender.getSelectedItem().equals(R.string.gender))
                new Handler().post(() -> ((TextView)gender.getSelectedView()).setError(getString(R.string.gender_not_empty)));
            if(target.getSelectedItem().equals(R.string.target))
                new Handler().post(() -> ((TextView)target.getSelectedView()).setError(getString(R.string.target_not_empty)));
        }*/
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

            JSONObject physicalData= new JSONObject();
            if(isSetAll()){
            physicalData = new JSONObject()
                                .put(NetworkHelper.Constant.GENDER, EnumUtilities.getEnumFromStrId(Gender.class, (int) gender.getSelectedItem()))
                                .put(NetworkHelper.Constant.TARGET, EnumUtilities.getEnumFromStrId(Target.class, (int) target.getSelectedItem()))
                                .put(NetworkHelper.Constant.HEIGHT, heightValue)
                                .put(NetworkHelper.Constant.WEIGHT, weightValue);
            }

            physicalDataListener.physicalData(physicalData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface PhysicalDataListener{
        void next(boolean valid);
        void physicalData(JSONObject jsonPhysical);
    }

}
