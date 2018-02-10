package com.sadgames.dicegame.rest_api.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserEntity extends BasicEntity implements Parcelable{

    private String userName;
    private String userPass;
    private String email;
    private String language;

    @SuppressWarnings("unused")
    public UserEntity() {}

    protected UserEntity(Parcel in) {
        loadFromParcel(in);
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        save2Parcel(dest);
    }

    @Override
    protected void save2Parcel(Parcel dest) {
        dest.writeString(userName);
        dest.writeString(userPass);
        dest.writeString(email);
        dest.writeString(language);
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        userName = in.readString();
        userPass = in.readString();
        email = in.readString();
        language = in.readString();
    }
}
