package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.exception.PasswordNotCorrectException;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends CommonActivity
        //implements Response.Listener<JSONObject>
{

    private static final String TAG = ChangePasswordActivity.class.getName();
    private EditText username;
    private EditText old_password;
    private EditText new_password;
    private EditText conf_password;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle(R.string.change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.password);
        conf_password = findViewById(R.id.conf_password);
    }

    @Override
    protected void listenerAction() {
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
                //Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();

                if(check()){
                    try{
                        final String hash_new_password = CryptographicHashFunctions.md5(new_password.getText().toString());
                        final String hash_conf_password = CryptographicHashFunctions.md5(conf_password.getText().toString());

                        if(!hash_new_password.equals(hash_conf_password)) throw new PasswordNotCorrectException(this);

                        saveChangedPassword(hash_new_password);

                    }catch (PasswordNotCorrectException e){
                        conf_password.setError(e.getMessage());
                        conf_password.setText("");
                        //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean check(){
        boolean isCheck = true;
        if(TextUtils.isEmpty(username.getText())){
            isCheck = false;
            username.setError(getString(R.string.username_not_empty));
        }
        if(TextUtils.isEmpty(old_password.getText())){
            isCheck = false;
            old_password.setError(getString(R.string.password_not_empty));
        }
        if(TextUtils.isEmpty(new_password.getText())){
            isCheck = false;
            new_password.setError(getString(R.string.password_not_empty));
        }
        if(TextUtils.isEmpty(conf_password.getText())){
            isCheck = false;
            conf_password.setError(getString(R.string.confirm_password_not_empty));
        }
        return isCheck;
    }

    private void saveChangedPassword(final String new_hash){
        try {
            final JSONObject bodyJson = new JSONObject()
                    .put(NetworkHelper.Constant.TOKEN,
                            DefaultPreferencesUser.getSession(this).getString(NetworkHelper.Constant.TOKEN))
                    .put(NetworkHelper.Constant.USERNAME, username.getText())
                    .put(NetworkHelper.Constant.OLD_PASSWORD, CryptographicHashFunctions.md5(old_password.getText().toString()))
                    .put(NetworkHelper.Constant.NEW_PASSWORD, new_hash);
            Log.e(TAG, bodyJson.toString());

            NetworkHelper.HttpRequest.request(this, NetworkHelper.Constant.CHANGE_PASSWORD, bodyJson);

        }catch (JSONException json){
            json.printStackTrace();
        }
    }

    /*@Override
    public void onResponse(JSONObject response) {
        super.onBackPressed();
    }*/
}
