package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.DateUtilities;

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
    }

    // TODO: 11/15/2019 MIGLIORARE
    public JSONObject toJson() throws JSONException {
        JSONObject userJson = new JSONObject();

        userJson.put("name", this.name)
                .put("last_name", this.last_name)
                .put("birth_date", this.birth_date)
                .put("email", this.email)
                .put("city", this.city)
                .put("phone", this.phone);

        return userJson;
    }

    public User clone(){
        return new User(this);
    }

    // TODO: 11/15/2019 MIGLIORARE
    public User(Context context, JSONObject userJson) throws JSONException {
        id_user = userJson.getInt("id_user");
        username = userJson.getString("username");
        name = userJson.getString("name");
        last_name = userJson.getString("last_name");
        gender = Gender.valueOf(userJson.getString("gender"));
        try {
            birth_date = DateUtilities.create(context).parseShortToDate(userJson.getString("birth_date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        email = userJson.getString("email");
        city = userJson.getString("city");
        phone = userJson.getString("phone");
        height = Measure.create(context, Measure.Type.HEIGHT, userJson.getDouble("height"));
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
        return DateUtilities.create(height.getContext()).parseToString(DateFormat.SHORT, birth_date);
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
