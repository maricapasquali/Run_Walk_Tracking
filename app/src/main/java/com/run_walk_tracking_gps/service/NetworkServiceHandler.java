package com.run_walk_tracking_gps.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;

public class NetworkServiceHandler
        //implements ServiceConnection
{

    private static final String TAG = NetworkServiceHandler.class.getName();

    private static NetworkServiceHandler handler;

    private final Context context;
    private Intent intent;

    private NetworkServiceHandler(Context context){
        this.context = context;
        intent = new Intent(context, NetworkService.class);
        Log.e(TAG, intent.toString());
    }

    /**
     *
     * @param context
     * @param action
     * @param filter
     * @param data json string format
     * @return
     */
    public static synchronized NetworkServiceHandler getInstance(Context context, String action,String filter, String data){
        if(handler==null)
            handler = new NetworkServiceHandler(context.getApplicationContext());

        return handler.setAction(action).setFilter(filter).setDataJson(data);
    }

    private NetworkServiceHandler setAction(String action){
        intent.setAction(action);
        Log.e(TAG, intent.toString());
        return handler;
    }

    private NetworkServiceHandler setFilter(String filter){
        intent.putExtra(KeysIntent.FILTER, filter);
        return handler;
    }

    private NetworkServiceHandler setDataJson(String data){
        intent.putExtra(KeysIntent.DATA_REQUEST, data);
        return handler;
    }

    public void startService(){
        //context.bindService(intent, this,  Context.BIND_AUTO_CREATE);
        context.startService(intent);
    }


    /*@Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        final NetworkService.LocalBinder binder = (NetworkService.LocalBinder) service;
        NetworkService nService = binder.getService();
        //nService.setResponseListener(response -> {Log.e(TAG, response.toString());context.unbindService(this);});
        nService.setServiceConnection(this);
        context.startService(intent);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }*/
}
