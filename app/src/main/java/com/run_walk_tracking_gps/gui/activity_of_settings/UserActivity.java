package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.db.dao.SqlLiteUserDao;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.gui.components.dialog.ZoomImageDialog;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.builder.UserBuilder;

import com.run_walk_tracking_gps.utilities.ImageFileHelper;

import org.json.JSONException;

import androidx.appcompat.app.AlertDialog;

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
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile);

        img = findViewById(R.id.profile_img);

        name = findViewById(R.id.profile_name);
        lastName = findViewById(R.id.profile_lastname);
        gender = findViewById(R.id.profile_gender);
        birthDate = findViewById(R.id.profile_birth_date);
        email = findViewById(R.id.profile_email);
        city = findViewById(R.id.profile_city);
        tel = findViewById(R.id.profile_tel);
        height = findViewById(R.id.profile_height);

        try {
            setGui(UserBuilder.create(this, SqlLiteUserDao.create(this).getUser()).build());
        } catch (JSONException e) {
            e.printStackTrace();
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
                new AlertDialog.Builder(this)
                               .setMessage(R.string.delete_account_mex)
                               .setPositiveButton(R.string.delete, (dialog, id) ->
                                       NetworkHelper.HttpRequest.request(this, NetworkHelper.Constant.DELETE_ACCOUNT, null))
                               .setNegativeButton(R.string.cancel, null)
                               .create()
                               .show();
            }
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_MODIFY_PROFILE:
                if(resultCode== Activity.RESULT_OK){
                    User user = (User) data.getParcelableExtra(KeysIntent.CHANGED_USER);
                    user.setContext(this);
                    setGui(user);
                } else if(resultCode== Activity.RESULT_CANCELED){
                    Log.e(TAG, "USER NON MODIFICATO");
                }
                break;
        }
    }

    @Override
    protected void listenerAction() {
    }

    private void setGui(User user){
        if(user!=null){

            name.setText(user.getName());
            lastName.setText(user.getLastName());
            gender.setText(user.getGender().getStrId());
            gender.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(user.getGender().getIconId()),
                    null, null, null);
            birthDate.setText(user.getBirthDateString());
            email.setText(user.getEmail());
            city.setText(user.getCity());
            tel.setText(user.getPhone());
            height.setText(user.getHeight().toString());

            if(user.getImage()!=null && user.getImage().exists())
                ImageFileHelper.create(this).load(img, user.getImage());

            this.user = user;
            Log.d(TAG, "User = " + user);
        }
    }


}
