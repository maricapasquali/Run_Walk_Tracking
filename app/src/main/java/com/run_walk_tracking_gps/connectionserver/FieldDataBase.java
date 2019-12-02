package com.run_walk_tracking_gps.connectionserver;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FieldDataBase {

    FIRST_LOGIN,
    TOKEN,
    ID_USER,
    NAME,
    LAST_NAME,
    BIRTH_DATE,
    EMAIL,
    CITY,
    PHONE,
    GENDER ,
    HEIGHT,
    IMG_ENCODE,
    USERNAME,
    PASSWORD,
    SPORT,
    TARGET,
    WEIGHT,
    ID_WEIGHT,
    ID_WORKOUT ,
    DURATION ,
    ID_SPORT ,
    MAP_ROUTE ,
    DISTANCE ,
    CALORIES,
    DATE ,
    VALUE ,
    FILTER,
    ENERGY,
    ID_TARGET,
    LANGUAGE;

    public String toName() {
        return this.toString().toLowerCase();
    }

    private static List<String> unmodifiableList(Stream<FieldDataBase> stream){
        return stream.map(FieldDataBase::toName).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    //ACCOUNT
    public static List<String> fieldRequiredForSignUp(){
        return unmodifiableList(Stream.of(NAME, LAST_NAME, GENDER, BIRTH_DATE, EMAIL,
                 CITY,PHONE,HEIGHT, LANGUAGE, TARGET, WEIGHT,
                 USERNAME, PASSWORD));
    }

    public static List< String> fieldRequiredForSignIn(){
        return unmodifiableList(Stream.of(USERNAME, PASSWORD));
    }

    public static List< String> fieldRequiredForFirstSignIn(){
        return unmodifiableList(Stream.of(ID_USER, TOKEN));
    }

    public static List< String> fieldRequiredForUserInformation(){
        return unmodifiableList(Stream.of(ID_USER));
    }

    public static List<String> fieldRequiredForImgProfile() {
        return unmodifiableList(Stream.of(ID_USER));
    }

    public static List< String> fieldRequiredForUpdatePassword(){
        return unmodifiableList(Stream.of(ID_USER,PASSWORD));
    }

    public static List< String> fieldRequiredForForgotPassword(){
        return unmodifiableList(Stream.of(EMAIL));
    }

    public static List< String> fieldSupportedForUpdateUserInformation(){
        return unmodifiableList(Stream.of(ID_USER,  IMG_ENCODE,  NAME,  LAST_NAME,  GENDER,
                 BIRTH_DATE,  EMAIL,  CITY,  PHONE, HEIGHT));
    }

    public static List< String> fieldRequiredForDeleteUser(){
        return unmodifiableList(Stream.of(ID_USER));
    }

    // SETTINGS
    public static  List< String> fieldRequiredForUpdateSetting(){
        return unmodifiableList(Stream.of(ID_USER,  FILTER,  VALUE));
    }

    // WORKOUTS
    public static List< String> fieldRequiredForAllWorkouts(){
        return unmodifiableList(Stream.of(ID_USER));
    }

    public static List< String> fieldSupportedForNewWorkout(){
        return unmodifiableList(Stream.of(ID_USER, DATE, SPORT, DURATION, MAP_ROUTE, DISTANCE, CALORIES));
    }

    public static List< String> fieldSupportedForUpdateWorkout(){
        return unmodifiableList(Stream.of(ID_WORKOUT,  DATE,  SPORT,  DURATION,  MAP_ROUTE,
                 DISTANCE,  CALORIES));
    }

    public static List< String> fieldRequiredForDeleteWorkout(){
        return unmodifiableList(Stream.of(ID_WORKOUT));
    }

    // STATISTICS
    public static  List< String> fieldRequiredForAllStatistics(){
        return unmodifiableList(Stream.of(ID_USER,  FILTER));
    }

    public static  List< String> fieldRequiredForNewWeight(){
        return unmodifiableList(Stream.of(ID_USER,  DATE,  VALUE));
    }

    public static List< String> fieldSupportedForUpdateWeight(){
        return unmodifiableList(Stream.of(ID_WEIGHT,  DATE,  VALUE));
    }

    public static List< String> fieldRequiredForDeleteWeight() {
        return unmodifiableList(Stream.of(ID_WEIGHT));
    }
}
