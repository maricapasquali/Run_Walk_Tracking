package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.fragments.HomeFragment;
import com.run_walk_tracking_gps.gui.fragments.StatisticsFragment;
import com.run_walk_tracking_gps.gui.fragments.WorkoutsFragment;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.utilities.DateUtilities;
import com.run_walk_tracking_gps.model.StatisticsData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class ApplicationActivity extends CommonActivity implements WorkoutsFragment.OnWorkOutSelectedListener,
        HomeFragment.OnStopWorkoutClickListener , WorkoutsFragment.OnManualAddClickedListener,
        StatisticsFragment.OnChangeFiltersListener, StatisticsFragment.OnAddWeightListener{

    private final static String TAG = ApplicationActivity.class.getName();
    private final static int REQUEST_SETTINGS = 1;
    private final static int REQUEST_CHANGED_DETAILS = 2;
    private final static int REQUEST_SUMMARY = 3;
    private final static int REQUEST_NEW_WORKOUT = 4;
    private final static int REQUEST_NEW_WEIGHT = 5;

    private BottomNavigationView navigationBarBottom;

    private StatisticsData newWeight;
    private Workout newWorkout;
    private Workout workoutChanged;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initGui() {
        Log.d(TAG,"OnCreate");
        setContentView(R.layout.activity_application);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setIcon(R.drawable.ic_runtracking_light);


        navigationBarBottom = findViewById(R.id.nav_bar);

        addFragment(new HomeFragment(), false);
        selectActiveFragment(HomeFragment.class);
    }

    private Date date(String string) throws ParseException {
        return DateUtilities.parseShortToDate(string);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void listenerAction() {
        navigationBarBottom.setOnNavigationItemSelectedListener(menuItem -> {
             if(!menuItem.isChecked()){
                 Log.d(TAG, "onSelectNavigationBottom");
                 switch (menuItem.getItemId()) {
                     case R.id.workouts:
                         addFragment(new WorkoutsFragment(), false);
                         //addFragment(WorkoutsFragment.createWithArgument(workouts),false);
                         break;
                     case R.id.home:
                         addFragment(new HomeFragment(),false);
                         break;
                     case R.id.statistics:
                         addFragment(new StatisticsFragment(), false);
                         break;
                 }
             }
            return true;

        });
    }

    private void selectActiveFragment(final Class fragment_class) {
        navigationBarBottom.setVisibility(View.VISIBLE);
        navigationBarBottom.setSelectedItemId(fragment_class == HomeFragment.class ? R.id.home :
                    fragment_class == WorkoutsFragment.class ? R.id.workouts : R.id.statistics);
    }

    private void addFragment(final Fragment fragment, final boolean toStack) {
        super.addFragment(fragment, R.id.container_fragments_application, toStack, TAG);
        setTitleAndLogoActionBar(fragment.getClass());
        Log.d(TAG, "Add " + fragment.getClass().getSimpleName());
    }

    private void setTitleAndLogoActionBar(final Class fragment_class) {
        if(getSupportActionBar()!=null){
            getSupportActionBar().setLogo(fragment_class==HomeFragment.class ? R.drawable.ic_runtracking_light : 0);
            getSupportActionBar().setTitle(fragment_class==HomeFragment.class ? R.string.app_name :
                    fragment_class==WorkoutsFragment.class? R.string.workouts :
                            R.string.statistics);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_application, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(intent, REQUEST_SETTINGS);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"OnResume");
        /*final Fragment topFragmentStack = getSupportFragmentManager().getPrimaryNavigationFragment();
        if(topFragmentStack!=null) {
            addFragment(topFragmentStack, false);
        }*/
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG,"OnBack");
        System.exit(0);
        /*addFragment(new HomeFragment(), false);selectActiveFragment(HomeFragment.class);
        if(getSupportFragmentManager().findFragmentById(R.id.container_fragments_application).getClass()==HomeFragment.class)
        {
            finish();
            System.exit(0);
        }*/
    }

    // LISTENER FRAGMENTS

    // WorkoutsFragment Listener
    @Override
    public void onWorkOutSelected(Workout workout) {
        final Intent intentDetail = new Intent(this, DetailsWorkoutActivity.class);
        intentDetail.putExtra(getString(R.string.detail_workout), workout);
        startActivityForResult(intentDetail, REQUEST_CHANGED_DETAILS);
    }

    @Override
    public Workout workoutChanged() {
        return workoutChanged;
    }

    @Override
    public void resetWorkoutChanged() {
        workoutChanged = null;
    }

    // Summary (DetailsWorkoutActivity) Listener
    @Override
    public void OnStopWorkoutClick(Workout workout) {
        final Intent intentSummary = new Intent(this, DetailsWorkoutActivity.class);
        intentSummary.putExtra(getString(R.string.summary_workout), workout);
        intentSummary.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intentSummary, REQUEST_SUMMARY);
    }

    // NewManualWorkoutActivity Listener
    @Override
    public void onManualAddClickedListener() {
        final Intent newWorkoutIntent = new Intent(this, NewManualWorkoutActivity.class);
        newWorkoutIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(newWorkoutIntent, REQUEST_NEW_WORKOUT);
    }

    @Override
    public Workout newWorkout() {
        return newWorkout;
    }

    @Override
    public void resetNewWorkout() {
        newWorkout = null;
    }

    // StatisticsFragment Listener
    @Override
    public ArrayList<StatisticsData> onChangeFilters(Measure measure, FilterTime filterTime) {
        // RICHIESTA AL DATABASE IN BASE A 'measure' e 'filterTime'

        //ESEMPIO DI RICHIESTA (con filterTime = ALL)
        final ArrayList<StatisticsData> s = new ArrayList<>();
        try{
            switch (measure){
                case WEIGHT:
                    s.add(new StatisticsData(date("10/09/2019"), 55.0));
                    s.add(new StatisticsData(date("09/09/2019"), 56.0));
                    s.add(new StatisticsData(date("08/09/2019"), 56.4));
                    s.add(new StatisticsData(date("07/09/2019"), 55.4));
                    break;
                case MIDDLE_SPEED:
                    s.add(new StatisticsData(date("10/09/2019"), 6.0));
                    s.add(new StatisticsData(date("08/09/2019"), 8.0));
                    s.add(new StatisticsData(date("07/09/2019"), 7.0));
                    break;
            }

        }catch (ParseException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return s;
    }

    @Override
    public void onAddWeight() {
        final Intent intent = new Intent(this, NewWeightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, REQUEST_NEW_WEIGHT);
    }

    @Override
    public StatisticsData newWeight() {
        return newWeight;
    }

    //Results
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (requestCode){
            case REQUEST_CHANGED_DETAILS:
                if(resultCode==Activity.RESULT_OK){
                    Toast.makeText(this, getString(R.string.changed_workout), Toast.LENGTH_LONG).show();
                    workoutChanged = (Workout)data.getParcelableExtra(getString(R.string.changed_workout));
                    Log.d(TAG, "Workout changed = " +workoutChanged);
                }
                break;
            case REQUEST_SUMMARY:
                if(resultCode==Activity.RESULT_OK) {
                    Toast.makeText(this, getString(R.string.summary_workout), Toast.LENGTH_LONG).show();
                    final Workout newAutoWorkout = (Workout) data.getParcelableExtra(getString(R.string.new_workout));
                    Log.d(TAG, "New Auto Workout = " + newAutoWorkout);
                    // TODO: 10/17/2019 RICHIEDE DATABASE
                    selectActiveFragment(WorkoutsFragment.class);
                }
                break;
            case REQUEST_NEW_WORKOUT:
                if(resultCode==Activity.RESULT_OK) {
                    Toast.makeText(this, getString(R.string.new_workout_manual), Toast.LENGTH_LONG).show();
                    newWorkout = (Workout) data.getParcelableExtra(getString(R.string.new_workout_manual));
                    Log.d(TAG, "New Manual Workout = " + newWorkout);
                }
                break;

            case REQUEST_NEW_WEIGHT:
                if(resultCode==Activity.RESULT_OK) {
                    Toast.makeText(this, getString(R.string.new_weight), Toast.LENGTH_LONG).show();
                    newWeight = (StatisticsData) data.getParcelableExtra(getString(R.string.new_weight));
                    Log.d(TAG, "New Weight = " + newWeight);
                }
                break;
            case REQUEST_SETTINGS:
                if(resultCode==Activity.RESULT_OK) {
                    Toast.makeText(this, "Close Settings", Toast.LENGTH_LONG).show();

                }
        }
    }

}
