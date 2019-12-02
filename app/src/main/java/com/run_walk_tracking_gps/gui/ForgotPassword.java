package com.run_walk_tracking_gps.gui;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends CommonActivity  implements  Response.Listener<JSONObject> {

    private TextView mexSuccess;
    private EditText email;
    private Button request;

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setTitle(getString(R.string.reset_password));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mexSuccess = findViewById(R.id.request_success);
        email = findViewById(R.id.forgot_email);
        request = findViewById(R.id.password_forgot_request);

    }

    @Override
    protected void listenerAction() {
        request.setOnClickListener(v ->{

            if(TextUtils.isEmpty(email.getText())){
                email.setError(getString(R.string.email)+ getString(R.string.not_empty));
            }
            else{
                try {
                    JSONObject bodyJson = new JSONObject().put(FieldDataBase.EMAIL.toName(), email.getText().toString());
                    if(!HttpRequest.requestForgotPassword(this, bodyJson, this)){
                        Snackbar.make(findViewById(R.id.snake),getString(R.string.internet_not_available), Snackbar.LENGTH_LONG).show();
                    }
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

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(HttpRequest.someError(response)){
                Snackbar.make(findViewById(R.id.snake), response.get(HttpRequest.ERROR).toString(), Snackbar.LENGTH_LONG).show();
            }else {

                if(response.getBoolean("request_sended")){
                    mexSuccess.setVisibility(View.VISIBLE);
                    request.setVisibility(View.GONE);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
