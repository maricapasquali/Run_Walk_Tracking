package com.run_walk_tracking_gps.gui;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.adapter.listview.NewInformationAdapter;

public abstract class NewInformationActivity extends CommonActivity {

    private int title;

    private ListView listView;

    private NewInformationAdapter adapter;
    private OnAddInfoListener onAddInfoListener;

    public NewInformationActivity(int titleBar) {
        this.title = titleBar;
    }


    @Override
    protected void init() {
        setContentView(R.layout.activity_add_info);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.info_to_add);

        try {
            onAddInfoListener = (OnAddInfoListener)this;
        }catch (ClassCastException e){
            throw new ClassCastException(this + " must implement NewInformationActivity.OnAddInfoListener");
        }

        adapter = onAddInfoListener.getAdapterListView();

        if(adapter==null)
            throw new NullPointerException("Adapter must be other than null");

        listView.setAdapter(adapter);
        setModel();
    }


    protected abstract void setModel();

    @Override
    protected void listenerAction() {

        listView.setOnItemClickListener((parent, view, position, id) ->
                onAddInfoListener.onSetInfo(parent, view, position, id));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_info:
                onAddInfoListener.onClickAddInfo();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnAddInfoListener{
        NewInformationAdapter getAdapterListView();
        void onSetInfo(AdapterView<?> parent, View view, int position, long id);
        void onClickAddInfo();
    }
}
