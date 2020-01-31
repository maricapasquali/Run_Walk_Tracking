package com.run_walk_tracking_gps.gui;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;

public class ForgotPassword extends CommonActivity {

    private TextInputEditText email;
    private MaterialButton request;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setTitle(getString(R.string.reset_password));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.forgot_email);
        request = findViewById(R.id.password_forgot_request);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void listenerAction() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ((TextInputLayout) email.getParent().getParent())
                        .setError(s.length()<=0 ? getString(R.string.email_not_empty) : null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        request.setOnClickListener(v ->{

            if(TextUtils.isEmpty(email.getText())){
                ((TextInputLayout) email.getParent().getParent()).setError(getString(R.string.email)+ getString(R.string.not_empty));
            }
            else{
                try {
                    JSONObject bodyJson = new JSONObject().put(NetworkHelper.Constant.EMAIL, email.getText().toString());
                    NetworkHelper.HttpRequest.request(this, NetworkHelper.Constant.FORGOT_PASSWORD, bodyJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
