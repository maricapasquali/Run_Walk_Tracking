package com.run_walk_tracking_gps.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtilities {
    public static Double round2(final double val) {
        return round_n(2, val);
    }

    public static Double round1(final double val) {
        return round_n(1, val);
    }

    private static Double round_n(final int scale, final double val) {
        return new BigDecimal(val).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}
