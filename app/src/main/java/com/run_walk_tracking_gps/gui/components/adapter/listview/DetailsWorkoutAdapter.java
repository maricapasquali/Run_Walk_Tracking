package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;

import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.enumerations.Sport;

import java.util.ArrayList;
import java.util.Map;

public class DetailsWorkoutAdapter extends BaseAdapter {

    private final static String TAG = DetailsWorkoutAdapter.class.getName();

    private Context context;
    private Map<Workout.Info, Object> details;

    public DetailsWorkoutAdapter(Context context, Map<Workout.Info, Object> map){
        this.context = context;
        this.details = map;
    }

    public void updateDetails(Workout workout){
        details = workout.details(true);
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Object getItem(int position) {
        return new ArrayList<>(details.entrySet()).get(position) ;
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

        final Map.Entry<Workout.Info, Object> entry = (Map.Entry<Workout.Info, Object>)getItem(position);

        final Workout.Info info = entry.getKey();
        viewHolder.infoWorkout.setText(info.getStrId());

        final Object det = entry.getValue();

        if(Workout.Info.isSport(info)){
            Sport sport = ((Sport)det);
            viewHolder.details.setText(sport.getStrId());
            viewHolder.details.setCompoundDrawablesWithIntrinsicBounds(
                    context.getDrawable(sport.getIconId()),
                    null,null,null);
        }
        else{
            viewHolder.details.setCompoundDrawablesWithIntrinsicBounds(
                    context.getDrawable(info.getIconId()),
                    null,null,null);

            viewHolder.details.setText((String)det);
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
