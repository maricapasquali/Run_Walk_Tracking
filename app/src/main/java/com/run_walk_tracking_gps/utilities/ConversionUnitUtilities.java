package com.run_walk_tracking_gps.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversionUnitUtilities {

    private static final double KM_TO_MI = 1.609;
    private static final double KG_TO_LB = 2.205;
    private static final double M_TO_FT = 3.28;

    public static double kilometerToMile(double km){
        return round2(km / KM_TO_MI);
    }

    public static double kilogramToPound(double kg){
        return round2(kg * KG_TO_LB);
    }

    public static double meterToFeet(double m){
        return round2(m * M_TO_FT);
    }

    public static double feetToMeter(double ft){
        return round2(ft / M_TO_FT);
    }

    public static double kilometerForHoursToMileForHours(double km_h){
        return kilometerToMile(km_h);
    }

    private static Double round2(final double val) {
        return new BigDecimal(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
