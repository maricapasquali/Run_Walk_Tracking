package com.run_walk_tracking_gps.gui;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.fragments.AccessDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PhysicalDataFragment;
import com.run_walk_tracking_gps.gui.fragments.PersonalDataFragment;

import java.util.LinkedList;
import java.util.List;


public class SignUpActivity extends CommonActivity {

    private static final String TAG = SignUpActivity.class.getName();

    private final int PERSONAL_DATA = 0;
    private final int PHYSICAL_DATA = 1;
    private final int ACCESS_DATA = 2;

    private MenuItem next;

    private List<Fragment> fragmentSignUp = new LinkedList<>();

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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    private void addFragment(final Fragment fragment, final boolean toStack) {
        super.addFragment(fragment, R.id.container_fragment_signup, toStack, TAG);
    }
}
