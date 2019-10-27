package com.run_walk_tracking_gps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import com.run_walk_tracking_gps.utilities.BitmapUtilities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BitmapTest {

    private static final String TAG = BitmapTest.class.getName();

    private Context context= InstrumentationRegistry.getTargetContext();
    private  Bitmap bitmap;
    @Before
    public void init(){
        Drawable image = context.getDrawable(R.drawable.boot_app_background);
        bitmap = ((BitmapDrawable)image).getBitmap();
        Log.d(TAG, "Bitmap : " + bitmap.getByteCount());
    }

    @Test
    public void test(){
        String stringBitMap = BitmapUtilities.BitMapToString(bitmap);
        Log.e(TAG, "Lunghezza compressa: " + stringBitMap.getBytes().length);
        Bitmap bitmap1 = BitmapUtilities.StringToBitMap(stringBitMap);
        Log.e(TAG, "Lunghezza decompressa : " + bitmap1.getByteCount());
        Assert.assertTrue(stringBitMap.getBytes().length < bitmap1.getByteCount());
    }
}
