package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.content.Intent;

import android.net.Uri;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.dialog.HeightDialog;
import com.run_walk_tracking_gps.model.Profile;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import java.util.Arrays;

public class ModifyProfileActivity extends CommonActivity {
    private static final String TAG =ModifyProfileActivity.class.getName();

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

    private Profile profile;
    @Override
    protected void initGui() {
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
            profile = (Profile)getIntent().getParcelableExtra(getString(R.string.profile));
            Log.d(TAG, profile.toString());

            if(!profile.getImg().equals(Uri.EMPTY)) {
                img.setImageURI(profile.getImg());
            }
            name.setText(profile.getName());
            lastName.setText(profile.getLastName());
            gender.setText(profile.getGender().getStrId());
            gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(profile.getGender().getIconId()), null, null, null);
            birthDate.setText(profile.getBirthDateString());
            email.setText(profile.getEmail());
            city.setText(profile.getCity());
            tel.setText(profile.getTel());

            height.setText(profile.getHeight());
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
                profile.setName(name.getText().toString());
                profile.setLastName(lastName.getText().toString());
                profile.setEmail(email.getText().toString());
                profile.setCity(city.getText().toString());
                profile.setTel(tel.getText().toString());

                final Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.changed_profile), profile);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void listenerAction() {

        img.setOnClickListener( v ->{
            imagePicker = new ImagePicker(this /*activity non null*/,
                    null /*fragment nullable*/,
                    imageUri -> { /*on image picked*/
                        profile.setImg(imageUri);

                        img.setImageURI(imageUri);
                        Log.d(TAG, profile.toString());
                        Toast.makeText(this, profile.toString(), Toast.LENGTH_LONG).show();

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
                        profile.setGender(val);


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
                profile.setBirthDate(calendar.getTime());
            }, true).show();
        });

        height.setOnClickListener(v ->{
            final TextView h = ((TextView) v);
            HeightDialog.create(this, h.getText().toString(),  height -> {
                h .setText(height);
                profile.setHeight(height);
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
}
