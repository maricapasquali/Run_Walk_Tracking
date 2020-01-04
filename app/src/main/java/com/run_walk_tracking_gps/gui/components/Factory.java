package com.run_walk_tracking_gps.gui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.google.android.gms.maps.model.PolylineOptions;
import com.run_walk_tracking_gps.R;


public class Factory {

    public static class CustomImageView extends android.support.v7.widget.AppCompatImageView {

        public static final float RADIUS = 500.0f;

        public CustomImageView(Context context) {
            super(context);
            setBackgroundDrawable(context.getDrawable(R.drawable.ic_user_empty));
        }

        public CustomImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final Path clipPath = new Path();
            final RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
            clipPath.addRoundRect(rect, RADIUS, RADIUS, Path.Direction.CW);
            canvas.clipPath(clipPath);
            super.onDraw(canvas);
        }
    }

    public static class CustomPolylineOptions{
        private static int WIDTH_DEFAULT = 15;
        private static int COLOR_DEFAULT = Color.BLUE;

        public static PolylineOptions create(){
            return new PolylineOptions().color(COLOR_DEFAULT).width(WIDTH_DEFAULT);
        }
    }
}
