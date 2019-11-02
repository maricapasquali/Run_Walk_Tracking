package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.dialog.DurationDialog;
import com.run_walk_tracking_gps.utilities.DateUtilities;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.utilities.DurationUtilities;

import java.util.List;

public class WorkoutsAdapter extends ArrayAdapter<Workout> {

    private String ND;


    public WorkoutsAdapter(Context context, List<Workout> objects) {
        super(context, R.layout.custom_item_workouts, objects);
        ND = context.getString(R.string.no_available_abbr);
    }

    public void updateWorkouts(List<Workout> list){
        this.clear();
        this.addAll(list);
        this.notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Workout workout = getItem(position);
        final ListHolder viewHolder;
        final View view;


        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.custom_item_workouts, null);

            final TextView date = view.findViewById(R.id.workout_date);
            final TextView time = view.findViewById(R.id.workout_time);
            final TextView distance = view.findViewById(R.id.workout_distance);
            final TextView calories = view.findViewById(R.id.workout_calories);

            viewHolder = new ListHolder(date, time, distance, calories);

            view.setTag(viewHolder);
        } else {

            view = convertView;

            viewHolder = (ListHolder) convertView.getTag();
        }

        viewHolder.date.setText(DateUtilities.parseFullToString(workout.getDate()));
        viewHolder.time.setText(DurationUtilities.format(workout.getDuration()));

        viewHolder.distance.setText(workout.getDistance() > 0f ?  String.valueOf(workout.getDistance()): ND);
        viewHolder.calories.setText(workout.getCalories() > 0f ? String.valueOf(workout.getCalories()): ND);

        return view;
    }

    private static class ListHolder {

        private TextView date;
        private TextView time;
        private TextView distance;
        private TextView calories;

        private ListHolder(TextView date, TextView time,TextView distance,TextView calories) {
            this.date = date;
            this.time = time;
            this.distance = distance;
            this.calories = calories;
        }
    }
}

