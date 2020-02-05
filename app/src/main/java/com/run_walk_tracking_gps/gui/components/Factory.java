package com.run_walk_tracking_gps.gui.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.ImagePickerContract;
import com.myhexaville.smartimagepicker.OnImagePickedListener;
import com.run_walk_tracking_gps.KeysIntent;
import com.run_walk_tracking_gps.R;
import com.run_walk_tracking_gps.gui.components.dialog.ZoomImageDialog;

import androidx.appcompat.widget.AppCompatImageView;

public class Factory {

    public static class CustomImageView extends AppCompatImageView implements View.OnClickListener {

        private static final String TAG = CustomImageView.class.getSimpleName();
        private static final float RADIUS = 500.0f;

        public CustomImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
            Log.d(TAG, CustomImageView.class.getName());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final Path clipPath = new Path();
            final RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
            clipPath.addRoundRect(rect, RADIUS, RADIUS, Path.Direction.CW);
            canvas.clipPath(clipPath);
            setOnClickListener(this);
            super.onDraw(canvas);
        }

        @Override
        public void onClick(View v) {
            Log.e(TAG, "onClick");
            try {
                ZoomImageDialog.create(getContext(), ((BitmapDrawable) this.getDrawable()).getBitmap()).show();
            }catch (ClassCastException e){
                Log.e(TAG, "Immagine vuota");
            }
        }

    }

    public static class CustomTakePhotoButton extends FloatingActionButton implements View.OnClickListener  {
        private static final String ERROR = "Must Implements OnTakePhotoListener";
        private static final String TAG = CustomTakePhotoButton.class.getSimpleName();
        private ImagePicker picker;

        private OnTakePhotoListener onTakePhotoListener;

        public CustomTakePhotoButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ImagePickerContract getImagePiker() {
            return picker;
        }

        public void onTakePhotoListener(OnTakePhotoListener onTakePhotoListener){
            if(onTakePhotoListener==null || onTakePhotoListener.getActivity()==null || onTakePhotoListener.setonClickListener()==null)
                throw new NullPointerException(ERROR);

            this.onTakePhotoListener = onTakePhotoListener;
            setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            picker = new ImagePicker(onTakePhotoListener.getActivity(),null ,
                    onTakePhotoListener.setonClickListener()).setWithImageCrop(1,1);
            picker.choosePicture(true);
        }

        public interface OnTakePhotoListener{
            Activity getActivity();
            OnImagePickedListener setonClickListener();
        }
    }

    public static class CustomImageProfile extends RelativeLayout {
        private static final String TAG = CustomImageProfile.class.getSimpleName();

        private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

        private static final String TAKE_PHOTO_ATTR = "take_photo";
        private static final boolean TAKE_PHOTO_DEFAULT = false;

        public CustomImageProfile(Context context, AttributeSet attrs) {
            super(context);
            final View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                                       .inflate(R.layout.custom_image_profile, this);

            boolean isTakePhoto = attrs.getAttributeBooleanValue(NAMESPACE, TAKE_PHOTO_ATTR, TAKE_PHOTO_DEFAULT);
            view.findViewById(R.id.take_photo).setVisibility(isTakePhoto ? View.VISIBLE : View.GONE );
            Log.d(TAG, "Take Photo = " + isTakePhoto);
        }
    }

    public static class CustomPolylineOptions{
        private static int WIDTH_DEFAULT = 15;
        private static int COLOR_DEFAULT = Color.BLUE;

        public static PolylineOptions create(){
            return new PolylineOptions().color(COLOR_DEFAULT).width(WIDTH_DEFAULT);
        }
    }

    public static  class CustomChronometer extends Chronometer{

        private static final String TAG = CustomChronometer.class.getName();
        private boolean isRunning = false;
        private long pauseOffSet = 0;

        public CustomChronometer(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomChronometer(Context context) {
            super(context);
        }

        public void setTimeInMillSec(long base, long pauseOffSet){
            super.setBase(base);
            this.pauseOffSet = pauseOffSet;
        }

        public void start(){
           if(!isRunning){
               super.setBase(SystemClock.elapsedRealtime());
               super.start();
               isRunning = true;
           }
        }

        public void pause(){
           if(isRunning){
               this.pauseOffSet = SystemClock.elapsedRealtime() - super.getBase();
               super.stop();
               isRunning = false;
           }
           super.setBase(SystemClock.elapsedRealtime() - pauseOffSet);
        }

        public void restart(){
           if(!isRunning){
               super.setBase(SystemClock.elapsedRealtime() - pauseOffSet);
               super.start();
               isRunning = true;
           }
        }

        public void stop(){
           if(isRunning){
               pauseOffSet = 0;
               super.stop();
               isRunning = false;
           }
        }

    }
}
