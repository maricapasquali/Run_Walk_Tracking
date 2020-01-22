package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.SqlLiteUserDao;
import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.components.dialog.ChooseDialog;
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
import java.util.Arrays;

import androidx.annotation.RequiresApi;

public class ModifyUserActivity extends CommonActivity {

    private static final String TAG = ModifyUserActivity.class.getName();

    private ImageView img;
    private EditText name;
    private EditText lastName;
    private EditText email;
    private EditText city;
    private EditText tel;
    private TextView gender;
    private TextView birthDate;
    private TextView height;

    private ImagePicker imagePicker;
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

        img = findViewById(R.id.modify_profile_img);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

            if(SqlLiteUserDao.create(this).update(bodyJson)){
                Preferences.Session.update(this);
                imageFileHelper.moveToImageDir(user.getImage().getName());
                if(oldUser.getImage()!=null && oldUser.getImage().exists()) oldUser.getImage().delete();
                imageFileHelper.deleteTmpDir();

                NetworkServiceHandler.getInstance(this,
                        NetworkHelper.Constant.UPDATE, NetworkHelper.Constant.USER,
                        bodyJson.toString()).bindService();

                setResult(RESULT_OK, new Intent().putExtra(KeysIntent.CHANGED_USER, user));
            }else{
                setResult(RESULT_CANCELED, new Intent());
            }
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        img.setOnClickListener( v ->{
            imagePicker = new ImagePicker(this,
                    null ,
                    imageUri -> {
                        String newName = ImageFileHelper.createNameRandom();
                        Log.e(TAG, "Name : "+ newName);
                        if(imageFileHelper.moveToTmpDir(imageUri, newName)){
                            File imageTmp = imageFileHelper.getImageTmp(newName);
                            imageFileHelper.load(img, imageTmp);
                            user.setImageProfile(imageTmp);

                            Log.e(TAG, "Name File user changed : "+ user.getImage());
                        }
                    }).setWithImageCrop(1,1);
            imagePicker.choosePicture(true);
        });

        gender.setOnClickListener(v ->{
            final ChooseDialog<Gender> genderDialog = new ChooseDialog<>(this, Gender.values(),
                    ((TextView) v).getText(),
                    (val, description) -> {
                        TextView gView = ((TextView) v);
                        gView.setText(val.getStrId());
                        gView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(val.getIconId()), null, null, null);
                        user.setGender(val);
                    },
                    () -> Arrays.stream(Gender.values())
                            .map(g -> getString(g.getStrId()))
                            .toArray(String[]::new)
            );
            genderDialog.setTitle(R.string.gender);
            genderDialog.create().show();
        });

        birthDate.setOnClickListener(v ->{
            final TextView d = ((TextView) v);
            DateTimePickerDialog.createDatePicker(this, (date, calendar) -> {
                d.setText(date);
                user.setBirthDate(calendar.getTime());
            }).show();
        });

        height.setOnClickListener(v ->{
            final TextView h = ((TextView) v);
            HeightDialog.create(this, h.getText().toString(),  (heightMeasure) -> {
                if(heightMeasure!=null){
                    h.setText(heightMeasure.toString());
                    user.getHeight().setValue(false, heightMeasure.getValue(false));
                }
            }).create().show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode,requestCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        imageFileHelper.deleteTmpDir();
    }
}
