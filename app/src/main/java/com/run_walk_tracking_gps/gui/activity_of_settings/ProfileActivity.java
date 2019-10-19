package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.model.Profile;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.DateUtilities;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

import java.text.ParseException;

public class ProfileActivity extends CommonActivity {

    private static final String TAG = ProfileActivity.class.getName();

    private static final int REQUEST_MODIFY_PROFILE = 11;

    private ImageView img;
    private TextView name;
    private TextView lastName;
    private TextView gender;
    private TextView birthDate;
    private TextView email;
    private TextView city;
    private TextView tel;
    private TextView height;


    private Profile profile;
    @Override
    protected void initGui() {
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("MarioRossi$12");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img = findViewById(R.id.profile_img);
        name = findViewById(R.id.profile_name);
        lastName = findViewById(R.id.profile_lastname);
        gender = findViewById(R.id.profile_gender);
        birthDate = findViewById(R.id.profile_birth_date);
        email = findViewById(R.id.profile_email);
        city = findViewById(R.id.profile_city);
        tel = findViewById(R.id.profile_tel);
        height = findViewById(R.id.profile_height);

        // TODO: 10/17/2019  RICHIESTA AL DATABASE (anche username)

        try {
            profile = Profile.create("Mario", "Rossi", Gender.MALE,
                    DateUtilities.parseShortToDate("03/05/1997"),
                                     "mario@gmail.com", "Roma", "1.70 m");
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        profile.setImg(Uri.EMPTY);
        profile.setTel("3333333333");

        name.setText(profile.getName());
        lastName.setText(profile.getLastName());
        gender.setText(profile.getGender().getStrId());
        gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(profile.getGender().getIconId()),
                null, null, null);
        birthDate.setText(profile.getBirthDateString());
        email.setText(profile.getEmail());
        city.setText(profile.getCity());
        tel.setText(profile.getTel());
        height.setText(profile.getHeight());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_modify:{
                Toast.makeText(this, getString(R.string.modify), Toast.LENGTH_LONG).show();

                final Intent profileIntent = new Intent(this, ModifyProfileActivity.class);
                profileIntent.putExtra(getString(R.string.profile), profile);
                startActivityForResult(profileIntent, REQUEST_MODIFY_PROFILE);
            }
            break;

            case R.id.profile_change_password:
                Toast.makeText(this, getString(R.string.change_password), Toast.LENGTH_LONG).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_MODIFY_PROFILE:
                if(resultCode== Activity.RESULT_OK){
                    final Profile newProfile = (Profile)data.getParcelableExtra(getString(R.string.changed_profile));
                    modifyFieldsChanged(profile, newProfile);
                    profile = newProfile;
                    Log.d(TAG, "Change Profile = " +newProfile);
                }
                break;
        }
    }

    private void modifyFieldsChanged(Profile oldP, Profile newP){
        if(!oldP.getImg().equals(newP.getImg())){
            img.setImageURI(newP.getImg());

        }

        if(!oldP.getName().equals(newP.getName())){
            name.setText(newP.getName());
        }
        if(!oldP.getLastName().equals(newP.getLastName())){
            lastName.setText(newP.getLastName());
        }
        if(!oldP.getGender().equals(newP.getGender())){
            gender.setText(newP.getGender().getStrId());
            gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(newP.getGender().getIconId()),
                    null, null, null);
        }
        if(!oldP.getBirthDate().equals(newP.getBirthDate())){
            birthDate.setText(newP.getBirthDateString());
        }
        if(!oldP.getEmail().equals(newP.getEmail())){
            email.setText(newP.getEmail());
        }
        if(!oldP.getCity().equals(newP.getCity())){
            city.setText(newP.getCity());
        }
        if(!oldP.getTel().equals(newP.getTel())){
            tel.setText(newP.getTel());
        }
        if(!oldP.getHeight().equals(newP.getHeight())){
            height.setText(newP.getHeight());
        }

    }

    @Override
    protected void listenerAction() {
    }
}
