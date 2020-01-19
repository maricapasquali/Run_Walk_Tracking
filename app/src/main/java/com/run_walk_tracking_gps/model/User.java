package com.run_walk_tracking_gps.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;
import com.run_walk_tracking_gps.controller.Preferences;
import com.run_walk_tracking_gps.db.tables.ImageProfileDescriptor;
import com.run_walk_tracking_gps.db.tables.UserDescriptor;
import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.utilities.DateHelper;
import com.run_walk_tracking_gps.utilities.ImageFileHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

public class User implements Parcelable {


    private int id_user;

    private String nameImageProfile;

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
        this.nameImageProfile = user.nameImageProfile;
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject().put(NetworkHelper.Constant.NAME, this.name)
                               .put(NetworkHelper.Constant.LAST_NAME, this.last_name)
                               .put(NetworkHelper.Constant.BIRTH_DATE, this.birth_date)
                               .put(NetworkHelper.Constant.EMAIL, this.email)
                               .put(NetworkHelper.Constant.CITY, this.city)
                               .put(NetworkHelper.Constant.PHONE, this.phone);
    }

    public User clone(){
        return new User(this);
    }

    public User(Context context, JSONObject userJson) throws JSONException {
        try {
            id_user = Preferences.Session.getIdUser(context);
            name = userJson.getString(UserDescriptor.NAME);
            last_name = userJson.getString(UserDescriptor.LAST_NAME);
            gender = Gender.valueOf(userJson.getString(UserDescriptor.GENDER));
            birth_date = DateHelper.create(context).parseShortToDate(userJson.getString(UserDescriptor.BIRTH_DATE));
            email = userJson.getString(UserDescriptor.EMAIL);
            city = userJson.getString(UserDescriptor.CITY);
            phone = userJson.getString(UserDescriptor.PHONE);
            height = Measure.create(context, Measure.Type.HEIGHT, userJson.getDouble(UserDescriptor.HEIGHT));
            if(userJson.has(NetworkHelper.Constant.IMAGE)){
                nameImageProfile = userJson.getJSONObject(NetworkHelper.Constant.IMAGE).getString(ImageProfileDescriptor.NAME);
            }

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
        name = in.readString();
        last_name = in.readString();
        gender = Gender.valueOf(in.readString());
        birth_date = new Date(in.readLong());
        email = in.readString();
        city = in.readString();
        phone = in.readString();
        height = in.readParcelable(Measure.class.getClassLoader());
        nameImageProfile = in.readString();
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
        dest.writeString(name);
        dest.writeString(last_name);
        dest.writeString(gender.toString());
        dest.writeLong(birth_date.getTime());
        dest.writeString(email);
        dest.writeString(city);
        dest.writeString(phone);
        dest.writeParcelable(height, 0);
        dest.writeString(nameImageProfile);
    }

// END - Parcelable IMPLEMENTATION
    public Context getContext() {
    return height.getContext();
}

    public int getIdUser() {
        return id_user;
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
        return DateHelper.create(getContext()).formatShortToString(birth_date);
    }

    public String getBirthDateStrDB(){
        return DateHelper.create(getContext()).formatForDB(birth_date);
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

    public File getImage() {
        return ImageFileHelper.create(getContext()).getImage(nameImageProfile);
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

    public void setImageProfile(File image_profile) {
        this.nameImageProfile = image_profile.getName();
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
                ", image='" + nameImageProfile + '\'' +
                '}';
    }

}
