package com.run_walk_tracking_gps;

import android.content.Context;
import android.icu.util.MeasureUnit;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import android.view.MenuItem;

import com.run_walk_tracking_gps.model.Measure;

import org.junit.Assert;
import org.junit.Test;

public class MeasureClassTest {

    private Context context = InstrumentationRegistry.getTargetContext();
    @Test
    public void test(){
        Measure measure = Measure.create(context, Measure.Type.DISTANCE, 12d);

        Assert.assertEquals(12d, measure.getValue(), 0.0);
        Assert.assertEquals(Measure.Type.DISTANCE, measure.getType());

        Assert.assertNotNull(Measure.conversionTo(Measure.Unit.MILE, measure.getValue()));

        Log.e("TAG", Measure.conversionTo(Measure.Unit.MILE,measure.getValue() ).toString());
        Log.e("TAG", measure.toString());
    }
}
