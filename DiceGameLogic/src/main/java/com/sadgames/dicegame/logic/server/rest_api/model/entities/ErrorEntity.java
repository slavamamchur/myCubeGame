package com.sadgames.dicegame.logic.server.rest_api.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.dicegame.logic.server.rest_api.model.responses.BasicResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorEntity extends BasicResponse implements Parcelable {


    public ErrorEntity() {}
    public ErrorEntity(String error, int errorCode) {
        setError(error);
        setErrorCode(errorCode);
    }
    protected ErrorEntity(Parcel in) {
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
    public static final Creator<ErrorEntity> CREATOR = new Creator<ErrorEntity>() {
        @Override
        public ErrorEntity createFromParcel(Parcel in) {
            return new ErrorEntity(in);
        }

        @Override
        public ErrorEntity[] newArray(int size) {
            return new ErrorEntity[size];
        }
    };
}
