package com.sadgames.dicegame.rest_api.model;

import android.os.Parcel;

public class BasicResponse extends BasicEntity{
    private String error;
    private int errorCode;
    private boolean auth;

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isAuth() {
        return auth;
    }
    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    @Override
    protected void save2Parcel(Parcel dest) {
        dest.writeString(getError());
        dest.writeInt(getErrorCode());
        dest.writeByte((byte) (isAuth() ? 1 : 0));
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        setError(in.readString());
        setErrorCode(in.readInt());
        setAuth(in.readByte() != 0);
    }
}
