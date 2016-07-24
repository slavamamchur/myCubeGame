package com.cubegames.slava.cubegame.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RegisterRequestParams implements Parcelable{

    private String userName;
    private String userPass;
    private String email;
    private String language;

    @SuppressWarnings("unused")
    public RegisterRequestParams() {}

    protected RegisterRequestParams(Parcel in) {
        userName = in.readString();
        userPass = in.readString();
        email = in.readString();
        language = in.readString();
    }

    public static final Creator<RegisterRequestParams> CREATOR = new Creator<RegisterRequestParams>() {
        @Override
        public RegisterRequestParams createFromParcel(Parcel in) {
            return new RegisterRequestParams(in);
        }

        @Override
        public RegisterRequestParams[] newArray(int size) {
            return new RegisterRequestParams[size];
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
        dest.writeString(userName);
        dest.writeString(userPass);
        dest.writeString(email);
        dest.writeString(language);
    }
}
