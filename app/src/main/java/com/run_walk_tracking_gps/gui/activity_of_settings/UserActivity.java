package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.enumeration.MeasureUnit;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;
import com.run_walk_tracking_gps.utilities.ConversionUnitUtilities;

import org.json.JSONException;


public class UserActivity extends CommonActivity {

    private static final String TAG = UserActivity.class.getName();

    private static final int REQUEST_MODIFY_PROFILE = 11;

    private static String unit_height ;

    private ImageView img;
    private TextView name;
    private TextView lastName;
    private TextView gender;
    private TextView birthDate;
    private TextView email;
    private TextView city;
    private TextView tel;
    private TextView height;


    private User user;

    @Override
    protected void init() {
        setContentView(R.layout.activity_profile);
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

        if(getIntent()!=null){
            try {
                unit_height = Preferences.getUnitHeightDefault(this);

                setGui(getIntent().getParcelableExtra(getString(R.string.user_info)));
                getSupportActionBar().setTitle(user.getUsername());

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (NullPointerException n){
                Log.e(TAG, getString(R.string.user_not_set));
            }
        }

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
                //Toast.makeText(this, getString(R.string.modify), Toast.LENGTH_LONG).show();

                final Intent profileIntent = new Intent(this, ModifyUserActivity.class);
                profileIntent.putExtra(getString(R.string.profile), user);
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
                    setGui((User) data.getParcelableExtra(getString(R.string.changed_profile)));
                }

                break;
        }
    }

    @Override
    protected void listenerAction() {
    }

    private void setGui(User user){
        name.setText(user.getName());
        lastName.setText(user.getLastName());
        gender.setText(user.getGender().getStrId());
        gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(user.getGender().getIconId()),null, null, null);
        birthDate.setText(user.getBirthDateString());
        email.setText(user.getEmail());
        city.setText(user.getCity());
        tel.setText(user.getPhone());
        // TODO: 10/31/2019 CONTROLLO SE L'UNITA DI MISURA PREDEFINITA E' IL METRO ALTRIMENTI CONVERTO IL NUMERO s_height IN FT
        try {
            double height_value = user.getHeight();
            if(!Preferences.getUnitHeightDefault(this).equals(getString(MeasureUnit.METER.getStrId()))){
                height_value = ConversionUnitUtilities.meterToFeet(height_value);
            }
            height.setText(new StringBuilder(height_value +getString(R.string.space)+ unit_height));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String img_encode = Preferences.getSharedPreferencesImagesUser(this).getString(String.valueOf(user.getIdUser()), "");
        if(!img_encode.equals("null")) img.setImageBitmap(BitmapUtilities.StringToBitMap(img_encode));

        this.user = user;
        Log.d(TAG, "User = " + user);
    }

}
