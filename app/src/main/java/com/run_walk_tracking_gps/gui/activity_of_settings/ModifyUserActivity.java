package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.os.Build;
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
import com.google.gson.Gson;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.task.CompressionBitMap;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.dialog.HeightDialog;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;
import com.run_walk_tracking_gps.utilities.ConversionUnitUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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
    private User user;

    private String image_encode;

    private boolean isMeterUnit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void init() {
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

        if(getIntent()!=null){
            try {
                user = (User)getIntent().getParcelableExtra(getString(R.string.profile));
                Log.d(TAG, user.toString());


                String img_encode = Preferences.getSharedPreferencesImagesUser(this).getString(String.valueOf(user.getIdUser()), null);
                if(img_encode!=null)img.setImageBitmap(BitmapUtilities.StringToBitMap(img_encode));

                name.setText(user.getName());
                lastName.setText(user.getLastName());
                gender.setText(user.getGender().getStrId());
                gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(user.getGender().getIconId()), null, null, null);
                birthDate.setText(user.getBirthDateString());
                email.setText(user.getEmail());
                city.setText(user.getCity());
                tel.setText(user.getPhone());

                // TODO: 11/3/2019 GESTIONE CONVERSIONE
                String unit_height = Preferences.getUnitHeightDefault(this);
                try {
                    double height_value = user.getHeight();
                    isMeterUnit = Preferences.getUnitHeightDefault(this).equals(
                            getString(Measure.Unit.METER.getStrId()));
                    if(!isMeterUnit){
                        height_value = ConversionUnitUtilities.meterToFeet(height_value);
                    }
                    height.setText(new StringBuilder(height_value +getString(R.string.space)+ unit_height));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException n){
                Log.e(TAG, getString(R.string.user_not_set));

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile:{
                Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
                user.setName(name.getText().toString());
                user.setLastName(lastName.getText().toString());
                user.setEmail(email.getText().toString());
                user.setCity(city.getText().toString());
                user.setPhone(tel.getText().toString());

                // REQUEST UPDATE USER
                try {
                    JSONObject bodyJson = new JSONObject(new Gson().toJson(user));
                    bodyJson.put(FieldDataBase.IMG_ENCODE.toName(), image_encode);
                    bodyJson.remove(FieldDataBase.USERNAME.toName());

                    Log.e(TAG, bodyJson.toString());
                    if(!HttpRequest.requestUpdateUserInformation(this, bodyJson,this)){
                        Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }

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
                        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();

                        CompressionBitMap.create(image_encode -> {
                            this.image_encode = image_encode;
                            Log.e(TAG, image_encode);
                        }).execute(bitmap);

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
                h .setText(heightMeasure.toString(this));
                // Conversione del valore 'height' se != METER
                user.setHeight(isMeterUnit? heightMeasure.getValue() : ConversionUnitUtilities.feetToMeter(heightMeasure.getValue()));
                
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
                Preferences.writeImageIntoSharedPreferences(this, user.getIdUser(), image_encode);

                final Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.changed_profile), user);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
