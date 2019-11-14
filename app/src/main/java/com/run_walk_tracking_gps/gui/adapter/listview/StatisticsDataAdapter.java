package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.model.Measure;
import com.run_walk_tracking_gps.model.StatisticsData;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsDataAdapter extends BaseAdapter {

    private final static String TAG = StatisticsDataAdapter.class.getName();

    private Context context;
    private List<StatisticsData> data_filtered;

    private Measure.Type measure;


    public StatisticsDataAdapter(Context context, List<StatisticsData> map, Measure.Type measure){
        this.context = context;
        this.data_filtered = map;
        this.measure=measure;
    }

    public void updateStatisticsData(List<StatisticsData> list, Measure.Type measure){
        this.data_filtered.clear();
        this.data_filtered.addAll(list);
        this.measure = measure;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data_filtered.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return data_filtered.get(position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position == 0) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_graph, null);

            final GraphView graphView = (GraphView) convertView.findViewById(R.id.statistics_graph);

            final LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

            data_filtered.stream().sorted((d1, d2) -> d1.getDate().compareTo(d2.getDate())).forEach(data ->
                    series.appendData(new DataPoint(data.getDate(),data.getValue()),true, getCount()-1));
            graphView.addSeries(series);


            // set date label formatter
            graphView.getGridLabelRenderer().setPadding(100);
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(context));
            graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            // set manual x bounds to have nice steps
            final List<Date> x = data_filtered.stream().map(StatisticsData::getDate).collect(Collectors.toList());
            if(!x.isEmpty()){
                graphView.getViewport().setMinX(x.get(data_filtered.size()-1).getTime());
                graphView.getViewport().setMaxX(x.get(0).getTime());
                graphView.getViewport().setXAxisBoundsManual(true);

                // as we use dates as labels, the human rounding to nice readable numbers
                graphView.getGridLabelRenderer().setHumanRounding(false);
            }

        } else {

            convertView = LayoutInflater.from(context).inflate(R.layout.custom_item_statistics_filtered, null);

            final TextView text_data_filtered = convertView.findViewById(R.id.statistics_data);
            final TextView date = convertView.findViewById(R.id.statistics_date);

            // TODO: 11/3/2019 GESTIONE CONVERSIONE
            
            text_data_filtered.setText(data_filtered.get(position-1).getMeasure().toString(context));
            date.setText(data_filtered.get(position-1).getDateStr());
        }

        return convertView;
    }

}
