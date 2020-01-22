package com.run_walk_tracking_gps.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;
import com.run_walk_tracking_gps.utilities.ImageFileHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.RequiresApi;

/**
 * Service -> SEND DATA TO SERVER
 */
public class NetworkService extends Service {

    private final String TAG = NetworkService.class.getName();
    private final IBinder binder = new NetworkService.LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public NetworkService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NetworkService.this;
        }
    }

    private Context context;
    //private Response.Listener<JSONObject> responseListener;

    private ServiceConnection serviceConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread threadCUD= new Thread(()->{
            if(intent!=null){
                try {
                    final String filter = intent.getStringExtra(KeysIntent.FILTER);
                    final JSONObject data = new JSONObject(intent.getStringExtra(KeysIntent.DATA_REQUEST));
                    if(data.has(UserDescriptor.ID_USER)) data.remove(UserDescriptor.ID_USER);
                    if(filter.equals(NetworkHelper.Constant.USER) && !data.isNull(NetworkHelper.Constant.IMAGE)){
                        // COMPRESSIONE IMMAGINE
                        final String name_img = data.getJSONObject(NetworkHelper.Constant.IMAGE).getString(ImageProfileDescriptor.NAME);
                        final String path = ImageFileHelper.create(context).getImage(name_img).getPath();
                        String encode = BitmapUtilities.BitMapToString(BitmapFactory.decodeFile(path));

                        data.getJSONObject(NetworkHelper.Constant.IMAGE).put(NetworkHelper.Constant.IMG_ENCODE, encode);
                    }
                    Log.e(TAG, data.toString());
                    if(intent.getAction()!=null){

                        NetworkHelper.HttpRequest.getInstance(context).requestCUD(intent.getAction(), filter , data , response -> {
                            Log.e(TAG, "Response after "+ intent.getAction() +" : "+ response.toString());
                            context.unbindService(serviceConnection);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        threadCUD.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /*public void setResponseListener(Response.Listener<JSONObject> responseListener) {
        this.responseListener = responseListener;
    }*/
    public void setServiceConnection(ServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

}