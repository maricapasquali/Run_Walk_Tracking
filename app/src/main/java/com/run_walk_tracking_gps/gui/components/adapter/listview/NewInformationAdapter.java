package com.run_walk_tracking_gps.gui.components.adapter.listview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.utilities.ColorUtilities;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.RequiresApi;

public abstract class NewInformationAdapter<T> extends BaseAdapter {

    private final String TAG = NewInformationAdapter.class.getName();
    private Context context;
    private View.OnFocusChangeListener listener;
    private List<T> info;

    @RequiresApi(api = Build.VERSION_CODES.N)
    NewInformationAdapter(Context _context_, T[] info, View.OnFocusChangeListener onClickListener) {
        this.context = _context_;
        this.info = Arrays.asList(info);
        this.listener = onClickListener;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ListHolder viewHolder;
        final View view;

        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.custom_item_details_workout, null);

            final TextInputLayout title = view.findViewById(R.id.detail_title);
            final TextInputEditText detail = view.findViewById(R.id.detail_description);
            if(listener!=null){
                detail.setOnFocusChangeListener(listener);
                detail.setOnClickListener(v -> listener.onFocusChange(v, true));
            }
            viewHolder = new ListHolder(title, detail);

            view.setTag(viewHolder);
        } else {

            view = convertView;

            viewHolder = (ListHolder) convertView.getTag();
        }

        addInfo(viewHolder.title, viewHolder.detail, info.get(position), position);
        return view;
    }

    protected abstract void addInfo(TextInputLayout title, TextInputEditText detail, T item, int position);


    Drawable darkIcon(int icon){
        return ColorUtilities.darkIcon(context, icon);
    }

    private class ListHolder {

        private TextInputLayout title;
        private TextInputEditText detail;


        private ListHolder(TextInputLayout title, TextInputEditText detail) {
            this.title = title;
            this.detail = detail;

        }
    }
}
