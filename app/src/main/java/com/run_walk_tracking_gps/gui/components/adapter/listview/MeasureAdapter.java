package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MeasureAdapter extends BaseAdapter {

    private Context context;
    private static RadioGroup.OnCheckedChangeListener listener;

    private LinkedHashMap<Measure.Type, Measure.Unit> defaultMeasure;

    public MeasureAdapter(Context context, RadioGroup.OnCheckedChangeListener listener, LinkedHashMap<Measure.Type, Measure.Unit> defaultMeasure) {
        this.context = context;
        MeasureAdapter.listener = listener;
        this.defaultMeasure = defaultMeasure;
    }

    @Override
    public int getCount() {
        return defaultMeasure.size();
    }

    @Override
    public Object getItem(int position) {
        return new ArrayList<>(defaultMeasure.entrySet()).get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Map.Entry<Measure.Type, Measure.Unit> entry =(Map.Entry<Measure.Type, Measure.Unit>)getItem(position);
        final Measure.Type measure = entry.getKey();
        final Measure.Unit unit = entry.getValue();
        final ListHolder viewHolder;
        final View view;

        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.custom_item_measure_unit, null);

            final TextView textViewMeasure = view.findViewById(R.id.measure);
            final RadioButton unit_1 = view.findViewById(R.id.unit_1);
            final RadioButton unit_2 = view.findViewById(R.id.unit_2);
            final RadioGroup choose_unit = (RadioGroup) view.findViewById(R.id.choose_unit);

            viewHolder = new ListHolder(textViewMeasure, unit_1, unit_2, choose_unit);

            view.setTag(viewHolder);
        } else {

            view = convertView;

            viewHolder = (ListHolder) convertView.getTag();
        }


        viewHolder.textViewMeasure.setText(measure.getStrId());
        viewHolder.textViewMeasure.setCompoundDrawablesWithIntrinsicBounds(
                context.getDrawable(measure.getIconId()), null, null, null);
        viewHolder.textViewMeasure.getCompoundDrawables()[0].setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);

        final Measure.Unit[] measureUnits = measure.getMeasureUnit();
        if(measureUnits.length==2){
            viewHolder.unit_1.setText(measureUnits[0].getStrId());
            viewHolder.unit_2.setText(measureUnits[1].getStrId());
        }
        if(unit!=null){
            if(unit.equals(measure.getMeasureUnitDefault())){
                viewHolder.unit_1.setChecked(true);
            }else{
                viewHolder.unit_2.setChecked(true);
            }
        }
        return view;
    }

    private static class ListHolder {

        private TextView textViewMeasure;
        private RadioButton unit_1;
        private RadioButton unit_2;

        private ListHolder(TextView textViewMeasure, RadioButton unit_1,RadioButton unit_2, RadioGroup choose_unit) {
            this.textViewMeasure = textViewMeasure;
            this.unit_1 = unit_1;
            this.unit_2 = unit_2;
            choose_unit.setOnCheckedChangeListener(listener);
        }
    }

}

