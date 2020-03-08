package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.exception.PasswordNotCorrectException;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;

public class ChangePasswordActivity extends CommonActivity {

    private static final String TAG = ChangePasswordActivity.class.getName();
    private TextInputEditText username;
    private TextInputEditText old_password;
    private TextInputEditText new_password;
    private TextInputEditText conf_password;

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

    private void setError(TextInputEditText view, int error){
        ((TextInputLayout) view.getParent().getParent()).setError(this.getString(error));
    }

    private void setError(TextInputEditText view, String error){
        ((TextInputLayout) view.getParent().getParent()).setError(error);
    }
    @Override
    protected void listenerAction() {
        Stream.of(username, old_password, new_password, conf_password).forEach(edit ->{
            edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(edit.equals(username)) setError(username, s.length()<=0 ? getString(R.string.username_not_empty): null);
                    if(edit.equals(old_password))setError(old_password, s.length()<=0 ? getString(R.string.password_not_empty): null);
                    if(edit.equals(new_password)) setError(new_password, s.length()<=0 ? getString(R.string.password_not_empty): null);
                    if(edit.equals(conf_password)) {
                        if(s.length()<=0)
                            setError(conf_password, R.string.confirm_password_not_empty);
                        else if((edit.getText().length() > new_password.getText().length() &&
                                !edit.getText().toString().equals(new_password.getText().toString())) ||
                                (edit.getText().length() == new_password.getText().length() &&
                                        !edit.getText().toString().equals(new_password.getText().toString()))
                        ){
                            setError(conf_password, R.string.not_correct_password);
                        }else{
                            setError(conf_password, null);
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


        });

    }
    private boolean check(){
        boolean isCheck = true;
        if(TextUtils.isEmpty(username.getText())){
            isCheck = false;
            setError(username, R.string.username_not_empty);
            //username.setError(getString(R.string.username_not_empty));
        }
        if(TextUtils.isEmpty(old_password.getText())){
            isCheck = false;
            setError(old_password, R.string.password_not_empty);
            //old_password.setError(getString(R.string.password_not_empty));
        }
        if(TextUtils.isEmpty(new_password.getText())){
            isCheck = false;
            setError(new_password, R.string.password_not_empty);
            //new_password.setError(getString(R.string.password_not_empty));
        }
        if(TextUtils.isEmpty(conf_password.getText())){
            isCheck = false;
            setError(conf_password, R.string.confirm_password_not_empty);
            //conf_password.setError(getString(R.string.confirm_password_not_empty));
        }
        return isCheck;
    }


    private boolean validation(){
        TextInputLayout userL = ((TextInputLayout) username.getParent().getParent());
        TextInputLayout oldPassL = ((TextInputLayout) old_password.getParent().getParent());
        TextInputLayout newPassL = ((TextInputLayout) new_password.getParent().getParent());
        TextInputLayout confPasserL = ((TextInputLayout) conf_password.getParent().getParent());

        return check() && (userL.getError()==null && oldPassL.getError()==null && newPassL.getError()==null && confPasserL.getError()==null);
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

                if(validation()){
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

    private void saveChangedPassword(final String new_hash){
        try {
            final JSONObject bodyJson = new JSONObject()
                    .put(NetworkHelper.Constant.TOKEN,
                            Preferences.Session.getSession(this).getString(NetworkHelper.Constant.TOKEN))
                    .put(NetworkHelper.Constant.USERNAME, username.getText())
                    .put(NetworkHelper.Constant.OLD_PASSWORD, CryptographicHashFunctions.md5(old_password.getText().toString()))
                    .put(NetworkHelper.Constant.NEW_PASSWORD, new_hash);
            Log.e(TAG, bodyJson.toString());

            NetworkHelper.HttpRequest.request(ChangePasswordActivity.this, NetworkHelper.Constant.CHANGE_PASSWORD, bodyJson);

        }catch (JSONException json){
            json.printStackTrace();
        }
    }
}
