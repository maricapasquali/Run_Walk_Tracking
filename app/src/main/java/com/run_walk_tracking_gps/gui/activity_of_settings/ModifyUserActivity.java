package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.gui.components.dialog.RequestDialog;
import com.run_walk_tracking_gps.intent.KeysIntent;
import com.run_walk_tracking_gps.task.CompressionBitMap;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.DefaultPreferencesUser;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.components.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.HeightDialog;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class ModifyUserActivity extends CommonActivity implements Response.Listener<JSONObject>{

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
    private String image_encode = null;

    private CompressionBitMap async =  null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modify_profile);
        getSupportActionBar().setTitle(R.string.modify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img = findViewById(R.id.modify_profile_img);
        name = findViewById(R.id.modify_profile_name);
        lastName = findViewById(R.id.modify_profile_lastname);
        email = findViewById(R.id.modify_profile_email);
        city = findViewById(R.id.modify_profile_city);
        tel = findViewById(R.id.modify_profile_tel);
        gender = findViewById(R.id.modify_profile_gender);
        birthDate = findViewById(R.id.modify_profile_birth_date);
        height = findViewById(R.id.modify_profile_height);
// TODO: 11/27/2019  CHECK SE EDITTEXT SONO VUOTI
        if(getIntent()!=null){
            oldUser = (User)getIntent().getParcelableExtra(KeysIntent.PROFILE);
            oldUser.setContext(this);
            Log.d(TAG, oldUser.toString());

            String img_encode = DefaultPreferencesUser.getSharedPreferencesImagesUser(this).getString(String.valueOf(oldUser.getIdUser()), null);
            if(img_encode!=null)img.setImageBitmap(BitmapUtilities.StringToBitMap(img_encode));

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

    private void saveUserChanged(RequestDialog dialog){
        try {

            final JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_USER.toName(), user.getIdUser());
            if(!user.getName().equals(oldUser.getName())){
                bodyJson.put(FieldDataBase.NAME.toName(), user.getName());
            }
            if(!user.getLastName().equals(oldUser.getLastName())){
                bodyJson.put(FieldDataBase.LAST_NAME.toName(), user.getLastName());
            }
            if(!user.getGender().equals(oldUser.getGender())){
                bodyJson.put(FieldDataBase.GENDER.toName(), user.getGender());
            }
            if(!user.getBirthDate().equals(oldUser.getBirthDate())){
                bodyJson.put(FieldDataBase.BIRTH_DATE.toName(), user.getBirthDate());
            }
            if(!user.getEmail().equals(oldUser.getEmail())){
                bodyJson.put(FieldDataBase.EMAIL.toName(), user.getEmail());
            }
            if(!user.getCity().equals(oldUser.getCity())){
                bodyJson.put(FieldDataBase.CITY.toName(), user.getCity());
            }
            if(!user.getPhone().equals(oldUser.getPhone())){
                bodyJson.put(FieldDataBase.PHONE.toName(), user.getPhone());
            }
            if(!user.getHeight().getValue().equals(oldUser.getHeight().getValue())){
                bodyJson.put(FieldDataBase.HEIGHT.toName(), user.getHeight().getValue());
            }

            if(async!=null){
                this.image_encode = async.get();
                if(image_encode!=null)
                    bodyJson.put(FieldDataBase.IMG_ENCODE.toName(), image_encode);
            }

            Log.d(TAG, bodyJson.toString());

            if(!HttpRequest.requestDelayedUpdateUserInformation(this, bodyJson,this, dialog)){
                Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile:{
                Log.e(TAG, "SALVA MODIFICHE");
                RequestDialog dialog = RequestDialog.create(ModifyUserActivity.this);
                dialog.show();
                Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
                user.setName(name.getText().toString());
                user.setLastName(lastName.getText().toString());
                user.setEmail(email.getText().toString());
                user.setCity(city.getText().toString());
                user.setPhone(tel.getText().toString());

                // REQUEST UPDATE USER
                saveUserChanged(dialog);

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
                        img.setImageURI(imageUri);
                        async = CompressionBitMap.create();
                        async.execute(((BitmapDrawable) img.getDrawable()).getBitmap());

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
            DateTimePickerDialog.create(this, d.getText().toString() , (date, calendar) -> {
                d.setText(date);
                user.setBirthDate(calendar.getTime());
            }, false).show();
        });

        height.setOnClickListener(v ->{
            final TextView h = ((TextView) v);
            HeightDialog.create(this, h.getText().toString(),  (heightMeasure) -> {
                if(heightMeasure!=null){
                    h.setText(heightMeasure.toString());
                    user.getHeight().setValueFromGui(heightMeasure.getValueToGui());
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

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(HttpRequest.someError(response) || !(boolean)response.get("update")){
                Snackbar.make(findViewById(R.id.snake), response.toString(), Snackbar.LENGTH_LONG).show();
            }else {
                DefaultPreferencesUser.setImage(this, user.getIdUser(), image_encode);

                final Intent returnIntent = new Intent();
                returnIntent.putExtra(KeysIntent.CHANGED_USER, user);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, e.getMessage());
        }
    }
}
