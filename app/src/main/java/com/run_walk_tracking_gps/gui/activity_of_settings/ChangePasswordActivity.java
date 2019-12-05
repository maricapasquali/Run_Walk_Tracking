package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.exception.PasswordNotCorrectException;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends CommonActivity implements Response.Listener<JSONObject>{

    private static final String TAG = ChangePasswordActivity.class.getName();
    private EditText password;
    private EditText conf_password;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle(R.string.change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        password = findViewById(R.id.password);
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
                        final String hash_password = CryptographicHashFunctions.md5(password.getText().toString());
                        final String hash_conf_password = CryptographicHashFunctions.md5(conf_password.getText().toString());

                        if(!hash_password.equals(hash_conf_password)) throw new PasswordNotCorrectException(this);

                        saveChangedPassword(CryptographicHashFunctions.md5(password.getText().toString()));

                    }catch (PasswordNotCorrectException e){
                        e.alert();
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
        if(TextUtils.isEmpty(password.getText())){
            isCheck = false;
            password.setError(getString(R.string.password_not_empty));
        }
        if(TextUtils.isEmpty(conf_password.getText())){
            isCheck = false;
            conf_password.setError(getString(R.string.confirm_password_not_empty));
        }
        return isCheck;
    }

    private void saveChangedPassword(final String hash){
        try {
            int id_user = Integer.valueOf(DefaultPreferencesUser.getIdUserLogged(this));
            final JSONObject bodyJson = new JSONObject().put(HttpRequest.Constant.ID_USER, id_user)
                    .put(HttpRequest.Constant.PASSWORD, hash);

            Log.e(TAG, bodyJson.toString());

            HttpRequest.requestUpdatePassword(this, bodyJson,this);

        }catch (JSONException json){
            json.printStackTrace();
        } catch (InternetNoAvailableException e) {
            e.alert();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        super.onBackPressed();
    }
}
