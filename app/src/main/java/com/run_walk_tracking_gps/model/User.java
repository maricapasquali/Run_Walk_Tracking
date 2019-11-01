package com.run_walk_tracking_gps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import java.text.DateFormat;
import java.util.Date;

public class User implements Parcelable {

    private int id_user;
    private String username;
    private String name;
    private String last_name;
    private Gender gender;
    private Date birth_date;
    private String email;
    private String city;
    private String phone;
    private double height;

    public User(){}

    protected User(Parcel in) {
        id_user = in.readInt();
        username =  in.readString();
        name = in.readString();
        last_name = in.readString();
        gender = Gender.valueOf(in.readString());
        birth_date = new Date(in.readLong());
        email = in.readString();
        city = in.readString();
        phone = in.readString();
        height = in.readDouble();
    }

    // TODO: 11/1/2019 CREATE UN COMPARATOR 
    
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getIdUser() {
        return id_user;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return last_name;
    }

    public Gender getGender() {
        return gender;
    }

    public Date getBirthDate() {
        return birth_date;
    }

    public String getBirthDateString() {
        return DateUtilities.parseToString(DateFormat.SHORT, birth_date);
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public double getHeight() {
        return height;
    }

    public void setIdUser(int id_user) {
        this.id_user = id_user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setBirthDate(Date birth_date) {
        this.birth_date = birth_date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setHeight(double height) {
        this.height = height;
    }




    @Override
    public String toString() {

        return "User{ id_user = " + id_user + '\'' +
                ", name='" + name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", gender=" + gender +
                ", birth_date='" + birth_date + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", phone= '" + phone + '\'' +
                ", height='" + height + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_user);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(last_name);
        dest.writeString(gender.toString());
        dest.writeLong(birth_date.getTime());
        dest.writeString(email);
        dest.writeString(city);
        dest.writeString(phone);
        dest.writeDouble(height);
    }
}
