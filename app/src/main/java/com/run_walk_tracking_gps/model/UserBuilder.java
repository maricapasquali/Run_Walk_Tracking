package com.run_walk_tracking_gps.model;

import android.content.Context;

import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class UserBuilder {

    private User user;

    private UserBuilder(){
        user = new User();
    }

    private UserBuilder(Context context,JSONObject userJson) throws JSONException {
        user = new User(context, userJson);
    }

    public static UserBuilder create(){
        return new UserBuilder();
    }

    public static UserBuilder create(Context context,JSONObject userJson) throws JSONException {
        return new UserBuilder(context, userJson);
    }

    public User build(){
        return user;
    }

    public UserBuilder setName(String name){
        user.setName(name);
        return this;
    }

    public UserBuilder setLastName(String lastName){
        user.setLastName(lastName);
        return this;
    }

    public UserBuilder setEmail(String email){
        user.setEmail(email);
        return this;
    }

    public UserBuilder setPhone(String phone){
        user.setPhone(phone);
        return this;
    }

    public UserBuilder setCity(String city){
        user.setCity(city);
        return this;
    }

    public UserBuilder setBirthDate(Date birthDate)  {
        user.setBirthDate(birthDate);
        return this;
    }

    public UserBuilder setGender(Gender gender){
        user.setGender(gender);
        return this;
    }
}
