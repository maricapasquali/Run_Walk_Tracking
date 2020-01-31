package com.run_walk_tracking_gps.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.run_walk_tracking_gps.R;

public class ColorUtilities {

    public static Drawable darkIcon(Context context, int icon){
        return darkIcon(context.getDrawable(icon));
    }

    public static Drawable darkIcon(Drawable drawable){
        drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        return drawable;
    }

    public static Drawable lightIcon(Context context, int icon){
        return lightIcon(context, context.getDrawable(icon));
    }

    public static Drawable lightIcon(Context context, Drawable drawable){
        drawable.setColorFilter(context.getColor(R.color.divColor), PorterDuff.Mode.MULTIPLY);
        return drawable;
    }


    public static Drawable colorIcon(Context context, int icon, int color){
        return colorIcon(context.getDrawable(icon), color);
    }

    public static Drawable colorIcon(Drawable drawable, int color){
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        return drawable;
    }

}
