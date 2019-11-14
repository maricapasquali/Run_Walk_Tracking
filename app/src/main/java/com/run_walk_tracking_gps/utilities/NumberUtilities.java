package com.run_walk_tracking_gps.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtilities {
    public static Double round2(final double val) {
        return new BigDecimal(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
