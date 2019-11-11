package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.Response;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.task.CompressionBitMap;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.fragments.AccessDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PhysicalDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PersonalDataFragment;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.model.enumerations.Target;
import com.run_walk_tracking_gps.utilities.EnumUtilities;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends CommonActivity
                            implements PersonalDataFragment.PersonalDataListener,
                                       PhysicalDataFragment.PhysicalDataListener,
                                       AccessDataFragment.AccessDataListener,
                                       Response.Listener<JSONObject>,
                                       PersonalDataFragment.ImagePickerHandlerListener {

    private static final String TAG = SignUpActivity.class.getName();

    private static JSONObject user = new JSONObject();

    private final int PERSONAL_DATA = 0;
    private final int PHYSICAL_DATA = 1;
    private final int ACCESS_DATA = 2;

    private MenuItem next;
    private List<Fragment> fragmentSignUp = new LinkedList<>();

    private ImagePicker imagePicker;
    private Bitmap bitmap;

    private OnCheckListener onCheckListener;

    @Override
    protected void init() {
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle(getString(R.string.rec));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onCheckListener = (OnCheckListener) getApplicationContext();

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
                try{
                    //Log.d(TAG, "Next : Fragment (Index) = " + fragmentSignUp.indexOf(getSupportFragmentManager().findFragmentByTag(TAG)));
                    switch(fragmentSignUp.indexOf(getSupportFragmentManager().findFragmentByTag(TAG))) {
                        case PERSONAL_DATA:
                            addFragment(fragmentSignUp.get(PHYSICAL_DATA), true);
                            break;
                        case PHYSICAL_DATA:
                            addFragment(fragmentSignUp.get(ACCESS_DATA), true);
                            next.setVisible(false);
                            break;
                    }

                }catch (Exception e){
                    Toast.makeText(this, "Campi non settati" , Toast.LENGTH_LONG).show();
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
    public void personalData(User personalInfoUser) {

        try{
            user.put(FieldDataBase.NAME.toName(), personalInfoUser.getName())
                .put(FieldDataBase.LAST_NAME.toName(), personalInfoUser.getLastName())
                .put(FieldDataBase.BIRTH_DATE.toName(), personalInfoUser.getBirthDate())
                .put(FieldDataBase.EMAIL.toName(), personalInfoUser.getEmail())
                .put(FieldDataBase.CITY.toName(), personalInfoUser.getCity())
                .put(FieldDataBase.PHONE.toName(), personalInfoUser.getPhone());
        }catch (JSONException je){
            Log.e(TAG, je.getMessage());
        }
        Log.d(TAG, user.toString());
    }

    @Override
    public void physicalData(int gender, int target, double height, double weight) {
        try{
            user.put(FieldDataBase.GENDER.toName(), EnumUtilities.getEnumFromStrId(Gender.class, gender))
                .put(FieldDataBase.TARGET.toName(), EnumUtilities.getEnumFromStrId(Target.class, target))
                .put(FieldDataBase.HEIGHT.toName(), height)
                .put(FieldDataBase.WEIGHT.toName(), weight);
        }catch (JSONException je){
            Log.e(TAG, je.getMessage());
        }
        Log.d(TAG, user.toString());
    }

    @Override
    public void accessData(String username, String hash_password) {
        try{
            user.put(FieldDataBase.USERNAME.toName(), username)
                .put(FieldDataBase.PASSWORD.toName(), hash_password)
                .put(FieldDataBase.LANGUAGE.toName(), Locale.getDefault().getDisplayLanguage());

        }catch (JSONException je){
            Log.e(TAG, je.getMessage());
        }
        Log.d(TAG, user.toString());

        if(!HttpRequest.requestSignUp(this, user,this)) {
            Toast.makeText(this, getString(R.string.internet_not_available), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(JSONObject response) {

        if(HttpRequest.someError(response)){
            Snackbar.make(findViewById(R.id.snake), response.toString(), Snackbar.LENGTH_LONG).show();
            Log.e(TAG, response.toString());
        }
        else{
            // TODO: 10/26/2019 ??? NOTIFICA PUSH OPPURE SNAKE INDEFINITO E AGGIUNGO IL FRAGMENT PER INSERIRE IL TOKEN ->POI ACCEDO SE è GIUSTO
            //Toast.makeText(this, getString(R.string.correctly_sign_up), Toast.LENGTH_LONG).show();
            //Snackbar.make(findViewById(R.id.snake), response.toString(), Snackbar.LENGTH_INDEFINITE).show();

            try {
                Intent intent = new Intent(this, TokenActivity.class);
                intent.putExtra(FieldDataBase.ID_USER.toName(), response.getInt(FieldDataBase.ID_USER.toName()));
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

                    CompressionBitMap.create(image_encode -> {
                        try {
                            user.put(FieldDataBase.IMG_ENCODE.toName(), image_encode);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        Log.e(TAG, user.toString());
                    }).execute(bitmap);

                }).setWithImageCrop(1,1);
        imagePicker.choosePicture(true);
    }

    public interface OnCheckListener {
        boolean check();
    }

}
