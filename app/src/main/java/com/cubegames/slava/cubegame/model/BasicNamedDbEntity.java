package com.cubegames.slava.cubegame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicNamedDbEntity extends BasicDbEntity implements Parcelable{
    @JsonProperty(required = true)
    protected String name;

    public BasicNamedDbEntity(){}
    protected BasicNamedDbEntity(Parcel in) {
        loadFromParcel(in);
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        save2Parcel(dest);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<BasicNamedDbEntity> CREATOR = new Creator<BasicNamedDbEntity>() {
        @Override
        public BasicNamedDbEntity createFromParcel(Parcel in) {
            return new BasicNamedDbEntity(in);
        }

        @Override
        public BasicNamedDbEntity[] newArray(int size) {
            return new BasicNamedDbEntity[size];
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
        super.save2Parcel(dest);

        dest.writeString(name);
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        super.loadFromParcel(in);

        setName(in.readString());
    }

    public static String ACTION_NAME = "";
    public String getActionName(){
        return ACTION_NAME;
    }
}
