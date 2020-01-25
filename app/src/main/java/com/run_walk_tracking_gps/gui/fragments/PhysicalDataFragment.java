package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.GenderAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.TargetAdapterSpinner;
import com.run_walk_tracking_gps.gui.components.dialog.HeightDialog;
import com.run_walk_tracking_gps.gui.components.dialog.MeasureDialog;
import com.run_walk_tracking_gps.gui.components.dialog.WeightDialog;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.model.enumerations.Target;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PhysicalDataFragment extends Fragment {

    private final String TAG = PhysicalDataFragment.class.getName();

    private ListPopupWindow popup;
    private GenderAdapterSpinner spinnerGenderAdapter;
    private TargetAdapterSpinner spinnerTargetAdapter;

    private View view;
    private TextInputEditText gender;
    private TextInputEditText target;

    private TextInputEditText weight;
    private TextInputEditText height;

    private double weightValue;
    private double heightValue;

    private PhysicalDataListener physicalDataListener;

    private View.OnFocusChangeListener listenerDialog = (v, hasFocus) -> {
        if(hasFocus) showDialog(v);
    };

    private View.OnFocusChangeListener listenerPopup = (v, hasFocus) -> {
        if(hasFocus) showPopup(v);
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        popup = new ListPopupWindow(context);
        spinnerGenderAdapter = new GenderAdapterSpinner(context);
        spinnerTargetAdapter = new TargetAdapterSpinner(context);

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

        // gender
        gender = view.findViewById(R.id.signup_profile_gender);
        weight = view.findViewById(R.id.signup_profile_weight);
        height = view.findViewById(R.id.signup_profile_height);
        target = view.findViewById(R.id.signup_profile_target);

        setActionListener();

        return view;
    }

    private void setError(TextInputEditText view, int error){
        ((TextInputLayout) view.getParent().getParent())
                .setError(TextUtils.isEmpty(view.getText()) ? getString(error) : null);
    }

    private void setActionListener(){

        popup.setOnItemClickListener((parent, view, position, id) -> {
            if(parent.getAdapter() instanceof GenderAdapterSpinner) {
                //gender
                final Gender gObjectSelect = (Gender)parent.getAdapter().getItem(position);
                Log.e(TAG, gObjectSelect.toString());
                gender.setText(getString(gObjectSelect.getStrId()));
                gender.setCompoundDrawablesWithIntrinsicBounds(
                        getContext().getDrawable(gObjectSelect.getIconId()),
                        null, null, null);
            }

            if(parent.getAdapter() instanceof TargetAdapterSpinner) {
                //target
                final Target tObjectSelect = (Target)parent.getAdapter().getItem(position);
                Log.e(TAG, tObjectSelect.toString());
                target.setText(getString(tObjectSelect.getStrId()));
            }

            popup.dismiss();
        });

        popup.setOnDismissListener(() -> {
            if(gender.equals(popup.getAnchorView()))
                setError(gender, R.string.gender_not_empty);

            if(target.equals(popup.getAnchorView()))
                setError(target, R.string.target_not_empty);

            validation();
        });

        gender.setOnClickListener(this::showPopup);
        gender.setOnFocusChangeListener(listenerPopup);

        target.setOnClickListener(this::showPopup);
        target.setOnFocusChangeListener(listenerPopup);

        weight.setOnClickListener(this::showDialog);
        weight.setOnFocusChangeListener(listenerDialog);

        height.setOnClickListener(this::showDialog);
        height.setOnFocusChangeListener(listenerDialog);
    }

    private void showPopup(View v){
        popup.setAnchorView(v);
        popup.setAdapter(v.equals(target) ? spinnerTargetAdapter : spinnerGenderAdapter);
        popup.show();
    }

    private void showDialog(View v) {
        MeasureDialog dialog = null;
        if(v.equals(height)) {
            dialog = HeightDialog.create(view.getContext(), (heightMeasure) -> {
                if(heightMeasure!=null){
                    ((TextView)v).setText(heightMeasure.toString());
                    heightValue = heightMeasure.getValue(true);
                }
                validation();
            });
        }
        if(v.equals(weight)) {
            dialog = WeightDialog.create(view.getContext(), (weightMeasure) -> {
                if(weightMeasure!=null){
                    ((TextView)v).setText(weightMeasure.toString());
                    weightValue = weightMeasure.getValue(true);
                }
                validation();
            });
        }
        dialog.setOnDismissListener(dialog1 -> {
            if(v.equals(weight))
                setError(weight, R.string.weight_not_empty);

            if(v.equals(height))
                setError(height, R.string.height_not_empty);

            validation();
        });
        if(dialog!=null) dialog.show();
    }

    private void validation(){
        TextInputLayout genderL = ((TextInputLayout) gender.getParent().getParent());
        TextInputLayout targetL = ((TextInputLayout) target.getParent().getParent());
        TextInputLayout weightL = ((TextInputLayout) weight.getParent().getParent());
        TextInputLayout heightL = ((TextInputLayout) height.getParent().getParent());

        physicalDataListener.next(genderL.getError()==null && targetL.getError()==null && weightL.getError()==null && heightL.getError()==null &&
                         (!TextUtils.isEmpty(gender.getText()) &&
                        !TextUtils.isEmpty(target.getText())&&
                        !TextUtils.isEmpty(weight.getText()) && weightValue>0 &&
                        !TextUtils.isEmpty(height.getText()) &&  heightValue>0));
    }

    @Override
    public void onResume() {
        super.onResume();
        validation();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {

            JSONObject physicalData = new JSONObject()
                                .put(NetworkHelper.Constant.GENDER, gender.getText().toString())
                                .put(NetworkHelper.Constant.TARGET, target.getText().toString())
                                .put(NetworkHelper.Constant.HEIGHT, heightValue)
                                .put(NetworkHelper.Constant.WEIGHT, weightValue);

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
