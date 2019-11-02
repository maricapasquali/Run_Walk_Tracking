package com.run_walk_tracking_gps.gui;

import android.app.Activity;
import android.content.Intent;

import android.support.annotation.Nullable;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.FieldDataBase;
import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.gui.fragments.HomeFragment;
import com.run_walk_tracking_gps.gui.fragments.StatisticsFragment;
import com.run_walk_tracking_gps.gui.fragments.WorkoutsFragment;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.gui.enumeration.FilterTime;
import com.run_walk_tracking_gps.gui.enumeration.Measure;
import com.run_walk_tracking_gps.utilities.DateUtilities;
import com.run_walk_tracking_gps.model.StatisticsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationActivity extends CommonActivity implements WorkoutsFragment.OnWorkOutSelectedListener,
        HomeFragment.OnStopWorkoutClickListener , WorkoutsFragment.OnManualAddClickedListener,
        StatisticsFragment.OnAddWeightListener, WorkoutsFragment.OnDeleteWorkoutClickedListener {

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
    private int id_workout_delete;

    private ArrayList<Workout> workouts;
    private ArrayList<StatisticsData> statisticsWeight = new ArrayList<>();

    @Override
    protected void init() {
        Log.d(TAG,"OnCreate");
        setContentView(R.layout.activity_application);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_runtracking_light);
        navigationBarBottom = findViewById(R.id.nav_bar);

        addFragment(new HomeFragment(), false);
        selectActiveFragment(HomeFragment.class);

        try {
            // REQUEST ALL WORKOUTS
            JSONObject bodyJson = new JSONObject();
            int id_user = Integer.valueOf(Preferences.getIdUserLogged(this));

            bodyJson.put(FieldDataBase.ID_USER.toName(), id_user);
            if(!HttpRequest.requestWorkouts(this, bodyJson , response -> {
                // TODO: 11/1/2019 CONTROLLO ERROR  REQUEST
                try {
                    if(HttpRequest.someError(response)){
                        Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
                    }else{
                        workouts = new Gson().fromJson(response.get("workouts").toString(), new TypeToken<List<Workout>>(){}.getType());
                        Log.e(TAG, workouts.toString());
                    }

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            })){
                Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
            }

            // REQUEST STATISTICS WEIGHT
            bodyJson = new JSONObject().put(FieldDataBase.ID_USER.toName(), id_user)
                                       .put(FieldDataBase.FILTER.toName(), FieldDataBase.WEIGHT.toName());
            if(!HttpRequest.requestStatistics(this, bodyJson, response -> {
                try {
                    //Log.e(TAG, response.toString());
                    if(HttpRequest.someError(response)){
                        Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
                    }else{

                        // TODO: 11/2/2019 MIGLIORARE
                        JSONArray array = (JSONArray)response.get("weights");
                        for(int i=0; i<array.length(); i++){
                            JSONObject s = (JSONObject)array.get(i);
                            StatisticsData statisticsData = new StatisticsData(DateUtilities.parseShortToDate((s.getString("date"))), s.getDouble("weight"));
                            statisticsData.setId(s.getInt(FieldDataBase.ID_WEIGHT.toName()));
                            statisticsWeight.add(statisticsData);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            })){
                Toast.makeText(this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void listenerAction() {
        navigationBarBottom.setOnNavigationItemSelectedListener(menuItem -> {
             if(!menuItem.isChecked()){
                 Log.d(TAG, "onSelectNavigationBottom");
                 switch (menuItem.getItemId()) {
                     case R.id.workouts:
                         addFragment(WorkoutsFragment.createWithArgument(workouts),false);
                         break;
                     case R.id.home:
                         addFragment(new HomeFragment(),false);
                         break;
                     case R.id.statistics:
                         addFragment(StatisticsFragment.createWithArgument(workouts, statisticsWeight), false);
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
        if(getSupportActionBar()!= null){
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

    //Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (requestCode){
            case REQUEST_CHANGED_DETAILS:
                if(resultCode==Activity.RESULT_OK){
                    
                    workoutChanged = (Workout)data.getParcelableExtra(getString(R.string.changed_workout));
                    if(workoutChanged!=null){
                        Toast.makeText(this, R.string.changed_workout, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Workout changed = " +workoutChanged);
                    }

                    id_workout_delete = data.getIntExtra(getString(R.string.delete_workout), 0);
                    if(id_workout_delete>0){
                        Toast.makeText(this, R.string.delete_workout, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Workout deleted = " +id_workout_delete);
                    }
                }
                break;
            case REQUEST_SUMMARY:
                if(resultCode==Activity.RESULT_OK) {
                    Toast.makeText(this, getString(R.string.summary_workout), Toast.LENGTH_LONG).show();
                    final Workout newAutoWorkout = (Workout) data.getParcelableExtra(getString(R.string.new_workout));
                    workouts.add(0,newAutoWorkout);
                    workouts.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                    selectActiveFragment(WorkoutsFragment.class);
                    Log.d(TAG, "New Auto Workout = " + newAutoWorkout);
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
