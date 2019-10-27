package com.run_walk_tracking_gps.gui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccessDataFragment extends Fragment {

    private final static String TAG = AccessDataFragment.class.getName();

    private EditText username;
    private EditText password;
    private EditText conf_password;

    private Button login;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_3, container, false);

        username = view.findViewById(R.id.signup_profile_username);
        password = view.findViewById(R.id.signup_profile_password);
        conf_password = view.findViewById(R.id.signup_profile_confirm_password);
        login = view.findViewById(R.id.signup_login);

        login.setOnClickListener(v ->{
            final String hash_password = md5(password.getText().toString());
            Log.d(TAG, hash_password);
            accessDataListener.accessData(username.getText().toString(), hash_password);
        });


        return view;
    }

    private String md5(String password){
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(password.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            return no.toString(16);

        } catch (NoSuchAlgorithmException e) {// For specifying wrong message digest algorithms
            throw new RuntimeException(e);
        }
    }


    public interface AccessDataListener{
        void accessData(String username, String hash_password);
    }
}
