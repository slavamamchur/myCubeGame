package com.cubegames.slava.cubegame.model;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicNamedDbEntity extends BasicDbEntity {
    @JsonProperty(required = true)
    protected String name;

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
