package com.run_walk_tracking_gps.gui.adapter.listview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.run_walk_tracking_gps.R;

import java.util.Arrays;
import java.util.List;

public abstract class NewInformationAdapter<T> extends BaseAdapter {

    private final String TAG = NewInformationAdapter.class.getName();
    private Context context;

    private List<T> info;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NewInformationAdapter(Context _context_, T[] info) {
        this.context = _context_;
        this.info = Arrays.asList(info);
        Log.d(TAG, Arrays.toString(info));
    }

    protected Context getContext(){
        return context;
    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Object getItem(int position) {
        return info.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ListHolder viewHolder;
        final View view;

        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.custom_item_details_workout, null);


            final TextView title = view.findViewById(R.id.detail_title);
            final TextView detail = view.findViewById(R.id.detail_description);

            viewHolder = new ListHolder(title, detail);

            view.setTag(viewHolder);
        } else {

            view = convertView;

            viewHolder = (ListHolder) convertView.getTag();
        }

        addInfo(viewHolder.title, viewHolder.detail, info.get(position));
        return view;
    }

    protected abstract void addInfo(TextView title, TextView detail, T item);


    private class ListHolder {

        private TextView title;
        private TextView detail;

        @RequiresApi(api = Build.VERSION_CODES.O)
        private ListHolder(TextView title, TextView detail) {
            this.title = title;
            this.detail = detail;

        }
    }
}
