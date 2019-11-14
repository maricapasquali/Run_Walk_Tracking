package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;

public class LoginActivity extends CommonActivity implements  Response.Listener<JSONObject> {

    private final static String TAG = LoginActivity.class.getName();

    private EditText username;
    private EditText password;
    private TextView forgotPassword;
    private Button login;

    @Override
    protected void init() {
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(getString(R.string.login));

        username = findViewById(R.id.access_username);
        password = findViewById(R.id.access_password);
        forgotPassword = findViewById(R.id.forgot_password);
        login = findViewById(R.id.login);
    }

    @Override
    protected void listenerAction() {

        forgotPassword.setOnClickListener(v ->{
            Toast.makeText(this,getString(R.string.forgot_password) , Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(this, ForgotPassword.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        login.setOnClickListener(v ->{
            if(check(username.getText(), password.getText())){
                try {
                    final String hash_password = CryptographicHashFunctions.md5(password.getText().toString());

                    final JSONObject bodyJson = new JSONObject().put(FieldDataBase.USERNAME.toName(), username.getText().toString())
                                                                .put(FieldDataBase.PASSWORD.toName(), hash_password);

                    if(!HttpRequest.requestSignIn(this, bodyJson, this)){
                        Snackbar.make(findViewById(R.id.snake), R.string.internet_not_available, Snackbar.LENGTH_INDEFINITE).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(HttpRequest.someError(response)){
                Snackbar.make(findViewById(R.id.snake), response.get(HttpRequest.ERROR).toString(), Snackbar.LENGTH_LONG).show();
            }else {

                Intent intent;
                if(Stream.of(response.keys()).anyMatch(i -> i.next().equals(FieldDataBase.FIRST_LOGIN.toName()))){
                    Snackbar.make(findViewById(R.id.snake), getString(R.string.account_not_checked), Snackbar.LENGTH_LONG).show();
                    intent = new Intent(this, TokenActivity.class);
                    intent.putExtra(FieldDataBase.ID_USER.toName(), response.getInt(FieldDataBase.ID_USER.toName()));
                } else {
                    Preferences.writeImageIntoSharedPreferences(this, response);
                    Preferences.writeSettingsIntoSharedPreferences(this, response);
                    intent = new Intent(this, ApplicationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private boolean check(Editable user, Editable pass){
        if(TextUtils.isEmpty(user) && TextUtils.isEmpty(pass)){
            username.setError(getString(R.string.username)+ getString(R.string.not_empty));
            password.setError(getString(R.string.password)+ getString(R.string.not_empty));
            return false;
        }
        if(TextUtils.isEmpty(user)){
            username.setError(getString(R.string.username)+ getString(R.string.not_empty));
            return false;
        }
        if(TextUtils.isEmpty(pass)){
            password.setError(getString(R.string.password)+ getString(R.string.not_empty));
            return false;
        }
        return true;
    }

}
