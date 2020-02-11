package com.run_walk_tracking_gps.utilities;

public class StringUtilities {

    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }
}
