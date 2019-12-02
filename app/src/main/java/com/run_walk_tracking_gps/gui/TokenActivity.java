package com.run_walk_tracking_gps.gui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.intent.KeysIntent;

import org.json.JSONException;
import org.json.JSONObject;


public class TokenActivity extends CommonActivity implements  Response.Listener<JSONObject> {
    private static final String TAG = TokenActivity.class.getName();

    private Button access;
    private EditText token;
    private int id_user;
    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_token);
        getSupportActionBar().setTitle(R.string.token);
        access = findViewById(R.id.access_login_token);
        token = findViewById(R.id.access_token);

        if(getIntent()!=null){
            id_user = getIntent().getIntExtra(KeysIntent.ID_USER, 0);
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
                    JSONObject bodyJson = new JSONObject().put(FieldDataBase.ID_USER.toName(), id_user)
                                                          .put(FieldDataBase.TOKEN.toName(), token.getText().toString());

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
        if(SplashScreenActivity.dataAccessResponse(this, response)) finishAffinity();
    }
}
