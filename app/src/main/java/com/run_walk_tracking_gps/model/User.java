package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.connectionserver.HttpRequest;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.DateHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
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
    private Measure height;

    public User(){
    }

    private User(User user){
        this.id_user = user.id_user;
        this.name = user.name;
        this.last_name = user.last_name;
        this.gender = user.gender;
        this.birth_date = user.birth_date;
        this.email = user.email;
        this.city = user.city;
        this.phone = user.phone;
        this.height = user.height.clone();
        this.username = user.username;
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject().put(HttpRequest.Constant.NAME, this.name)
                               .put(HttpRequest.Constant.LAST_NAME, this.last_name)
                               .put(HttpRequest.Constant.BIRTH_DATE, this.birth_date)
                               .put(HttpRequest.Constant.EMAIL, this.email)
                               .put(HttpRequest.Constant.CITY, this.city)
                               .put(HttpRequest.Constant.PHONE, this.phone);
    }

    public User clone(){
        return new User(this);
    }

    public User(Context context, JSONObject userJson) throws JSONException {
        try {
            id_user = userJson.getInt(HttpRequest.Constant.ID_USER);
            username = userJson.getString(HttpRequest.Constant.USERNAME);
            name = userJson.getString(HttpRequest.Constant.NAME);
            last_name = userJson.getString(HttpRequest.Constant.LAST_NAME);
            gender = Gender.valueOf(userJson.getString(HttpRequest.Constant.GENDER));
            birth_date = DateHelper.create(context).parseShortToDate(userJson.getString(HttpRequest.Constant.BIRTH_DATE));
            email = userJson.getString(HttpRequest.Constant.EMAIL);
            city = userJson.getString(HttpRequest.Constant.CITY);
            phone = userJson.getString(HttpRequest.Constant.PHONE);
            height = Measure.create(context, Measure.Type.HEIGHT, userJson.getDouble(HttpRequest.Constant.HEIGHT));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setContext(Context context){
        this.height.setContext(context);
    }


// START - Parcelable IMPLEMENTATION
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
        height = in.readParcelable(Measure.class.getClassLoader());
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
        dest.writeParcelable(height, 0);
    }

// END - Parcelable IMPLEMENTATION


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
        return DateHelper.create(height.getContext()).formatShortToString(birth_date);
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

    public Measure getHeight() {
        return height;
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

    public void setUsername(String username) {
        this.username = username;
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

}
