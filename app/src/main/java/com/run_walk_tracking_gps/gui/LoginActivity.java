package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.utilities.AppUtilities;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;

import androidx.annotation.RequiresApi;

public class LoginActivity extends CommonActivity
        //implements  Response.Listener<JSONObject>
{

    private final static String TAG = LoginActivity.class.getName();

    private TextInputEditText username;
    private TextInputEditText password;
    private TextView forgotPassword;
    private MaterialButton login;

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

        Stream.of(username, password).forEach(editText -> {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String error = null;
                    if(editText.equals(username) && s.length()<=0)
                        error = getString(R.string.username_not_empty);
                    if(editText.equals(password) && s.length()<=0)
                        error = getString(R.string.password_not_empty);
                    ((TextInputLayout) editText.getParent().getParent()).setError(error);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        });

        forgotPassword.setOnClickListener(v -> {
            final Intent intent = new Intent(this, ForgotPassword.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
            if(check(username.getText(), password.getText())){
                try {
                    hash_password = CryptographicHashFunctions.md5(password.getText().toString());

                    bodyJson = new JSONObject().put(NetworkHelper.Constant.USERNAME, username.getText().toString())
                                               .put(NetworkHelper.Constant.PASSWORD, hash_password)
                                               .put(NetworkHelper.Constant.DEVICE, AppUtilities.id(this));

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

    private boolean check(Editable user, Editable pass){
        boolean isOk = true;
        if(TextUtils.isEmpty(user)){
            ((TextInputLayout) username.getParent().getParent()).setError(getString(R.string.username_not_empty));
            isOk = false;
        }
        if(TextUtils.isEmpty(pass)){
            ((TextInputLayout) password.getParent().getParent()).setError(getString(R.string.password_not_empty));
            isOk = false;
        }
        return isOk;
    }

}
