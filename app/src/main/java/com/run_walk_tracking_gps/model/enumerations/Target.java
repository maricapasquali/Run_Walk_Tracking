package com.run_walk_tracking_gps.model.enumerations;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.run_walk_tracking_gps.R;

import java.util.Arrays;


public enum Target {

    MARATHON(R.string.marathon),
    LOSE_WEIGHT(R.string.lose_weight);

    private final int strId;

    Target(int strId) {
        this.strId = strId;
    }

    public int getStrId() {
        return this.strId;
    }

    public int getIconId() {
        return  R.drawable.ic_target;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Target getTargetFromStrId(int strId){
        return Arrays.asList(Target.values()).stream().filter( t -> t.getStrId()==strId).findFirst().orElse(null);
    }

}
