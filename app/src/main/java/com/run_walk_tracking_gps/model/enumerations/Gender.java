package com.run_walk_tracking_gps.model.enumerations;

import com.run_walk_tracking_gps.R;

public enum Gender {

    FEMALE(R.string.female, R.drawable.ic_female),
    MALE(R.string.male, R.drawable.ic_male),
    OTHER(R.string.other, R.drawable.ic_other);

    private final int strId;
    private final int iconId;

    Gender(int strId, int iconId) {
        this.strId = strId;
        this.iconId = iconId;
    }

    public int getStrId() {
        return this.strId;
    }

    public int getIconId() {
        return this.iconId;
    }

}
