package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.gui.enumeration.InfoWorkout;
import com.run_walk_tracking_gps.model.enumerations.Sport;

import java.util.Arrays;

public class DetailsWorkoutAdapter extends BaseAdapter {

    private final static String TAG = DetailsWorkoutAdapter.class.getName();

    private Context context;

    private InfoWorkout[] infoWorkouts;

    private String[] detailsWorkout ;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DetailsWorkoutAdapter(Context context, String[] details) {
        this.context = context;
        this.infoWorkouts = InfoWorkout.values();
        this.detailsWorkout = details;

        Log.d(TAG, Arrays.toString(infoWorkouts));
        Log.d(TAG, Arrays.toString(detailsWorkout));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DetailsWorkoutAdapter(Context context, String[] details, boolean isModify) {
        this.context = context;
        this.infoWorkouts = isModify? InfoWorkout.infoWorkoutNoSpeed() :InfoWorkout.values();
        this.detailsWorkout = details;

        Log.d(TAG, Arrays.toString(infoWorkouts));
        Log.d(TAG, Arrays.toString(detailsWorkout));
    }

    public void updateDetails(InfoWorkout info, String detail){
        int index = Arrays.asList(infoWorkouts).indexOf(info);
        detailsWorkout[index] = detail;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDetails(Workout workout){
        detailsWorkout = workout.toArrayString();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return infoWorkouts.length;
    }

    @Override
    public Object getItem(int position) {
        return infoWorkouts[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView");
        final ListHolder viewHolder;
        final View view;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_item_details_workout, null);

            final TextView title = view.findViewById(R.id.detail_title);
            final TextView description = view.findViewById(R.id.detail_description);

            viewHolder = new ListHolder(title, description);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ListHolder) convertView.getTag();
        }

        viewHolder.infoWorkout.setText(infoWorkouts[position].getStrId());

        if(InfoWorkout.isSport(infoWorkouts[position]))
        {
            final Sport specificSport = Sport.valueOf(detailsWorkout[position]);

            viewHolder.details.setText(specificSport.getStrId());
            viewHolder.details.setCompoundDrawablesWithIntrinsicBounds(
                    context.getDrawable(specificSport.getIconId()),
                    null,null,null);

        }else{

            viewHolder.details.setText(detailsWorkout[position]);

            viewHolder.details.setCompoundDrawablesWithIntrinsicBounds(
                        context.getDrawable(infoWorkouts[position].getIconId()),
                        null,null,null);
        }

        return view;
    }

    private static class ListHolder {
        private TextView infoWorkout;
        private TextView details;

        private ListHolder(TextView titleItem, TextView description) {
            this.infoWorkout = titleItem;
            this.details = description;
        }
    }
}
