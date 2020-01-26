package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;

import com.run_walk_tracking_gps.model.Workout;
import com.run_walk_tracking_gps.model.enumerations.Sport;

import java.util.ArrayList;
import java.util.Map;

public class DetailsWorkoutAdapter extends BaseAdapter {

    private final static String TAG = DetailsWorkoutAdapter.class.getName();
    private View.OnClickListener listener;

    private Context context;
    private Map<Workout.Info, Object> details;

    public DetailsWorkoutAdapter(Context context, Map<Workout.Info, Object> map){
        this.context = context;
        this.details = map;
    }

    public DetailsWorkoutAdapter(Context context, Map<Workout.Info, Object> map, View.OnClickListener listener){
        this(context,map);
        this.listener = listener;
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

            final TextInputLayout title = view.findViewById(R.id.detail_title);
            final TextInputEditText description = view.findViewById(R.id.detail_description);
            if(listener!=null) description.setOnClickListener(listener);
            viewHolder = new ListHolder(title, description);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ListHolder) convertView.getTag();
        }

        final Map.Entry<Workout.Info, Object> entry = (Map.Entry<Workout.Info, Object>)getItem(position);

        final Workout.Info info = entry.getKey();
        viewHolder.infoWorkout.setHint(context.getString(info.getStrId()));

        final Object det = entry.getValue();
        Drawable icon;
        if(Workout.Info.isSport(info)){
            Sport sport = ((Sport)det);
            viewHolder.details.setText(sport.getStrId());
            icon = context.getDrawable(sport.getIconId());
        }
        else{
            icon = context.getDrawable(info.getIconId());
            viewHolder.details.setText((String)det);
        }
        if(icon!=null){
            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            viewHolder.details.setCompoundDrawablesWithIntrinsicBounds(icon,null,null,null);
        }

        return view;
    }

    private static class ListHolder {
        private TextInputLayout infoWorkout;
        private TextInputEditText details;

        private ListHolder(TextInputLayout titleItem, TextInputEditText description) {
            this.infoWorkout = titleItem;
            this.details = description;
        }
    }
}
