package com.run_walk_tracking_gps.gui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;

import java.util.Calendar;


public class PersonalDataFragment extends Fragment {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_1, container, false);

        final TextView birthDate = view.findViewById(R.id.signup_profile_birth_date);
        birthDate.setOnClickListener(v ->{
            final TextView  birthText = (TextView)v;
            DateTimePickerDialog.create(getContext(), birthText.getText().toString(), (date, calendar) -> birthText.setText(date),false ).show();
        });


        return view;
    }

}
