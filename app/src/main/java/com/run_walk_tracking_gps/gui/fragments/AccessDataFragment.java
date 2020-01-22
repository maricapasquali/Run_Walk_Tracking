package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.exception.PasswordNotCorrectException;
import com.run_walk_tracking_gps.utilities.CryptographicHashFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;

import androidx.fragment.app.Fragment;

public class AccessDataFragment extends Fragment {

    private final static String TAG = AccessDataFragment.class.getName();

    private EditText username;
    private EditText password;
    private EditText conf_password;

    private MaterialButton login;
    private AccessDataListener accessDataListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            accessDataListener = (AccessDataListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement AccessDataListener");
        }

    }

    private boolean isSetAll(){
        boolean isOk = true;
        if(TextUtils.isEmpty(username.getText())){
            isOk =false;
            username.setError(getString(R.string.username_not_empty));
        }
        if(TextUtils.isEmpty(password.getText()) ){
            isOk =false;
            password.setError(getString(R.string.password_not_empty));
        }
        if(TextUtils.isEmpty(conf_password.getText()) ){
            isOk =false;
            conf_password.setError(getString(R.string.confirm_password_not_empty));
        }
        return isOk;
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_3, container, false);

        username = view.findViewById(R.id.signup_profile_username);
        password = view.findViewById(R.id.signup_profile_password);
        conf_password = view.findViewById(R.id.signup_profile_confirm_password);
        login = view.findViewById(R.id.signup_login);

        setActionListener();
        return view;
    }

    private void setActionListener(){
        login.setOnClickListener(v ->{
            try {
                final String hash_password = CryptographicHashFunctions.md5(password.getText().toString());
                final String hash_conf_password = CryptographicHashFunctions.md5(conf_password.getText().toString());

                if(isSetAll()){

                    if(!hash_password.equals(hash_conf_password)) throw new PasswordNotCorrectException(getContext());

                    final JSONObject accessData = new JSONObject().put(NetworkHelper.Constant.USERNAME, username.getText())
                            .put(NetworkHelper.Constant.PASSWORD, hash_password);

                    accessDataListener.accessData(accessData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (PasswordNotCorrectException e) {
                conf_password.setError(e.getMessage());
                conf_password.setText("");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Stream.of(username, password, conf_password).forEach(editText -> editText.setError(null));

        if(!TextUtils.isEmpty(conf_password.getText())) conf_password.setText("");
    }

    public interface AccessDataListener{
        void accessData(JSONObject jsonAccess);
    }

}
