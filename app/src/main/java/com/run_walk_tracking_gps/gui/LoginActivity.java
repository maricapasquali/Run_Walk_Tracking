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
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;
import com.run_walk_tracking_gps.utilities.DeviceUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends CommonActivity implements  Response.Listener<JSONObject> {

    private final static String TAG = LoginActivity.class.getName();

    private EditText username;
    private EditText password;
    private TextView forgotPassword;
    private Button login;

    private JSONObject bodyJson;

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
                    final String hash_password = CryptographicHashFunctions.md5(password.getText().toString());

                    bodyJson = new JSONObject().put(HttpRequest.Constant.USERNAME, username.getText().toString())
                                               .put(HttpRequest.Constant.PASSWORD, hash_password)
                                               .put(HttpRequest.Constant.IMEI, DeviceUtilities.getIdDevice(this));

                    HttpRequest.requestSignIn(this, bodyJson, this);

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                } catch (InternetNoAvailableException e) {
                    e.alert();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HttpRequest.cancelAllRequestPending(bodyJson);
    }

    @Override
    public void onResponse(JSONObject response) {
        if(SplashScreenActivity.dataAccessResponse(this, response)) finishAffinity();
    }

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
