package com.run_walk_tracking_gps.model.enumerations;

import com.run_walk_tracking_gps.R;

public enum Language {

    ITALIAN(R.string.it_code),
    ENGLISH(R.string.en_code);

    private int code;

    Language(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
