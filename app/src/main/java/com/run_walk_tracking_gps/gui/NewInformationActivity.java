package com.run_walk_tracking_gps.gui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.components.adapter.listview.NewInformationAdapter;

public abstract class NewInformationActivity extends CommonActivity {

    private int title;

    private ListView listView;

    private NewInformationAdapter adapter;
    //private OnAddInfoListener onAddInfoListener;

    public NewInformationActivity(int titleBar) {
        this.title = titleBar;
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_info);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.info_to_add);

        adapter = getAdapterListView();

        if(adapter==null)
            throw new NullPointerException("Adapter must be other than null");

        listView.setAdapter(adapter);
        setModel();
    }

    protected abstract void setModel();

    protected abstract NewInformationAdapter getAdapterListView();

    protected abstract View.OnClickListener onSetInfo();

    protected abstract void onClickAddInfo();

    @Override
    protected void listenerAction() {
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
                //onAddInfoListener.onClickAddInfo();
                onClickAddInfo();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
