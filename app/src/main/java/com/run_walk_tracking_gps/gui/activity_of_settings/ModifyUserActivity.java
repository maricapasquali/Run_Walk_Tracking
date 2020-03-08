package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListPopupWindow;


import com.google.android.material.textfield.TextInputEditText;
import com.myhexaville.smartimagepicker.OnImagePickedListener;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.gui.components.Factory;
import com.run_walk_tracking_gps.gui.components.adapter.spinner.GenderAdapterSpinner;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.HeightDialog;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.JSONUtilities;
import com.run_walk_tracking_gps.utilities.ImageFileHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import androidx.annotation.RequiresApi;

public class ModifyUserActivity extends CommonActivity {

    private static final String TAG = ModifyUserActivity.class.getName();

    private ImageView img;
    private Factory.CustomTakePhotoButton take_photo;
    private TextInputEditText name;
    private TextInputEditText lastName;
    private TextInputEditText email;
    private TextInputEditText city;
    private TextInputEditText tel;

    private TextInputEditText gender;
    private TextInputEditText birthDate;
    private TextInputEditText height;

    private ListPopupWindow popup;
    private GenderAdapterSpinner spinnerGenderAdapter;
    private View.OnFocusChangeListener listenerDialog = (v, hasFocus) -> {
        if(hasFocus) showDialog(v);
    };
    private View.OnFocusChangeListener listenerPopup = (v, hasFocus) -> {
        if(hasFocus) showPopup(v);
    };

    private User oldUser;
    private User user;
    private JSONObject bodyJson;
    private ImageFileHelper imageFileHelper;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modify_profile);
        getSupportActionBar().setTitle(R.string.modify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageFileHelper = ImageFileHelper.create(this);

        img = findViewById(R.id.profile_img);
        take_photo = findViewById(R.id.take_photo);
        name = findViewById(R.id.modify_profile_name);
        lastName = findViewById(R.id.modify_profile_lastname);
        email = findViewById(R.id.modify_profile_email);
        city = findViewById(R.id.modify_profile_city);
        tel = findViewById(R.id.modify_profile_tel);
        gender = findViewById(R.id.modify_profile_gender);
        birthDate = findViewById(R.id.modify_profile_birth_date);
        height = findViewById(R.id.modify_profile_height);

        if(getIntent()!=null){
            oldUser = (User)getIntent().getParcelableExtra(KeysIntent.PROFILE);
            oldUser.setContext(this);
            Log.d(TAG, oldUser.toString());

            if(oldUser.getImage()!=null && oldUser.getImage().exists())
                imageFileHelper.load(img, oldUser.getImage());

            name.setText(oldUser.getName());
            lastName.setText(oldUser.getLastName());
            gender.setText(oldUser.getGender().getStrId());
            gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(oldUser.getGender().getIconId()), null, null, null);
            birthDate.setText(oldUser.getBirthDateString());
            email.setText(oldUser.getEmail());
            city.setText(oldUser.getCity());
            tel.setText(oldUser.getPhone());
            height.setText(oldUser.getHeight().toString());

            user = oldUser.clone();
        }

        popup = new ListPopupWindow(this);
        spinnerGenderAdapter = new GenderAdapterSpinner(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void saveUserChanged(){
        try {
            bodyJson = new JSONObject();
            if(!user.getName().equals(oldUser.getName())){
                bodyJson.put(UserDescriptor.NAME, user.getName());
            }
            if(!user.getLastName().equals(oldUser.getLastName())){
                bodyJson.put(UserDescriptor.LAST_NAME, user.getLastName());
            }
            if(!user.getGender().equals(oldUser.getGender())){
                bodyJson.put(UserDescriptor.GENDER, user.getGender());
            }
            if(!user.getBirthDate().equals(oldUser.getBirthDate())){
                bodyJson.put(UserDescriptor.BIRTH_DATE, user.getBirthDateStrDB());
            }
            if(!user.getEmail().equals(oldUser.getEmail())){
                bodyJson.put(UserDescriptor.EMAIL, user.getEmail());
            }
            if(!user.getCity().equals(oldUser.getCity())){
                bodyJson.put(UserDescriptor.CITY, user.getCity());
            }
            if(!user.getPhone().equals(oldUser.getPhone())){
                bodyJson.put(UserDescriptor.PHONE, user.getPhone());
            }
            if(!user.getHeight().getValue(true).equals(oldUser.getHeight().getValue(true))){
                bodyJson.put(UserDescriptor.HEIGHT, user.getHeight().getValue(true));
            }
            if(!user.getImage().equals(oldUser.getImage())){
                bodyJson.put(NetworkHelper.Constant.IMAGE, new JSONObject().put(ImageProfileDescriptor.NAME, user.getImage().getName()));
            }

            Log.d(TAG, user + ", count = "+ JSONUtilities.countKey(bodyJson));

            if(DaoFactory.getInstance(this).getUserDao().update(bodyJson)){
                Preferences.Session.update(this);
                if(imageFileHelper.moveToImageDir(user.getImage().getName())){
                    if(oldUser.getImage()!=null && oldUser.getImage().exists())
                        oldUser.getImage().delete();
                    imageFileHelper.deleteTmpDir();
                }

                NetworkServiceHandler.getInstance(this,
                        NetworkHelper.Constant.UPDATE, NetworkHelper.Constant.USER,
                        bodyJson.toString()).startService();

                setResult(RESULT_OK, new Intent().putExtra(KeysIntent.CHANGED_USER, user));
            }else{
                setResult(RESULT_CANCELED, new Intent());
            }
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile:{
                Log.e(TAG, "SALVA MODIFICHE");

                user.setName(TextUtils.isEmpty(name.getText())? oldUser.getName() : name.getText().toString());
                user.setLastName(TextUtils.isEmpty(lastName.getText())? oldUser.getName() : lastName.getText().toString());
                user.setEmail(TextUtils.isEmpty(email.getText())? oldUser.getName() : email.getText().toString());
                user.setCity(TextUtils.isEmpty(city.getText())? oldUser.getName() : city.getText().toString());
                user.setPhone(TextUtils.isEmpty(tel.getText())? oldUser.getName() : tel.getText().toString());

                // REQUEST UPDATE USER
                saveUserChanged();
            }
            break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void listenerAction() {

        take_photo.onTakePhotoListener(new Factory.CustomTakePhotoButton.OnTakePhotoListener() {
            @Override
            public Activity getActivity() {
                return ModifyUserActivity.this;
            }

            @Override
            public OnImagePickedListener setonClickListener() {
                return imageUri -> {
                    String newName = ImageFileHelper.createNameRandom();
                    Log.e(TAG, "Name : "+ newName);
                    if(imageFileHelper.moveToTmpDir(imageUri, newName)){
                        File imageTmp = imageFileHelper.getImageTmp(newName);
                        imageFileHelper.load(img, imageTmp);
                        user.setImageProfile(imageTmp);

                        Log.e(TAG, "Name File user changed : "+ user.getImage());
                    }
                };
            }
        });

        popup.setOnItemClickListener((parent, view, position, id) -> {
            //gender
            final Gender gObjectSelect = (Gender)parent.getAdapter().getItem(position);
            Log.e(TAG, gObjectSelect.toString());
            user.setGender(gObjectSelect);
            gender.setText(getString(gObjectSelect.getStrId()));
            gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(gObjectSelect.getIconId()),
                        null, null, null);
            popup.dismiss();
        });
        gender.setOnClickListener(this::showPopup);
        gender.setOnFocusChangeListener(listenerPopup);

        height.setOnClickListener(this::showDialog);
        height.setOnFocusChangeListener(listenerDialog);

        birthDate.setOnClickListener(this::showDialog);
        birthDate.setOnFocusChangeListener(listenerDialog);
    }

    private void showPopup(View v){
        popup.setAnchorView(v);
        popup.setAdapter(spinnerGenderAdapter);
        popup.show();
    }

    private void showDialog(View v) {
        final TextInputEditText textView = ((TextInputEditText) v);

        if(v.equals(height)) {
            HeightDialog.create(this, textView.getText().toString(),  (heightMeasure) -> {
                if(heightMeasure!=null){
                    textView.setText(heightMeasure.toString());
                    user.getHeight().setValue(false, heightMeasure.getValue(false));
                }
            }).create().show();
        }
        if(v.equals(birthDate)) {

            DateTimePickerDialog.createDatePicker(this, (date, calendar) -> {
                textView.setText(date);
                user.setBirthDate(calendar.getTime());
            }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        take_photo.getImagePiker().handleActivityResult(resultCode,requestCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        take_photo.getImagePiker().handlePermission(requestCode, grantResults);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        imageFileHelper.deleteTmpDir();
    }
}
