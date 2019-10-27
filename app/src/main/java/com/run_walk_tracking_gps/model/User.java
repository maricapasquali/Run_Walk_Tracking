package com.run_walk_tracking_gps.model;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.DateUtilities;

import java.text.DateFormat;
import java.util.Date;

public class User implements Parcelable {

    private int id;
    private Uri img;
    private String name;
    private String lastName;
    private Gender gender;
    private Date birthDate;
    private String email;
    private String city;
    private String tel;
    private String height;

    public User(){}

    private User(String name, String lastName, Gender gender, Date birthDate, String email, String city, String height) {
        this.name = name;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.email = email;
        this.city = city;
        this.height = height;
    }


    protected User(Parcel in) {
        id = in.readInt();
        img = Uri.parse(in.readString());
        name = in.readString();
        gender = Gender.valueOf(in.readString());
        lastName = in.readString();
        birthDate = new Date(in.readLong());
        email = in.readString();
        city = in.readString();
        tel = in.readString();
        height = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(img.toString());
        dest.writeString(name);
        dest.writeString(gender.name());
        dest.writeString(lastName);
        dest.writeLong(birthDate.getTime());
        dest.writeString(email);
        dest.writeString(city);
        dest.writeString(tel);
        dest.writeString(height);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public static User create(String name, String lastName, Gender gender, Date birthDate, String email, String city, String height){
        return new User(name, lastName, gender, birthDate, email, city, height);
    }

    public int getId() {
        return id;
    }

    public Uri getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getBirthDateString() {
        return DateUtilities.parseToString(DateFormat.SHORT, birthDate);
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getTel() {
        return tel;
    }

    public String getHeight() {
        return height;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {

        return "User{ Id = " + id + '\'' +
                ", pathImg='" + img + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", birthDate='" + birthDate + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", tel= '" + tel + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
