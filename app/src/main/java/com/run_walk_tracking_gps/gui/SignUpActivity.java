package com.run_walk_tracking_gps.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.exception.InternetNoAvailableException;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.enumerations.Language;
import com.run_walk_tracking_gps.task.CompressionBitMap;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.fragments.AccessDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PhysicalDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PersonalDataFragment;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.utilities.JSONUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    private Uri imageUri;
    private CompressionBitMap async = null;

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
    public void personalData(JSONObject personalInfoUser) {
        try {
            user = personalInfoUser;
            if(async!=null){
                final String img_encode = async.get();
                if(img_encode!=null)user.put(HttpRequest.Constant.IMG_ENCODE,img_encode);
            }
            Log.d(TAG, user.toString());
        }catch (NullPointerException e){
           // if(getSupportFragmentManager().findFragmentByTag(TAG) instanceof PhysicalDataFragment)
            //      getSupportFragmentManager().popBackStack(fragmentSignUp.get(PHYSICAL_DATA).getClass().getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
        }catch (NullPointerException e){
            getSupportFragmentManager().popBackStack(fragmentSignUp.get(ACCESS_DATA).getClass().getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            next.setVisible(true);
        }
    }

    @Override
    public void accessData(JSONObject jsonAccess) {
        try{
            user = JSONUtilities.merge(user, jsonAccess);
            user.put(HttpRequest.Constant.LANGUAGE, getString(Language.defaultForUser(this).getCode()));
            Log.d(TAG, user.toString());

            HttpRequest.requestSignUp(this, user,this);

        }catch (JSONException je){
            je.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (InternetNoAvailableException e) {
            e.alert();
        }
    }

    @Override
    public void onResponse(JSONObject response) {

        // TODO: 10/26/2019 ??? NOTIFICA PUSH OPPURE SNAKE INDEFINITO E AGGIUNGO IL FRAGMENT PER INSERIRE IL TOKEN ->POI ACCEDO SE è GIUSTO
        //Toast.makeText(this, getString(R.string.correctly_sign_up), Toast.LENGTH_LONG).show();
        //Snackbar.make(findViewById(R.id.snake), response.toString(), Snackbar.LENGTH_INDEFINITE).show();
        try {
            final Intent intent = new Intent(this, TokenActivity.class);
            intent.putExtra(KeysIntent.ID_USER, response.getInt(HttpRequest.Constant.ID_USER));
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
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
                    this.imageUri = imageUri;
                    imageView.setImageURI(imageUri);
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                    async = CompressionBitMap.create();
                    async.execute(bitmap);

                }).setWithImageCrop(1,1);
        imagePicker.choosePicture(true);
    }

    @Override
    public Uri getImageUri() {
        return this.imageUri;
    }

}
