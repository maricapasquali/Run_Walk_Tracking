package com.run_walk_tracking_gps;

import com.run_walk_tracking_gps.utilities.ConversionUnitUtilities;

import org.junit.Assert;
import org.junit.Test;

public class ConversionUnitTest {

    @Test
    public void text(){
        double mile = ConversionUnitUtilities.kilometerToMile(20);
        System.out.println("Mile = " + mile);
        Assert.assertTrue(12.43==mile);

        double pound = ConversionUnitUtilities.kilogramToPound(20);
        System.out.println("Pound = " + pound);
        Assert.assertTrue(44.10==pound);

        double feet = ConversionUnitUtilities.meterToFeet(1.75);
        System.out.println("Feet = " + feet);
        Assert.assertTrue(5.74==feet);
    }
}
