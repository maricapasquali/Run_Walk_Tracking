package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.controller.UserSingleton;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.gui.BootAppActivity;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.gui.components.dialog.ZoomImageDialog;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void init(Bundle savedInstanceState) {

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
            user = UserSingleton.getInstance().getUser();
            if(user!=null){
                setGui(user);
                getSupportActionBar().setTitle(user.getUsername());
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
                //Toast.makeText(this, R.string.modify, Toast.LENGTH_LONG).show();
                Log.d(TAG, getString(R.string.modify));
                final Intent profileIntent = new Intent(this, ModifyUserActivity.class);
                profileIntent.putExtra(KeysIntent.PROFILE, user);
                startActivityForResult(profileIntent, REQUEST_MODIFY_PROFILE);
            }
            break;
            case R.id.profile_change_password:
                //Toast.makeText(this, R.string.change_password, Toast.LENGTH_LONG).show();
                Log.d(TAG, getString(R.string.change_password));
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.delete_account: {
                Log.d(TAG, getString(R.string.delete_account));
                //Toast.makeText(this, R.string.delete_account, Toast.LENGTH_LONG).show();
                try {
                    final JSONObject bodyJson = new JSONObject().put(HttpRequest.Constant.ID_USER,  user.getIdUser());

                    new AlertDialog.Builder(this)
                            .setMessage(R.string.delete_account_mex)
                            .setPositiveButton(R.string.delete, (dialog, id) -> {
                                try {
                                    HttpRequest.requestDeleteUser(this, bodyJson, response -> {
                                        // CANCELLAZIONE PREFERENCES
                                        DefaultPreferencesUser.deleteUser(this, String.valueOf(user.getIdUser()));
                                        //EXIT
                                        DefaultPreferencesUser.unSetUserLogged(this);

                                        final Intent intent = new Intent(this, BootAppActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    });
                                } catch (InternetNoAvailableException e) {
                                    e.alert();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create()
                            .show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_MODIFY_PROFILE:
                if(resultCode== Activity.RESULT_OK){
                    setGui((User) data.getParcelableExtra(KeysIntent.CHANGED_USER));
                }
                break;
        }
    }

    @Override
    protected void listenerAction() {

        findViewById(R.id.profile_img).setOnClickListener(v ->{
            ImageView i = (ImageView)v;
            ZoomImageDialog.create(this, ((BitmapDrawable) i.getDrawable()).getBitmap()).show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setGui(User user){
        user.setContext(this);
        name.setText(user.getName());
        lastName.setText(user.getLastName());
        gender.setText(user.getGender().getStrId());
        gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(user.getGender().getIconId()),null, null, null);
        birthDate.setText(user.getBirthDateString());
        email.setText(user.getEmail());
        city.setText(user.getCity());
        tel.setText(user.getPhone());

        height.setText(user.getHeight().toString());

        try{
            String img_encode = DefaultPreferencesUser.getSharedPreferencesImagesUser(this).getString(String.valueOf(user.getIdUser()),null);
            Log.e(TAG, "IMAGE ENCODE = " + img_encode);
            if(img_encode!=null)img.setImageBitmap(BitmapUtilities.StringToBitMap(img_encode));
        }catch (IOException e){
            img.setImageResource(R.drawable.ic_user_empty);
        }

        this.user = user;
        Log.d(TAG, "User = " + user);
    }

}
