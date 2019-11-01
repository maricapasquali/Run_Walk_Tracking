package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.Preferences;

import org.json.JSONException;
import org.json.JSONObject;


public class TokenActivity extends CommonActivity implements  Response.Listener<JSONObject> {
    private static final String TAG = TokenActivity.class.getName();

    private Button access;
    private EditText token;
    private int id_user;
    @Override
    protected void init() {
        setContentView(R.layout.activity_token);
        access = findViewById(R.id.access_login_token);
        token = findViewById(R.id.access_token);

        if(getIntent()!=null){
            id_user = getIntent().getIntExtra(FieldDataBase.ID_USER.toName(), 0);
        }
    }

    @Override
    protected void listenerAction() {
        access.setOnClickListener(v ->{
            if(TextUtils.isEmpty(token.getText())){
                token.setError(getString(R.string.token)+ getString(R.string.not_empty));
            }
            else{
                try {
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put(FieldDataBase.ID_USER.toName(), id_user);
                    bodyJson.put(FieldDataBase.TOKEN.toName(), token.getText().toString());

                    if(!HttpRequest.requestFirstSignIn(this, bodyJson, this )){
                        Toast.makeText(this, getString(R.string.internet_not_available), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

                Preferences.writeSettingsIntoSharedPreferences(this, response);
                Intent intent = new Intent(this, ApplicationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
