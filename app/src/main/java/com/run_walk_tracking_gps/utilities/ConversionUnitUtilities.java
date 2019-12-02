package com.run_walk_tracking_gps.utilities;

public class ConversionUnitUtilities {

    private static final double KM_TO_MI = 1.609;
    private static final double KG_TO_LB = 2.205;
    private static final double M_TO_FT = 3.28;

    public static double kilometerToMile(double km){
        return NumberUtilities.round2(km / KM_TO_MI);
    }

    public static double mileToKilometer(double mi){
        return NumberUtilities.round2(mi * KM_TO_MI);
    }

    public static double kilogramToPound(double kg){
        return NumberUtilities.round1(kg * KG_TO_LB);
    }

    public static Double poundToKilogram(double lb) {
        return NumberUtilities.round1(lb / KG_TO_LB);
    }

    public static double meterToFeet(double m){
        return NumberUtilities.round2(m * M_TO_FT);
    }

    public static double feetToMeter(double ft){
        return NumberUtilities.round2(ft / M_TO_FT);
    }

    public static double kilometerForHoursToMileForHours(double km_h){
        return kilometerToMile(km_h);
    }


    public static Double mileForHoursToKilometerForHours(double mi_h) {
        return mileToKilometer(mi_h);
    }
}
