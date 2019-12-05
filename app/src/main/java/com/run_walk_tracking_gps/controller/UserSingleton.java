package com.run_walk_tracking_gps.controller;

import android.content.Context;

import com.run_walk_tracking_gps.model.User;
import com.run_walk_tracking_gps.model.builder.UserBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSingleton {

    private static UserSingleton instance;

    private User user;

    private UserSingleton() {
    }

    public static UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    public User getUser(){
        return user;
    }

    public void setUser(final Context context, final JSONObject user){
        try {
            setUser(UserBuilder.create(context, user).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setUser(final User user){
        this.user = user;
    }
}
