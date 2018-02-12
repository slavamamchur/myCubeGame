package com.sadgames.dicegame.rest_api.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthTokenEntity extends BasicEntity implements Parcelable{

    private String id;

    @SuppressWarnings("unused")
    public AuthTokenEntity() {}

    public AuthTokenEntity(String id) {
        this.id = id;
    }

    public AuthTokenEntity(Parcel in)
    {
        loadFromParcel(in);
    }
    public static final Parcelable.Creator<AuthTokenEntity> CREATOR = new Parcelable.Creator<AuthTokenEntity>()
    {
        public AuthTokenEntity createFromParcel(Parcel s)
        {
            return new AuthTokenEntity(s);
        }
        public AuthTokenEntity[] newArray(int size)
        {
            return new AuthTokenEntity[size];
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
