package com.run_walk_tracking_gps.utilities;

public class ServiceUtilities {

    private static final int ONE_SECOND = 1;

    public static void waitSecond(int n){
        try {
            Thread.sleep(n * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitOneSecond() {
        waitSecond(ONE_SECOND);
    }
}
