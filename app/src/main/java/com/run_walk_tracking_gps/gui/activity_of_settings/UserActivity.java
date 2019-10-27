package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;
import com.run_walk_tracking_gps.utilities.DateUtilities;
import com.run_walk_tracking_gps.model.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Iterator;

public class UserActivity extends CommonActivity {

    private static final String TAG = UserActivity.class.getName();

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


    private User user;

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
        /*if(!HttpRequest.requestJsonGetToServerVolley(this, HttpRequest.USER + "68", null,
                response -> {
                    Iterator<String> it = response.keys();
                    if(it.hasNext() && it.next().equals(HttpRequest.ERROR))
                        Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
                    else {
                        try {
                            Log.d(TAG, response.get("user").toString());
                            JSONArray responseArray = (JSONArray) response.get("user");
                            String imageString = (String) ((JSONObject)responseArray.get(1)).get("img_encode");
                            Log.e(TAG, imageString);
                            Bitmap bitmap = BitmapUtilities.StringToBitMap(imageString);
                            img.setImageBitmap(bitmap);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })){
            Toast.makeText(this, getString(R.string.internet_not_available), Toast.LENGTH_LONG).show();
        }*/


        try {
            user = User.create("Mario", "Rossi", Gender.MALE,
                    DateUtilities.parseShortToDate("03/05/1997"),
                                     "mario@gmail.com", "Roma", "1.70 m");
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        user.setImg(Uri.EMPTY);
        user.setTel("3333333333");

        name.setText(user.getName());
        lastName.setText(user.getLastName());
        gender.setText(user.getGender().getStrId());
        gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(user.getGender().getIconId()),
                null, null, null);
        birthDate.setText(user.getBirthDateString());
        email.setText(user.getEmail());
        city.setText(user.getCity());
        tel.setText(user.getTel());
        height.setText(user.getHeight());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    final User newUser = (User)data.getParcelableExtra(getString(R.string.changed_profile));
                    modifyFieldsChanged(user, newUser);
                    user = newUser;
                    Log.d(TAG, "Change User = " + newUser);
                }
                break;
        }
    }

    private void modifyFieldsChanged(User oldP, User newP){
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
