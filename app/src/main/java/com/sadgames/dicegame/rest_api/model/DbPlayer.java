package com.sadgames.dicegame.rest_api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.sadgames.dicegame.gl3d_engine.utils.ISysUtilsWrapper;
import com.sadgames.dicegame.rest_api.AbstractHttpRequest;
import com.sadgames.dicegame.rest_api.DBPlayerController;

import static com.sadgames.dicegame.rest_api.RestConst.URL_PLAYER;

public class DbPlayer extends BasicNamedDbEntity implements Parcelable{
    public int color;
    public static String ACTION_NAME =  URL_PLAYER;

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

    @Override
    public AbstractHttpRequest getController(ISysUtilsWrapper sysUtilsWrapper) {
        return new DBPlayerController(sysUtilsWrapper);
    }
}
