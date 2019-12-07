package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.fragments.HomeFragment;
import com.run_walk_tracking_gps.gui.fragments.StatisticsFragment;
import com.run_walk_tracking_gps.gui.fragments.WorkoutsFragment;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.StatisticsData;

import java.util.ArrayList;

public class ApplicationActivity extends CommonActivity
        implements  WorkoutsFragment.OnWorkOutSelectedListener,
                    WorkoutsFragment.OnDeleteWorkoutClickedListener,
                    WorkoutsFragment.OnManualAddClickedListener,
                    HomeFragment.OnStopWorkoutClickListener ,
                    HomeFragment.OnBlockScreenClickListener,
                    StatisticsFragment.OnWeightListener {

    private final static String TAG = ApplicationActivity.class.getName();
    private final static int REQUEST_SETTINGS = 1;
    private final static int REQUEST_CHANGED_DETAILS = 2;
    private final static int REQUEST_SUMMARY = 3;
    private final static int REQUEST_NEW_WORKOUT = 4;
    private final static int REQUEST_NEW_WEIGHT = 5;
    private final static int REQUEST_MODIFY_WEIGHT = 6;

    private BottomNavigationView navigationBarBottom;
    private Menu menuApp;

    private StatisticsData newWeight;
    private Workout newWorkout;
    private Workout workoutChanged;
    private int id_workout_delete;

    private StatisticsData statisticsData;
    private int id_weight_delete;

    private ArrayList<Workout> workouts = new ArrayList<>();
    private ArrayList<StatisticsData> statisticsWeight = new ArrayList<>();

    @Override
    protected void init(Bundle savedInstanceState) {
        Log.d(TAG,"init");

        setContentView(R.layout.activity_application);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_runtracking_light);

        setNavigationBarBottom();

        if(getIntent()!=null){
            workouts = getIntent().getParcelableArrayListExtra(KeysIntent.WORKOUTS);
            statisticsWeight = getIntent().getParcelableArrayListExtra(KeysIntent.WEIGHTS);
            if(workouts!=null && statisticsWeight!=null){
                workouts.forEach(w -> w.setContext(this));
                statisticsWeight.forEach(s -> s.setContext(this));

                if(savedInstanceState==null){
                    selectActiveFragment(HomeFragment.class);
                }else {
                   setTitleAndLogoActionBar(getSupportFragmentManager().findFragmentByTag(TAG).getClass());
                }
            }
            if(workouts==null) workouts = new ArrayList<>();
            if(statisticsWeight==null) statisticsWeight = new ArrayList<>();
            // TODO: 11/26/2019 PER TEST MAPROUTE
            // StatisticsData statisticsData = StatisticsData.create(this, Measure.Type.WEIGHT);statisticsData.setValue(70.0);
            // statisticsWeight.add(statisticsData);
        }
    }

    private void setNavigationBarBottom(){
        navigationBarBottom = findViewById(R.id.nav_bar);

        navigationBarBottom.setOnNavigationItemSelectedListener(menuItem -> {
            Log.e(TAG, "setOnNavigationItemSelectedListener");
            final int previousItem = navigationBarBottom.getSelectedItemId();
            final int nextItem = menuItem.getItemId();
            if(previousItem!=nextItem){
                switch (menuItem.getItemId()) {
                    case R.id.workouts:
                        addFragment(WorkoutsFragment.createWithArgument(workouts),false);
                        break;
                    case R.id.home:
                        addFragment(HomeFragment.createWithArgument(statisticsWeight.get(0).getValue()),false);
                        break;
                    case R.id.statistics:
                        addFragment(StatisticsFragment.createWithArgument(workouts, statisticsWeight), false);
                        break;
                    default:
                        return true;
                }
            }
            return true;
        });

    }


    @Override
    protected void listenerAction() {
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
        if(getSupportActionBar()!= null){
            getSupportActionBar().setLogo(fragment_class==HomeFragment.class ? R.drawable.ic_runtracking_light : 0);
            getSupportActionBar().setTitle("  " + getString(fragment_class==HomeFragment.class ? R.string.app_name :
                    fragment_class==WorkoutsFragment.class? R.string.workouts :
                            R.string.statistics));
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
                Intent intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_SETTINGS);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState : " + outState);
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_fragments_application);
        if(fragment instanceof HomeFragment && ((HomeFragment)fragment).isWorkoutRunning()){
            moveTaskToBack(true);
        } else{
            finish();
            System.exit(0);
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
        intentSummary.putExtra(KeysIntent.SUMMARY, workout);
        intentSummary.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intentSummary, REQUEST_SUMMARY);
    }

    @Override
    public void onBlockScreenClickListener(boolean isClickable) {
        menuApp.findItem(R.id.setting).setEnabled(isClickable);
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

    // Delete Workout Listener
    @Override
    public int idWorkoutDeleted() {
        return id_workout_delete;
    }

    @Override
    public void resetWorkoutDelete() {
       id_workout_delete = 0;
    }

    // StatisticsFragment Listener
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

    @Override
    public void resetAddWeight() {
        newWeight =null;
    }

    @Override
    public void modifyWeight(StatisticsData statisticsData) {
        final Intent intent = new Intent(this, ModifyWeightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(KeysIntent.MODIFY_WEIGHT,statisticsData);
        intent.putExtra(KeysIntent.IS_LAST_WEIGHT, statisticsWeight.size()==1);
        startActivityForResult(intent, REQUEST_MODIFY_WEIGHT);
    }

    @Override
    public StatisticsData changedWeight() {
        return statisticsData;
    }

    @Override
    public void resetChangedWeight() {
        statisticsData =null;
    }

    @Override
    public int deletedWeight() {
        return id_weight_delete;
    }

    @Override
    public void resetDeletedWeight() {
        id_weight_delete=0;
    }

    //Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (requestCode){
            case REQUEST_CHANGED_DETAILS:
                if(resultCode==Activity.RESULT_OK){
                    
                    workoutChanged = (Workout)data.getParcelableExtra(KeysIntent.CHANGED_WORKOUT);
                    if(workoutChanged!=null){
                        workoutChanged.setContext(this);
                        //Toast.makeText(this, ConstantIntent.CHANGED_WORKOUT, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Workout changed = " +workoutChanged);
                    }

                    id_workout_delete = data.getIntExtra(KeysIntent.DELETE_WORKOUT, 0);
                    if(id_workout_delete>0){
                        //Toast.makeText(this, R.string.delete_workout, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Workout deleted = " +id_workout_delete);
                    }
                }
                break;
            case REQUEST_SUMMARY:
                if(resultCode==Activity.RESULT_OK) {
                    //Toast.makeText(this, getString(R.string.summary_workout), Toast.LENGTH_LONG).show();
                    final Workout newAutoWorkout = (Workout) data.getParcelableExtra(KeysIntent.NEW_WORKOUT);
                    newAutoWorkout.setContext(this);
                    workouts.add(0,newAutoWorkout);
                    workouts.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                    selectActiveFragment(WorkoutsFragment.class);
                    Log.d(TAG, "New Auto Workout = " + newAutoWorkout);
                }
                break;
            case REQUEST_NEW_WORKOUT:
                if(resultCode==Activity.RESULT_OK) {
                    //Toast.makeText(this, ConstantIntent.NEW_WORKOUT_MANUAL, Toast.LENGTH_LONG).show();
                    newWorkout = (Workout) data.getParcelableExtra(KeysIntent.NEW_WORKOUT_MANUAL);
                    newWorkout.setContext(this);
                    Log.d(TAG, "New Manual Workout = " + newWorkout);
                }
                break;

            case REQUEST_NEW_WEIGHT:
                if(resultCode==Activity.RESULT_OK) {
                    //Toast.makeText(this, getString(R.string.new_weight), Toast.LENGTH_LONG).show();
                    newWeight = (StatisticsData) data.getParcelableExtra(KeysIntent.NEW_WEIGHT);
                    newWeight.setContext(this);
                    Log.d(TAG, "New Weight = " + newWeight);
                }
                break;

            case REQUEST_MODIFY_WEIGHT:
                if(resultCode==Activity.RESULT_OK) {
                    statisticsData = (StatisticsData)data.getParcelableExtra(KeysIntent.CHANGED_WEIGHT);
                    if(statisticsData!=null){
                        statisticsData.setContext(this);
                        //Toast.makeText(this, R.string.modify_weight, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Weight changed = " +statisticsData);
                    }

                    id_weight_delete = data.getIntExtra(KeysIntent.DELETE_WEIGHT, 0);
                    Log.d(TAG, "Weight deleted = " +id_weight_delete);
                    if(id_weight_delete>0){
                        //Toast.makeText(this, R.string.delete_weight, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Weight deleted = " +id_weight_delete);
                    }
                }
            case REQUEST_SETTINGS:
                if(resultCode==Activity.RESULT_OK) {
                    //Toast.makeText(this, "Close Settings", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Close Settings");
                }
                break;
        }
    }
}
