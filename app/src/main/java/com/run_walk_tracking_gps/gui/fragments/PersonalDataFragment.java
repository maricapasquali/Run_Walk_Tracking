package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.SignUpActivity;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Date;


public class PersonalDataFragment extends Fragment {

    private static final String TAG = PersonalDataFragment.class.getName();

    private ImageView img;
    private EditText name ;
    private EditText last_name ;
    private TextView birthDate;
    private EditText email ;
    private EditText city ;
    private EditText phone ;

    private ImagePickerHandlerListener imagePickerHandlerListener;
    private PersonalDataListener personalDataListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            personalDataListener =(PersonalDataListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement PersonalDataListener");
        }
        try {
            imagePickerHandlerListener =(ImagePickerHandlerListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement ImagePickerHandlerListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_1, container, false);

        img = view.findViewById(R.id.signup_profile_img);
        name = view.findViewById(R.id.signup_profile_name);
        last_name = view.findViewById(R.id.signup_profile_lastname);
        birthDate = view.findViewById(R.id.signup_profile_birth_date);
        email = view.findViewById(R.id.signup_profile_email);
        city = view.findViewById(R.id.signup_profile_city);
        phone = view.findViewById(R.id.signup_profile_tel);


        birthDate.setOnClickListener(v ->{
            final TextView  birthText = (TextView)v;
            DateTimePickerDialog.create(getContext(), birthText.getText().toString(),
                    (date, calendar) -> {
                birthText.setText(date);
            },false ).show();
        });

        img.setOnClickListener( v -> imagePickerHandlerListener.imagePickerHandler((ImageView)v));

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        try {
            personalDataListener.personalData(name.getText().toString(),
                                              last_name.getText().toString(),
                                              DateUtilities.parseShortToDate(birthDate.getText().toString()) ,
                                              email.getText().toString(),
                                              city.getText().toString(),
                                              phone.getText().toString());
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDestroy");
    }


    public interface ImagePickerHandlerListener{
        void imagePickerHandler(ImageView imageView);
    }

    public interface PersonalDataListener{
        void personalData(String name, String last_name, Date date_birth, String email, String city, String phone);
    }
}
