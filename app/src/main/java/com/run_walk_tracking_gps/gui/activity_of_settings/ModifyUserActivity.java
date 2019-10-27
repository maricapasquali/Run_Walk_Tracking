package com.run_walk_tracking_gps.gui.activity_of_settings;

import android.content.Intent;

import android.net.Uri;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myhexaville.smartimagepicker.ImagePicker;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.CommonActivity;
import com.run_walk_tracking_gps.gui.dialog.ChooseDialog;
import com.run_walk_tracking_gps.gui.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.dialog.HeightDialog;
import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import java.util.Arrays;

public class ModifyUserActivity extends CommonActivity {
    private static final String TAG = ModifyUserActivity.class.getName();

    private ImageView img;
    private EditText name;
    private EditText lastName;
    private EditText email;
    private EditText city;
    private EditText tel;

    private TextView gender;
    private TextView birthDate;
    private TextView height;

    private ImagePicker imagePicker;

    private User user;
    @Override
    protected void initGui() {
        setContentView(R.layout.activity_modify_profile);

        getSupportActionBar().setTitle(R.string.modify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img = findViewById(R.id.modify_profile_img);
        name = findViewById(R.id.modify_profile_name);
        lastName = findViewById(R.id.modify_profile_lastname);
        email = findViewById(R.id.modify_profile_email);
        city = findViewById(R.id.modify_profile_city);
        tel = findViewById(R.id.modify_profile_tel);

        gender = findViewById(R.id.modify_profile_gender);
        birthDate = findViewById(R.id.modify_profile_birth_date);
        height = findViewById(R.id.modify_profile_height);

        if(getIntent()!=null){
            user = (User)getIntent().getParcelableExtra(getString(R.string.profile));
            Log.d(TAG, user.toString());

            if(!user.getImg().equals(Uri.EMPTY)) {
                img.setImageURI(user.getImg());
            }
            name.setText(user.getName());
            lastName.setText(user.getLastName());
            gender.setText(user.getGender().getStrId());
            gender.setCompoundDrawablesWithIntrinsicBounds(getDrawable(user.getGender().getIconId()), null, null, null);
            birthDate.setText(user.getBirthDateString());
            email.setText(user.getEmail());
            city.setText(user.getCity());
            tel.setText(user.getTel());

            height.setText(user.getHeight());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile:{
                Toast.makeText(this, getString(R.string.save), Toast.LENGTH_LONG).show();
                user.setName(name.getText().toString());
                user.setLastName(lastName.getText().toString());
                user.setEmail(email.getText().toString());
                user.setCity(city.getText().toString());
                user.setTel(tel.getText().toString());

                final Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.changed_profile), user);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void listenerAction() {

        img.setOnClickListener( v ->{
            imagePicker = new ImagePicker(this /*activity non null*/,
                    null /*fragment nullable*/,
                    imageUri -> { /*on image picked*/
                        user.setImg(imageUri);

                        img.setImageURI(imageUri);
                        Log.d(TAG, user.toString());
                        Toast.makeText(this, user.toString(), Toast.LENGTH_LONG).show();

                    }).setWithImageCrop(1,1);
            imagePicker.choosePicture(true);
        });


        gender.setOnClickListener(v ->{

            final ChooseDialog<Gender> genderDialog = new ChooseDialog<>(this, Gender.values(),
                    ((TextView) v).getText(),
                    (val, description) -> {
                        TextView gView = ((TextView) v);
                        gView.setText(val.getStrId());
                        gView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(val.getIconId()), null, null, null);
                        user.setGender(val);


                    },
                    () -> Arrays.stream(Gender.values())
                            .map(g -> getString(g.getStrId()))
                            .toArray(String[]::new)
            );
            genderDialog.setTitle(R.string.gender);
            genderDialog.create().show();
        });

        birthDate.setOnClickListener(v ->{
            final TextView d = ((TextView) v);
            DateTimePickerDialog.create(this, d.getText().toString() , (date, calendar) -> {
                d.setText(date);
                user.setBirthDate(calendar.getTime());
            }, true).show();
        });

        height.setOnClickListener(v ->{
            final TextView h = ((TextView) v);
            HeightDialog.create(this, h.getText().toString(),  (heightString, height) -> {
                h .setText(heightString);
                user.setHeight(heightString);
            }).create().show();
        });
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
}
