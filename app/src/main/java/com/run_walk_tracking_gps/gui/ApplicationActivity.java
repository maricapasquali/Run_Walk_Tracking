package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.gui.fragments.HomeFragment;
import com.run_walk_tracking_gps.gui.fragments.StatisticsFragment;
import com.run_walk_tracking_gps.gui.fragments.WorkoutsFragment;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.StatisticsData;
import com.run_walk_tracking_gps.receiver.ActionReceiver;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class ApplicationActivity extends CommonActivity
        implements  WorkoutsFragment.OnWorkOutSelectedListener,
                    WorkoutsFragment.OnManualAddClickedListener,
                    HomeFragment.OnStopWorkoutClickListener ,
                    HomeFragment.OnBlockScreenClickListener ,
                    StatisticsFragment.OnWeightListener{

    public final static String TAG = ApplicationActivity.class.getName();
    //private final static int REQUEST_SETTINGS = 1;
    private final static int REQUEST_CHANGED_DETAILS = 2;
    private final static int REQUEST_SUMMARY = 3;
    private final static int REQUEST_NEW_WORKOUT = 4;
    private final static int REQUEST_NEW_WEIGHT = 5;
    private final static int REQUEST_MODIFY_WEIGHT = 6;

    private BottomNavigationView navigationBarBottom;
    private Menu menuApp;

    private String restore = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void init(Bundle savedInstanceState) {
        Log.d(TAG,"init");
        setContentView(R.layout.activity_application);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_runtracking_light);

        setNavigationBarBottom();

        if(getIntent()!=null) {
            Log.d(TAG, "getIntent : " + getIntent());
            if(getIntent().getAction()!=null &&
                    (getIntent().getAction().equals(ActionReceiver.RUNNING_WORKOUT)
                            || getIntent().getAction().equals(ActionReceiver.STOP_ACTION))){
                restore = getIntent().getAction();
                selectActiveFragment(HomeFragment.class);
                restore = null;
            }else{
                if(savedInstanceState==null)
                    selectActiveFragment(HomeFragment.class);
                else
                    setTitleAndLogoActionBar(getSupportFragmentManager().findFragmentByTag(TAG));
            }

        }
    }

    @Override
    protected void listenerAction() {
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNavigationBarBottom(){
        navigationBarBottom = findViewById(R.id.nav_bar);

        navigationBarBottom.setOnNavigationItemSelectedListener(menuItem -> {
            Log.d(TAG, "setOnNavigationItemSelectedListener");
            final int previousItem = navigationBarBottom.getSelectedItemId();
            final int nextItem = menuItem.getItemId();

            if(previousItem!=nextItem)
            {
                switch (menuItem.getItemId()) {
                    case R.id.workouts:

                        addFragment(new WorkoutsFragment(),false);
                        break;
                    case R.id.home:
                        Log.d(TAG, "Home: restore : " + restore);

                        addFragment(restore==null ?
                                    new HomeFragment() :
                                    HomeFragment.createWithArgument(restore),false);
                        break;
                    case R.id.statistics:

                        addFragment(new StatisticsFragment(), false);
                        break;
                    default:
                        return true;
                }
            }
            else
            {
                NetworkHelper.HttpRequest.syncInForeground(this);
            }
            return true;
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "New Intent Action = " + intent.getAction());
        if(intent.getAction()!=null){
            switch (intent.getAction()){
                case ActionReceiver.STOP_ACTION:{
                    final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_fragments_application);
                    if(fragment instanceof HomeFragment && ((HomeFragment)fragment).getServiceHandler().isWorkoutRunning()){
                        ((HomeFragment)fragment).stop();
                    }
                }
                break;
            }
        }
    }

    private void selectActiveFragment(final Class fragment_class) {
        navigationBarBottom.setVisibility(View.VISIBLE);
        navigationBarBottom.setSelectedItemId(fragment_class == HomeFragment.class ? R.id.home :
                    fragment_class == WorkoutsFragment.class ? R.id.workouts : R.id.statistics);
    }

    private void addFragment(final Fragment fragment, final boolean toStack) {
        super.addFragment(fragment, R.id.container_fragments_application, toStack, TAG);

        setTitleAndLogoActionBar(fragment);
        Log.d(TAG, "Add " + fragment.getClass().getSimpleName());
    }

    private void setTitleAndLogoActionBar(final Fragment fragment) {
        if(getSupportActionBar()!= null){
            getSupportActionBar().setLogo(fragment instanceof HomeFragment ? R.drawable.ic_runtracking_light : 0);
            getSupportActionBar().setTitle("  " + getString(fragment instanceof HomeFragment ? R.string.app_name :
                    fragment instanceof WorkoutsFragment? R.string.workouts : R.string.statistics));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuApp = menu;
        getMenuInflater().inflate(R.menu.menu_application, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_fragments_application);
        if(fragment instanceof HomeFragment && ((HomeFragment)fragment).getServiceHandler().isWorkoutRunning()){
            moveTaskToBack(true);
        } else{
            finishAffinity();
        }
    }

    // LISTENER FRAGMENTS
    // WorkoutsFragment Listener
    @Override
    public void onWorkOutSelected(Workout workout) {
        final Intent intentDetail = new Intent(this, DetailsWorkoutActivity.class);
        intentDetail.putExtra(KeysIntent.DETAIL, workout);
        startActivityForResult(intentDetail, REQUEST_CHANGED_DETAILS);
    }

    // Summary (DetailsWorkoutActivity) Listener
    @Override
    public void OnStopWorkoutClick(Workout workout) {
        startActivityForResult(new Intent(this, DetailsWorkoutActivity.class)
                .putExtra(KeysIntent.SUMMARY, workout), REQUEST_SUMMARY);
    }

    @Override
    public void onBlockScreenClick(boolean isClickable) {
        menuApp.findItem(R.id.setting).setEnabled(isClickable);
    }

    // NewManualWorkoutActivity Listener
    @Override
    public void onManualAddClickedListener() {
        final Intent newWorkoutIntent = new Intent(this, NewManualWorkoutActivity.class);
        newWorkoutIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(newWorkoutIntent, REQUEST_NEW_WORKOUT);
    }

    // StatisticsFragment Listener
    @Override
    public void onAddWeight() {
        final Intent intent = new Intent(this, NewWeightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, REQUEST_NEW_WEIGHT);
    }

    @Override
    public void modifyWeight(StatisticsData statisticsData) {
        final Intent intent = new Intent(this, ModifyWeightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(KeysIntent.MODIFY_WEIGHT, statisticsData);
        startActivityForResult(intent, REQUEST_MODIFY_WEIGHT);
    }

    //Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        // TODO: 1/1/2020 UPDATE GUI
        switch (requestCode){
            case REQUEST_SUMMARY:
                if(resultCode==Activity.RESULT_OK)
                    Toast.makeText(this, "Auto-Workout add", Toast.LENGTH_LONG).show();

                if(!getSupportActionBar().isShowing()) getSupportActionBar().show();
                selectActiveFragment(WorkoutsFragment.class);
                break;
            case REQUEST_CHANGED_DETAILS:
                if(resultCode==Activity.RESULT_OK){
                    Toast.makeText(this, "Modify or Delete " + getString(R.string.workout), Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_NEW_WORKOUT:
                if(resultCode==Activity.RESULT_OK) {
                    Toast.makeText(this,"New " + getString(R.string.workout), Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_NEW_WEIGHT:
                if(resultCode==Activity.RESULT_OK) {
                    Toast.makeText(this, "New " + getString(R.string.weight), Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_MODIFY_WEIGHT:
                if(resultCode==Activity.RESULT_OK) {
                    Toast.makeText(this, "Modify or Delete " + getString(R.string.weight), Toast.LENGTH_LONG).show();
                }
                break;
            /*case REQUEST_SETTINGS:
                if(resultCode==Activity.RESULT_OK) {
                    //Toast.makeText(this, "Close Settings", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Close Settings");
                }
                break;*/
        }
    }
}
