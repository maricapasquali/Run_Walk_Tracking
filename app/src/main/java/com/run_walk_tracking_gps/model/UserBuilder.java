package com.run_walk_tracking_gps.model;

import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.DateUtilities;
import java.text.ParseException;

public class UserBuilder {

    private User user;

    private UserBuilder(){
        user = new User();
    }

    public static UserBuilder create(){
        return new UserBuilder();
    }

    public User build(){
        return user;
    }

    public UserBuilder setName(String name){
        user.setName(name);
        return this;
    }

    public UserBuilder setLastName(String lastname){
        user.setLastName(lastname);
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

    public UserBuilder setBirthDate(String birthDate) throws ParseException {
        user.setBirthDate(DateUtilities.parseShortToDate(birthDate));
        return this;
    }

    public UserBuilder setGender(Gender gender){
        user.setGender(gender);
        return this;
    }
}
