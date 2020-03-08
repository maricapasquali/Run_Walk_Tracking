package com.run_walk_tracking_gps.gui;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.utilities.AppUtilities;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;

public class ActivationAccountActivity extends CommonActivity {

    private static final String TAG = ActivationAccountActivity.class.getName();

    private MaterialButton access;
    private TextInputEditText token;
    private String username;
    private String password;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_token);
        getSupportActionBar().setTitle(R.string.activation_account);
        access = findViewById(R.id.access_login_token);
        token = findViewById(R.id.access_token);

        if(getIntent()!=null){
            username = getIntent().getStringExtra(KeysIntent.USERNAME);
            password = getIntent().getStringExtra(KeysIntent.PASSWORD);
        }
    }


    @Override
    protected void listenerAction() {

        token.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String error = null;
                if(s.length() <= 0) error = getString(R.string.check_code) + getString(R.string.not_empty);
                ((TextInputLayout) token.getParent().getParent()).setError(error);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        access.setOnClickListener(v ->{
            if(TextUtils.isEmpty(token.getText())){
                ((TextInputLayout) token.getParent().getParent()).setError(getString(R.string.check_code)+ getString(R.string.not_empty));
            }
            else{
                try {
                    JSONObject bodyJson = new JSONObject().put(NetworkHelper.Constant.USERNAME, username)
                                                          .put(NetworkHelper.Constant.PASSWORD, password)
                                                          .put(NetworkHelper.Constant.TOKEN, token.getText().toString())
                                                          .put(NetworkHelper.Constant.DEVICE, AppUtilities.id(this));

                    NetworkHelper.HttpRequest.request(this, NetworkHelper.Constant.FIRST_LOGIN, bodyJson);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
