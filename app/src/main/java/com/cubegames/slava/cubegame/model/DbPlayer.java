package com.cubegames.slava.cubegame.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DbPlayer extends BasicNamedDbEntity implements Parcelable{
    private int color;

    public DbPlayer() {}
    protected DbPlayer(Parcel in) {
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

    public static final Creator<DbPlayer> CREATOR = new Creator<DbPlayer>() {
        @Override
        public DbPlayer createFromParcel(Parcel in) {
            return new DbPlayer(in);
        }

        @Override
        public DbPlayer[] newArray(int size) {
            return new DbPlayer[size];
        }
    };

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected void save2Parcel(Parcel dest) {
        super.save2Parcel(dest);

        dest.writeInt(color);
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        super.loadFromParcel(in);

        setColor(in.readInt());
    }

}
