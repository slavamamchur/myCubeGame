package com.cubegames.slava.cubegame.model;

import android.os.Parcel;

public class BasicEntity extends AbstractResponse {

    public BasicEntity() {}
    public BasicEntity(String id, String error) {
        super(id, error);
    }

    protected void save2Parcel(Parcel dest) {
        dest.writeString(getId());
        dest.writeString(getError());
    }

    protected void loadFromParcel(Parcel in) {
        setId(in.readString());
        setError(in.readString());
    }
}
