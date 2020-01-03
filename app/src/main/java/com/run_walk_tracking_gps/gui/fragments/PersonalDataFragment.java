package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.model.builder.UserBuilder;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;


public class PersonalDataFragment extends Fragment {

    private static final String TAG = PersonalDataFragment.class.getName();

    private Factory.CustomImageView img;

    private View view;

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
        view = inflater.inflate(R.layout.fragment_signup_1, container, false);

        img = view.findViewById(R.id.signup_profile_img);

        name = view.findViewById(R.id.signup_profile_name);
        last_name = view.findViewById(R.id.signup_profile_lastname);
        birthDate = view.findViewById(R.id.signup_profile_birth_date);
        email = view.findViewById(R.id.signup_profile_email);
        city = view.findViewById(R.id.signup_profile_city);
        phone = view.findViewById(R.id.signup_profile_tel);

        setActionListener();
        return view;
    }

    private void setActionListener(){

        Stream.of(name, last_name, birthDate, email, city, phone).forEach( editTexts -> {
            editTexts.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count<=0){
                        if(editTexts.equals(name)){
                            name.setError(getString(R.string.name_not_empty));
                        }
                        if(editTexts.equals(last_name)){
                            last_name.setError(getString(R.string.lastname_not_empty));
                        }
                        if(editTexts.equals(email)){
                            email.setError(getString(R.string.email_not_empty));
                        }
                        if(editTexts.equals(city)){
                            city.setError(getString(R.string.city_not_empty));
                        }
                        if(editTexts.equals(phone)){
                            phone.setError(getString(R.string.tel_not_empty));
                        }
                    }

                    validation();

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        });

        birthDate.setOnClickListener(v ->{
            final TextView  birthText = (TextView)v;
            DateTimePickerDialog.createDatePicker(getContext(),
                    (date, calendar) -> {
                        birthText.setText(date);
                        userBuilder.setBirthDate(calendar.getTime());
                        birthDate.setError(null);
                        validation();
                    } ).show();
        });

        img.setOnClickListener( v -> imagePickerHandlerListener.imagePickerHandler((ImageView)v));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if(imagePickerHandlerListener.getImageUri()!=null)
            img.setImageURI(imagePickerHandlerListener.getImageUri());
        /*if(!isOk){
            if(TextUtils.isEmpty(name.getText()))
                name.setError(getString(R.string.name_not_empty));
            if(TextUtils.isEmpty(birthDate.getText()))
                birthDate.setError(getString(R.string.birth_date_not_empty));
            if(TextUtils.isEmpty(last_name.getText()))
                last_name.setError(getString(R.string.lastname_not_empty));

            if(TextUtils.isEmpty(email.getText()))
                email.setError(getString(R.string.email_not_empty));

            if(TextUtils.isEmpty(city.getText()))
                city.setError(getString(R.string.city_not_empty));
            if(TextUtils.isEmpty(phone.getText()))
                phone.setError(getString(R.string.tel_not_empty));
        }*/
    }

    private void validation(){
        personalDataListener.next((name.getError()==null && last_name.getError()==null && email.getError()==null && city.getError()==null && phone.getError()==null) &&
                (!TextUtils.isEmpty(name.getText()) &&
                        !TextUtils.isEmpty(last_name.getText()) &&
                        !TextUtils.isEmpty(birthDate.getText()) &&
                        !TextUtils.isEmpty(email.getText()) &&
                        !TextUtils.isEmpty(city.getText()) &&
                        !TextUtils.isEmpty(phone.getText())));

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
        try {
            //JSONObject userJson = null;
            //if(isSetAll()){
            JSONObject userJson = userBuilder.setName(name.getText().toString())
                        .setLastName(last_name.getText().toString())
                        .setEmail(email.getText().toString())
                        .setCity(city.getText().toString())
                        .setPhone(phone.getText().toString())
                        .build()
                        .toJson();

                userJson.remove(NetworkHelper.Constant.ID_USER);
                userJson.remove(NetworkHelper.Constant.HEIGHT);
            //}

            personalDataListener.personalData(userJson);

        } catch (JSONException es){
            Log.e(TAG, es.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    public interface ImagePickerHandlerListener{
        void imagePickerHandler(ImageView imageView);
        Uri getImageUri();
    }

    public interface PersonalDataListener{
        void next(boolean valid);
        void personalData(JSONObject personalInfoUser);
    }
}
