package com.cubegames.slava.cubegame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthToken extends BasicEntity implements Parcelable{

    private String id;

    @SuppressWarnings("unused")
    public AuthToken() {}
    public AuthToken(Parcel in)
    {
        loadFromParcel(in);
    }
    public static final Parcelable.Creator<AuthToken> CREATOR = new Parcelable.Creator<AuthToken>()
    {
        public AuthToken createFromParcel(Parcel s)
        {
            return new AuthToken(s);
        }
        public AuthToken[] newArray(int size)
        {
            return new AuthToken[size];
        }
    };

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
        dest.writeString(id);
    }
    @Override
    protected void loadFromParcel(Parcel in) {
        id = in.readString();
    }
}
