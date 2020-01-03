package com.run_walk_tracking_gps.gui;

import android.annotation.SuppressLint;
import android.content.Context;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivationAccountActivity extends CommonActivity
        //implements  Response.Listener<JSONObject>
{
    private static final String TAG = ActivationAccountActivity.class.getName();

    private Button access;
    private EditText token;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void listenerAction() {
        access.setOnClickListener(v ->{
            if(TextUtils.isEmpty(token.getText())){
                token.setError(getString(R.string.check_code)+ getString(R.string.not_empty));
            }
            else{
                try {
                    @SuppressLint("HardwareIds")
                    String mac = CryptographicHashFunctions.md5(
                            ((WifiManager)getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress());

                    JSONObject bodyJson = new JSONObject().put(NetworkHelper.Constant.USERNAME, username)
                                                          .put(NetworkHelper.Constant.PASSWORD, password)
                                                          .put(NetworkHelper.Constant.TOKEN, token.getText().toString())
                                                          .put(NetworkHelper.Constant.DEVICE, mac);

                    NetworkHelper.HttpRequest.request(this, NetworkHelper.Constant.FIRST_LOGIN, bodyJson);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResponse(JSONObject response) {

        if(response.has(NetworkHelper.Constant.SESSION) && response.has(NetworkHelper.Constant.DATA)){
            try {
                // SESSION TO SHAREDPREFERENCE
                DefaultPreferencesUser.setSession(this, response.getJSONObject(NetworkHelper.Constant.SESSION));

                // DATA TO DATABASE
                JSONObject data = response.getJSONObject(NetworkHelper.Constant.DATA);
                final  JSONObject user = data.getJSONObject(NetworkHelper.Constant.USER);
                if(SqlLiteUserDao.create(this).insert(user)){
                    // TODO: 1/2/2020 DECOMPRESSIONE IMMAGINE E SALVATAGGIO IN image/
                    final JSONObject image = user.getJSONObject(NetworkHelper.Constant.IMAGE);
                    final String name = image.getString(ImageProfileDescriptor.NAME);
                    final String encode = image.getString(NetworkHelper.Constant.IMG_ENCODE);

                    DecompressionEncodeImageTask.create(this, name).execute(encode);
                }
                SqlLiteSettingsDao.create(this).insert(data.getJSONObject(NetworkHelper.Constant.SETTINGS));
                SqlLiteStatisticsDao.createWeightDao(this).insert(data.getJSONArray(NetworkHelper.Constant.WEIGHTS).getJSONObject(0));

                startActivity(new Intent(this, ApplicationActivity.class));
                finishAffinity();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    */
}
