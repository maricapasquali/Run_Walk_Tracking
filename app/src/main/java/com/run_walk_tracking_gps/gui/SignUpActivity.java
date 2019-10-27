package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import android.support.v4.app.Fragment;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.Response;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.fragments.AccessDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PhysicalDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PersonalDataFragment;
import com.run_walk_tracking_gps.model.enumerations.Target;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;
import com.run_walk_tracking_gps.utilities.EnumUtilities;
import com.run_walk_tracking_gps.model.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends CommonActivity
                            implements PersonalDataFragment.PersonalDataListener,
                                       PhysicalDataFragment.PhysicalDataListener,
                                       AccessDataFragment.AccessDataListener,
                                       Response.Listener<JSONObject>,
                                        //Response.Listener<String>,
        PersonalDataFragment.ImagePickerHandlerListener {

    private static final String TAG = SignUpActivity.class.getName();

    private static final String KEY_IMG ="img_encode";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_DATE = "birth_date";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CITY = "city";
    private static final String KEY_PHONE = "phone";

    private static final String KEY_GENDER = "gender";
    private static final String KEY_TARGET = "target";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";

    private static final String KEY_USERNAME ="username";
    private static final String KEY_HASH_PASSWORD ="password";
    private static final String KEY_LANGUAGE ="language";

    private static JSONObject user = new JSONObject();


    private final int PERSONAL_DATA = 0;
    private final int PHYSICAL_DATA = 1;
    private final int ACCESS_DATA = 2;

    private MenuItem next;
    private List<Fragment> fragmentSignUp = new LinkedList<>();

    private ImagePicker imagePicker;
    private Bitmap bitmap;

    @Override
    protected void initGui() {
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle(getString(R.string.rec));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentSignUp.add(PERSONAL_DATA, new PersonalDataFragment());
        fragmentSignUp.add(PHYSICAL_DATA, new PhysicalDataFragment());
        fragmentSignUp.add(ACCESS_DATA, new AccessDataFragment());

        addFragment(fragmentSignUp.get(PERSONAL_DATA), false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        next = menu.findItem(R.id.next_signup);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.next_signup:
                //Log.d(TAG, "Next : Fragment (Index) = " + fragmentSignUp.indexOf(getSupportFragmentManager().findFragmentByTag(TAG)));
                switch (fragmentSignUp.indexOf(getSupportFragmentManager().findFragmentByTag(TAG))) {
                    case PERSONAL_DATA:
                        addFragment(fragmentSignUp.get(PHYSICAL_DATA), true);
                        break;

                    case PHYSICAL_DATA:
                        addFragment(fragmentSignUp.get(ACCESS_DATA), true);
                        next.setVisible(false);
                        break;
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Log.d(TAG, "Back :Fragment (Index) = " + fragmentSignUp.indexOf(getSupportFragmentManager().findFragmentByTag(TAG)));
        next.setVisible(fragmentSignUp.indexOf(getSupportFragmentManager().findFragmentByTag(TAG))!=ACCESS_DATA);
    }

    @Override
    protected void listenerAction() {
    }



    @Override
    public void personalData(String name, String last_name, Date date_birth, String email, String city, String phone) {

        try{
            user.put(KEY_NAME, name);
            user.put(KEY_LAST_NAME, last_name);
            user.put(KEY_DATE, date_birth);
            user.put(KEY_EMAIL, email);
            user.put(KEY_CITY, city);
            user.put(KEY_PHONE, phone);
        }catch (JSONException je){
            Log.e(TAG, je.getMessage());
        }

        Log.d(TAG, user.toString());
    }

    @Override
    public void physicalData(int gender, int target, double height, double weight) {
        try{
            user.put(KEY_GENDER, getString(gender));
            user.put(KEY_TARGET, EnumUtilities.getEnumFromStrId(Target.class, target));
            user.put(KEY_HEIGHT, height);
            user.put(KEY_WEIGHT, weight);
        }catch (JSONException je){
            Log.e(TAG, je.getMessage());
        }
        Log.d(TAG, user.toString());
    }

    @Override
    public void accessData(String username, String hash_password) {
        try{
            user.put(KEY_USERNAME, username);
            user.put(KEY_HASH_PASSWORD, hash_password);
            user.put(KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());

        }catch (JSONException je){
            Log.e(TAG, je.getMessage());
        }

        Log.d(TAG, user.toString());

// TODO: 10/17/2019  RICHIESTA AL DATABASE (anche username)
        //if(!HttpRequest.requestStringToServerVolley(this, HttpRequest.SIGN_UP, Request.Method.POST, user, this))
        /*if(!HttpRequest.requestJsonPostToServerVolley(this, HttpRequest.SIGN_UP, user,this))
        {
            Toast.makeText(this, getString(R.string.internet_not_available), Toast.LENGTH_LONG).show();
        }*/
    }
    /*@Override
    public void onResponse(String response) {
        Log.e(TAG, response);
    }*/
    @Override
    public void onResponse(JSONObject response) {
        Iterator<String> it = response.keys();
        if(it.hasNext() && it.next().equals(HttpRequest.ERROR)){
            Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
            Log.e(TAG, response.toString());
        }
        else{
        // TODO: 10/26/2019 NOTIFICA PUSH
            Toast.makeText(this, "Registrazione avvenuta correttamente! Riceverai un email.", Toast.LENGTH_LONG).show();
        }
    }

    private void addFragment(final Fragment fragment, final boolean toStack) {
        super.addFragment(fragment, R.id.container_fragment_signup, toStack, TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode,requestCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);
    }

    @Override
    public void imagePickerHandler(ImageView imageView) {
        imagePicker = new ImagePicker(this /*activity non null*/,
                null /*fragment nullable*/,
                imageUri -> { /*on image picked*/
                    imageView.setImageURI(imageUri);
                    bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                    CompressionBitMap.create().execute(bitmap);

                }).setWithImageCrop(1,1);
        imagePicker.choosePicture(true);
    }

    private static class CompressionBitMap extends AsyncTask<Bitmap, Void, String>{
        private CompressionBitMap(){}
        public static CompressionBitMap create(){
            return new CompressionBitMap();
        }


        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            return BitmapUtilities.BitMapToString(bitmaps[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                user.put(KEY_IMG, s);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            Log.e(TAG, user.toString());
        }
    }
}
