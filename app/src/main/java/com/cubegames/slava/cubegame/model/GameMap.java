package com.cubegames.slava.cubegame.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameMap extends BasicNamedDbEntity implements Parcelable{
    @JsonProperty(required = false)
    private long createdDate;
    //@JsonProperty(required = false)
    //private byte[] binaryData;
    @JsonProperty(required = false)
    private Bitmap bitmap;

    public GameMap() {}

    protected GameMap(Parcel in) {
        loadFromParcel(in);

        createdDate = in.readLong();

        byte[] binaryData = in.createByteArray();
        bitmap = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
    }

    @Override
    protected void finalize() throws Throwable {
        if (bitmap != null) {
            bitmap.recycle();
        }

        super.finalize();
    }

    public static final Creator<GameMap> CREATOR = new Creator<GameMap>() {
        @Override
        public GameMap createFromParcel(Parcel in) {
            return new GameMap(in);
        }

        @Override
        public GameMap[] newArray(int size) {
            return new GameMap[size];
        }
    };

    public long getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /*public byte[] getBinaryData() {
        return binaryData;
    }
    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }*/


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        save2Parcel(dest);

        dest.writeLong(createdDate);
        //dest.writeByteArray(binaryData);
        if (bitmap != null)
            bitmap.writeToParcel(dest, flags);
    }
}
