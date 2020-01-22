package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.builder.UserBuilder;
import com.run_walk_tracking_gps.task.CompressionBitMapTask;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.fragments.AccessDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PhysicalDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PersonalDataFragment;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.utilities.ImageFileHelper;
import com.run_walk_tracking_gps.utilities.JSONUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class SignUpActivity extends CommonActivity
        implements PersonalDataFragment.PersonalDataListener,
                   PhysicalDataFragment.PhysicalDataListener,
                   AccessDataFragment.AccessDataListener,
                   //Response.Listener<JSONObject>,
                   PersonalDataFragment.ImagePickerHandlerListener {

    private static final String TAG = SignUpActivity.class.getName();

    private static JSONObject user = new JSONObject();

    private final int PERSONAL_DATA = 0;
    private final int PHYSICAL_DATA = 1;
    private final int ACCESS_DATA = 2;

    private MenuItem next;
    private List<Fragment> fragmentSignUp = new LinkedList<>();

    private ImagePicker imagePicker;
    private Uri imageUri;

    @Override
    protected void init(Bundle savedInstanceState) {
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
        next.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.next_signup:
                switch(fragmentSignUp.indexOf(getSupportFragmentManager().findFragmentByTag(TAG))) {
                    case PERSONAL_DATA:
                        addFragment(fragmentSignUp.get(PHYSICAL_DATA), true);
                        break;
                    case PHYSICAL_DATA:
                        addFragment(fragmentSignUp.get(ACCESS_DATA), true);
                        break;
                }
                next.setVisible(false);
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
        next.setVisible(fragmentSignUp.indexOf(getSupportFragmentManager().findFragmentByTag(TAG))!=ACCESS_DATA);
    }

    @Override
    protected void listenerAction() {
    }

    @Override
    public void next(boolean valid) {
        next.setVisible(valid);
    }

    @Override
    public void receivePersonalData(JSONObject personalInfoUser) {
        try {
            user = JSONUtilities.replace(user, personalInfoUser);
            Log.d(TAG, user.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void physicalData(JSONObject jsonPhysical) {
        try{
            user = JSONUtilities.merge(user, jsonPhysical);
            Log.d(TAG, user.toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void accessData(JSONObject jsonAccess) {
        try{
            user = JSONUtilities.merge(user, jsonAccess);
            Log.d(TAG, user.toString());

            NetworkHelper.HttpRequest.request(this, NetworkHelper.Constant.SIGN_UP, user);
/*
            Bitmap bitmap = null;
            if(!user.isNull(Constant.IMAGE)) {
                bitmap = BitmapFactory.decodeFile(ImageFileHelper.create(this).getPathTmpImage(user.getJSONObject(Constant.IMAGE).getString(Constant.NAME)));
            }
            if(bitmap!=null){
                RequestDialog progressDialog = RequestDialog.create(this);
                progressDialog.show();
                CompressionBitMapTask.create(this, image_encode -> {
                    user.getJSONObject(Constant.IMAGE).put(Constant.IMG_ENCODE, image_encode);
                    Log.d(TAG, "After compress : " + user.toString());
                    HttpRequest.requestSignUp(this, user, progressDialog);
                }).execute(bitmap);

               // SignUpTask.create(this, user).execute(bitmap);
            }
            else {
                user.remove(Constant.IMAGE);
                HttpRequest.requestSignUp(this, user, null);
            }
*/
        } catch (JSONException je){
            je.printStackTrace();
        }
        //catch (InternetNoAvailableException e) {            e.alert();        }
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
        imagePicker = new ImagePicker(this,
                null ,
                imageUri -> {
                    String newName = ImageFileHelper.createNameRandom();
                    Log.e("PICKERIMAGE", "Name : "+ newName);
                    ImageFileHelper imageFileHelper = ImageFileHelper.create(this);
                    if(imageFileHelper.moveToTmpDir(imageUri, newName)){
                        try {
                            this.imageUri = Uri.parse(imageFileHelper.getPathTmpImage(newName));
                            imageView.setImageURI(this.imageUri);
                            user.put(NetworkHelper.Constant.IMAGE, new JSONObject().put(NetworkHelper.Constant.NAME, newName));


                            Bitmap bitmap = null;
                            if(!user.isNull(NetworkHelper.Constant.IMAGE)) {
                                bitmap = BitmapFactory.decodeFile(ImageFileHelper.create(this)
                                        .getPathTmpImage(user.getJSONObject(NetworkHelper.Constant.IMAGE).getString(NetworkHelper.Constant.NAME)));
                            }
                            if(bitmap!=null){
                                CompressionBitMapTask.create(this, image_encode -> {
                                    user.getJSONObject(NetworkHelper.Constant.IMAGE).put(NetworkHelper.Constant.IMG_ENCODE, image_encode);
                                    Log.d(TAG, "After compress : " + user.toString());
                                }).execute(bitmap);
                            }else {
                                user.remove(NetworkHelper.Constant.IMAGE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }).setWithImageCrop(1,1);
        imagePicker.choosePicture(true);
    }

    @Override
    public Uri getImageUri() {
        return imageUri;
    }

}
