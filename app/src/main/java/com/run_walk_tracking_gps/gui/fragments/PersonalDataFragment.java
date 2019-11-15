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
import android.widget.ImageView;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.model.UserBuilder;

import org.json.JSONException;
import org.json.JSONObject;


public class PersonalDataFragment extends Fragment {

    private static final String TAG = PersonalDataFragment.class.getName();

    private ImageView img;
    private EditText name ;
    private EditText last_name ;
    private EditText birthDate;
    private EditText email ;
    private EditText city ;
    private EditText phone ;

    private ImagePickerHandlerListener imagePickerHandlerListener;
    private PersonalDataListener personalDataListener;

    private UserBuilder userBuilder = UserBuilder.create();
    private boolean isOk = true;

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
                userBuilder.setBirthDate(calendar.getTime());
            },false ).show();
        });

        img.setOnClickListener( v -> imagePickerHandlerListener.imagePickerHandler((ImageView)v));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        if(!isOk){
            // TODO: 11/13/2019 SISTEMARE ERROR STRING
            if(TextUtils.isEmpty(name.getText()))
                name.setError("Nome non vuoto");
            if(TextUtils.isEmpty(birthDate.getText()))
                birthDate.setError("Nome non vuoto");
            if(TextUtils.isEmpty(last_name.getText()))
                last_name.setError("Nome non vuoto");
            if(TextUtils.isEmpty(email.getText()))
                email.setError("Nome non vuoto");
            if(TextUtils.isEmpty(city.getText()))
                city.setError("Nome non vuoto");
            if(TextUtils.isEmpty(phone.getText()))
                phone.setError("Nome non vuoto");
        }
    }

    private boolean isSetAll(){
        return isOk = (!TextUtils.isEmpty(name.getText()) &&
                !TextUtils.isEmpty(last_name.getText()) &&
                !TextUtils.isEmpty(birthDate.getText()) &&
                !TextUtils.isEmpty(email.getText()) &&
                !TextUtils.isEmpty(city.getText()) &&
                !TextUtils.isEmpty(phone.getText()));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        try {
            JSONObject userJson = null;
            if(isSetAll()){
                userJson = userBuilder.setName(name.getText().toString())
                                      .setLastName(last_name.getText().toString())
                                      .setEmail(email.getText().toString())
                                      .setCity(city.getText().toString())
                                      .setPhone(phone.getText().toString())
                                      .build()
                                      .toJson();

                userJson.remove(FieldDataBase.ID_USER.toName());
                userJson.remove(FieldDataBase.HEIGHT.toName());
            }

            personalDataListener.personalData(userJson);

        } catch (JSONException es){
            Log.e(TAG, es.getMessage());
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
        void personalData(JSONObject personalInfoUser);
    }
}
