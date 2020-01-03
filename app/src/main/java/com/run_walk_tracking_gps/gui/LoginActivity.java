package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends CommonActivity
        //implements  Response.Listener<JSONObject>
{

    private final static String TAG = LoginActivity.class.getName();

    private EditText username;
    private EditText password;
    private TextView forgotPassword;
    private Button login;

    private JSONObject bodyJson;
    private String hash_password;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(getString(R.string.login));

        username = findViewById(R.id.access_username);
        password = findViewById(R.id.access_password);
        forgotPassword = findViewById(R.id.forgot_password);
        login = findViewById(R.id.login);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void listenerAction() {
        forgotPassword.setOnClickListener(v ->{
            //Toast.makeText(this,getString(R.string.forgot_password) , Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(this, ForgotPassword.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        login.setOnClickListener(v ->{
            if(check(username.getText(), password.getText())){
                try {
                    hash_password = CryptographicHashFunctions.md5(password.getText().toString());

                    bodyJson = new JSONObject().put(NetworkHelper.Constant.USERNAME, username.getText().toString())
                                               .put(NetworkHelper.Constant.PASSWORD, hash_password);

                    NetworkHelper.HttpRequest.request(this, NetworkHelper.Constant.SIGN_IN, bodyJson);

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NetworkHelper.HttpRequest.cancelAllRequestPending(bodyJson);
    }
    /*
    @Override
    public void onResponse(JSONObject response) {
        try {
            if(response.has(NetworkHelper.Constant.FIRST_LOGIN) && response.getBoolean(NetworkHelper.Constant.FIRST_LOGIN)){
                final Intent intent = new Intent(this, TokenActivity.class);
                intent.putExtra(KeysIntent.USERNAME, username.getText().toString());
                intent.putExtra(KeysIntent.PASSWORD, hash_password);
                startActivity(intent);
                finish();
            }else if(response.has(NetworkHelper.Constant.SESSION)){
                // SESSION TO SHAREDPREFERENCE

                DefaultPreferencesUser.setSession(this,
                        response.getJSONObject(NetworkHelper.Constant.SESSION).put(NetworkHelper.Constant.LAST_UPDATE, 0));

                // REQUEST SYNC
                NetworkHelper.HttpRequest.getInstance(this).sync(new OnUpdateGuiListener() {
                    @Override
                    public void onChangeStateDB() {
                        final Intent intent = new Intent(LoginActivity.this, ApplicationActivity.class);
                        startActivity(intent);
                        finish();
                    }


                    @Override
                    public void onConsistentInternalDB() {
                    }

                    @Override
                    public void onNoConsistentInternalDB() {
                        final Intent intent = new Intent(LoginActivity.this, ApplicationActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNoConsistentServerDB() {
                    }

                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (InternetNoAvailableException e) {
            e.alert();
        }
    }
*/
    private boolean check(Editable user, Editable pass){
        boolean isOk = true;
        if(TextUtils.isEmpty(user)){
            username.setError(getString(R.string.username_not_empty));
            isOk = false;
        }
        if(TextUtils.isEmpty(pass)){
            password.setError(getString(R.string.password_not_empty));
            isOk = false;
        }
        return isOk;
    }

}
