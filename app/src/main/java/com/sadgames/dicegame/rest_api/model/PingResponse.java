package com.sadgames.dicegame.rest_api.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PingResponse extends BasicEntity implements Parcelable {

    private String name;

    public PingResponse() {}
    protected PingResponse(Parcel in) {
        loadFromParcel(in);
    }
    public static final Creator<PingResponse> CREATOR = new Creator<PingResponse>() {
        @Override
        public PingResponse createFromParcel(Parcel in) {
            return new PingResponse(in);
        }

        @Override
        public PingResponse[] newArray(int size) {
            return new PingResponse[size];
        }
    };

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void save2Parcel(Parcel dest) {
        dest.writeString(name);
    }
    @Override
    protected void loadFromParcel(Parcel in) {
        name = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        save2Parcel(dest);
    }
}
