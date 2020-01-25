package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.model.builder.UserBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class PersonalDataFragment extends Fragment {

    private static final String TAG = PersonalDataFragment.class.getName();
    private Factory.CustomImageView img;
    private FloatingActionButton takePhoto;

    private TextInputEditText name ;
    private TextInputEditText last_name ;
    private TextInputEditText birthDate;
    private TextInputEditText email;
    private TextInputEditText city;
    private TextInputEditText phone;

    private ImagePickerHandlerListener imagePickerHandlerListener;
    private PersonalDataListener personalDataListener;

    private UserBuilder userBuilder = UserBuilder.create();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
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
        final View view = inflater.inflate(R.layout.fragment_signup_1, container, false);
        Log.d(TAG,"onCreateView");
        img = view.findViewById(R.id.signup_profile_img);
        takePhoto = view.findViewById(R.id.take_photo);

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
        Stream.of(name, last_name, email, city, phone).forEach( editText ->
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String error = null;
                        if(editText.equals(name) && count <= 0)
                            error = getString(R.string.name_not_empty);
                        if(editText.equals(last_name) && count <= 0)
                            error = getString(R.string.lastname_not_empty);
                        if(editText.equals(email) && count <= 0)
                            error = getString(R.string.email_not_empty);
                        if(editText.equals(city) && count <= 0)
                            error = getString(R.string.city_not_empty);
                        if(editText.equals(phone) && s.length() <= 0)
                            error = getString(R.string.tel_not_empty);

                        ((TextInputLayout) editText.getParent().getParent()).setError(error);
                        validation();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
        }));

        birthDate.setOnClickListener(this::showDateDialog);
        birthDate.setOnFocusChangeListener((v, hasFocus)->{
            if(hasFocus) showDateDialog(v);
        });
        takePhoto.setOnClickListener(v -> imagePickerHandlerListener.imagePickerHandler(img));
    }

    private void showDateDialog(View v) {
        final TextView birthText = (TextView)v;
        DateTimePickerDialog.createDatePicker(getContext(),
                (date, calendar) -> {
                    birthText.setText(date);
                    userBuilder.setBirthDate(calendar.getTime());
                    birthDate.setError(null);
                    validation();
                }).show();
    }

    private void validation(){

        TextInputLayout nameL = ((TextInputLayout) name.getParent().getParent());
        TextInputLayout lastNameL = ((TextInputLayout) last_name.getParent().getParent());
        TextInputLayout birthDateL = ((TextInputLayout) birthDate.getParent().getParent());
        TextInputLayout emailL = ((TextInputLayout) email.getParent().getParent());
        TextInputLayout cityL = ((TextInputLayout) city.getParent().getParent());
        TextInputLayout phoneL = ((TextInputLayout) phone.getParent().getParent());

        personalDataListener.next(nameL.getError()==null && lastNameL.getError()==null &&
                                       emailL.getError()==null && cityL.getError()==null &&
                                       phoneL.getError()==null && birthDateL.getError()==null &&
                                        (!TextUtils.isEmpty(birthDate.getText()) &&
                                         !TextUtils.isEmpty(name.getText()) &&
                                         !TextUtils.isEmpty(last_name.getText()) &&
                                         !TextUtils.isEmpty(email.getText()) &&
                                         !TextUtils.isEmpty(phone.getText()) &&
                                         !TextUtils.isEmpty(city.getText())));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if(imagePickerHandlerListener.getImageUri()!=null)
            img.setImageURI(imagePickerHandlerListener.getImageUri());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
        try {

            JSONObject userJson  = userBuilder.setName(name.getText().toString())
                    .setLastName(last_name.getText().toString())
                    .setEmail(email.getText().toString())
                    .setCity(city.getText().toString())
                    .setPhone(phone.getText().toString())
                    .build().toJson();
                userJson.remove(NetworkHelper.Constant.ID_USER);
                userJson.remove(NetworkHelper.Constant.HEIGHT);

            personalDataListener.receivePersonalData(userJson);

        } catch (JSONException es){
            Log.e(TAG, es.getMessage());
        }
    }

    public interface ImagePickerHandlerListener{
        void imagePickerHandler(ImageView imageView);
        Uri getImageUri();
    }

    public interface PersonalDataListener{
        void next(boolean valid);
        void receivePersonalData(JSONObject personalInfoUser);
    }

}
