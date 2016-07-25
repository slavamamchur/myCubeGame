package com.cubegames.slava.cubegame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorEntity extends BasicEntity implements Parcelable {

    @JsonProperty(required = true)
    private String error;

    public ErrorEntity() {}
    public ErrorEntity(String error) {
        this.error = error;
    }
    protected ErrorEntity(Parcel in) {
        loadFromParcel(in);
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
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

    @Override
    protected void save2Parcel(Parcel dest) {
        dest.writeString(error);
    }
    @Override
    protected void loadFromParcel(Parcel in) {
        error = in.readString();
    }
}
