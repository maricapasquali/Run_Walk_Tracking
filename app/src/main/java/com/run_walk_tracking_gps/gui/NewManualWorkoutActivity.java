package com.run_walk_tracking_gps.gui;

import android.content.Intent;

import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.dao.DaoFactory;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.db.tables.WorkoutDescriptor;
import com.run_walk_tracking_gps.exception.DataException;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewInformationAdapter;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewManualWorkoutAdapter;
import com.run_walk_tracking_gps.gui.components.dialog.DateTimePickerDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DistanceDialog;
import com.run_walk_tracking_gps.gui.components.dialog.DurationDialog;
import com.run_walk_tracking_gps.gui.components.dialog.EnergyDialog;
import com.run_walk_tracking_gps.gui.components.dialog.SportDialog;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.service.NetworkServiceHandler;
import com.run_walk_tracking_gps.utilities.EnumUtilities;
import com.run_walk_tracking_gps.utilities.ColorUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class NewManualWorkoutActivity extends NewInformationActivity {

    private final static String TAG = NewManualWorkoutActivity.class.getName();

    private Workout workout;

    public NewManualWorkoutActivity() {
        super(R.string.workout);
    }

    @Override
    protected void setModel() {
        workout = new Workout(this);
    }

    @Override
    public NewInformationAdapter getAdapterListView() {
        return new NewManualWorkoutAdapter(this, onSetInfo());
    }

    @Override
    public View.OnFocusChangeListener onSetInfo() {
        return (view, hasFocus) ->{
            if(hasFocus){
                final TextInputEditText detail = (TextInputEditText)view;
                final TextInputLayout detailTitle = (TextInputLayout) detail.getParent().getParent();

                Workout.Info title = null;

                try{
                    title = (Workout.Info) EnumUtilities.getEnumFromString(Workout.Info.class , this, detailTitle.getHint().toString());
                }catch (Exception ignored){
                }
                Log.d(TAG, detailTitle.getHint().toString());

                switch (title){
                    case DATE:
                        DateTimePickerDialog.createDateTimePicker(NewManualWorkoutActivity.this, (date, calendar) -> {
                            workout.setDate(calendar.getTime());
                            detail.setText(date);
                        }).show();
                        break;
                    case SPORT:
                        SportDialog.create(NewManualWorkoutActivity.this, detail.getText(), (val, description) -> {
                            detail.setText(val.getStrId());
                            detail.setCompoundDrawablesWithIntrinsicBounds(ColorUtilities.darkIcon(this, val.getIconId()), null, null, null);
                            workout.setSport(val);
                        }).show();
                        break;
                    case TIME:
                        DurationDialog.create(NewManualWorkoutActivity.this, (durationMeasure) -> {
                            if(durationMeasure!=null){
                                workout.getDuration().setValue(true, durationMeasure.getValue(true));
                                detail.setText(durationMeasure.toString());
                            }
                            workout.setMiddleSpeed();
                        }).show();
                        break;
                    case DISTANCE:
                        DistanceDialog.create(NewManualWorkoutActivity.this, (distanceMeasure) -> {
                            if(distanceMeasure==null){
                                detail.setText(R.string.no_available_abbr);
                                workout.getDistance().setValue(true, 0d);
                            }else{
                                detail.setText(distanceMeasure.toString());
                                workout.getDistance().setValue(false, distanceMeasure.getValue(false));
                                setAutoCalories();
                            }
                            workout.setMiddleSpeed();
                        }).show();
                        break;
                    case CALORIES:
                        EnergyDialog.create(NewManualWorkoutActivity.this, (caloriesMeasure)  -> {
                            if(caloriesMeasure==null){
                                detail.setText(R.string.no_available_abbr);
                                setAutoCalories();
                                //workout.getCalories().setValue(true, 0d);
                            }else{
                                detail.setText(caloriesMeasure.toString());
                                workout.getCalories().setValue(false, caloriesMeasure.getValue(false));
                            }
                        }).show();
                        break;
                }
            }
        };
    }

    private void setAutoCalories(){
        if(workout.getCalories().getValue(true)==0){
            workout.getCalories().setValue(false, workout.getSport()!=null && workout.getDistance().getValue(true)>0 ?
                                workout.getSport().getConsumedEnergy(DaoFactory.getInstance(this).getWeightDao().getLast(),
                                                    workout.getDistance().getValue(true)) : 0d);
        }
    }

    @Override
    public void onClickAddInfo() {
        // Set workout
        try{
            Log.d(TAG, "" +workout.getDistance().getValue(false));
            if(!workout.isMinimalSet())
                throw new DataException(this, Workout.class);

            final JSONObject bodyJson = new JSONObject().put(UserDescriptor.ID_USER, Preferences.Session.getIdUser(this))
                                                        .put(WorkoutDescriptor.SPORT, workout.getSport())
                                                        .put(WorkoutDescriptor.DURATION, workout.getDuration().getValue(true))
                                                        .put(WorkoutDescriptor.DATE, workout.getUnixTime());
            if(!Measure.isNullOrEmpty(workout.getDistance()))
                bodyJson.put(WorkoutDescriptor.DISTANCE, workout.getDistance().getValue(true));
            if(!Measure.isNullOrEmpty(workout.getCalories()))
                bodyJson.put(WorkoutDescriptor.CALORIES, workout.getCalories().getValue(true));


            long id_workout = DaoFactory.getInstance(this).getWorkoutDao().insert(bodyJson);
            if(id_workout!=-1){
                Preferences.Session.update(this);

                NetworkServiceHandler.getInstance(this, NetworkHelper.Constant.INSERT,
                        NetworkHelper.Constant.WORKOUT, bodyJson.put(WorkoutDescriptor.ID_WORKOUT, id_workout).toString())
                                     .startService();

                setResult(RESULT_OK, new Intent());
            }else {
                setResult(RESULT_CANCELED, new Intent());
            }
            finish();

        }catch (JSONException je){
            je.printStackTrace();
        }
        catch (DataException e) {
            e.alert();
        }
    }

}
