package com.cubegames.slava.cubegame.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginResponse extends BasicEntity implements Parcelable{

    @SuppressWarnings("unused")
    public LoginResponse() {}
    public LoginResponse(String id, String error) {
        super(id, error);
    }
    public LoginResponse(Parcel in)
    {
        loadFromParcel(in);
    }
    public static final Parcelable.Creator<LoginResponse> CREATOR = new Parcelable.Creator<LoginResponse>()
    {
        public LoginResponse createFromParcel(Parcel s)
        {
            return new LoginResponse(s);
        }
        public LoginResponse[] newArray(int size)
        {
            return new LoginResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        save2Parcel(dest);
    }
}
