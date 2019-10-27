package com.run_walk_tracking_gps.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseNameFields {

    /**
     * Table USER
     */
    public static final String KEY_ID_USER ="id_user";
    public static final String KEY_NAME ="name";
    public static final String KEY_LAST_NAME ="last_name";
    public static final String KEY_BIRTH_DATE ="birth_date";
    public static final String KEY_EMAIL ="email";
    public static final String KEY_CITY ="city";
    public static final String KEY_PHONE ="phone";
    public static final String KEY_GENDER ="gender";
    public static final String KEY_HEIGHT ="height";

    /**
     * Table PROFILE_IMAGE
     */
    public static final String KEY_IMG_ENCODE ="img_encode";

    /**
     * Table LOGIN
     */
    public static final String KEY_USERNAME ="username";
    public static final String KEY_PASSWORD ="password";

    /**
     * Table TARGET
     */
    public static final String KEY_TARGET ="target";

    /**
     * Table LANGUAGE
     */
    public static final String KEY_LANGUAGE ="language";

    /**
     * Table WEIGHT
     */
    public static final String KEY_WEIGHT ="weight";



    public static final String KEY_DATE = "date";
    public static final String KEY_VALUE_WEIGHT = "value";

    public static final String KEY_FILTER = "filter";
    public static final String VALUE_FILTER_WEIGHT = "weight";


    public static List<String> fieldRequiredForUserInformation(){
        return Arrays.asList(KEY_ID_USER);
    }

    public static List<String> fieldRequiredForSignIn(){
        final List<String> signUp = new ArrayList<>();
        signUp.add(KEY_USERNAME);
        signUp.add(KEY_PASSWORD);
        return signUp;
    }

    public static List<String> fieldRequiredForSignUp(){
        final List<String> signUp = new ArrayList<>();
        signUp.add(KEY_NAME);
        signUp.add(KEY_LAST_NAME);
        signUp.add(KEY_GENDER);
        signUp.add(KEY_BIRTH_DATE);
        signUp.add(KEY_EMAIL);
        signUp.add(KEY_CITY);
        signUp.add(KEY_PHONE);
        signUp.add(KEY_HEIGHT);
        signUp.add(KEY_LANGUAGE);
        signUp.add(KEY_TARGET);
        signUp.add(KEY_WEIGHT);
        signUp.add(KEY_USERNAME);
        signUp.add(KEY_PASSWORD);
        return signUp;
    }

}
