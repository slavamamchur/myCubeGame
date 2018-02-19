package com.sadgames.dicegame.logic.server.rest_api.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.sadgames.dicegame.logic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.DBPlayerController;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_PLAYER;

public class DbPlayerEntity extends BasicNamedDbEntity implements Parcelable{
    public int color;
    public static String ACTION_NAME =  URL_PLAYER;

    public DbPlayerEntity() {}
    protected DbPlayerEntity(Parcel in) {
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

    public static final Creator<DbPlayerEntity> CREATOR = new Creator<DbPlayerEntity>() {
        @Override
        public DbPlayerEntity createFromParcel(Parcel in) {
            return new DbPlayerEntity(in);
        }

        @Override
        public DbPlayerEntity[] newArray(int size) {
            return new DbPlayerEntity[size];
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
    public AbstractHttpRequest getController(SysUtilsWrapperInterface sysUtilsWrapper) {
        return new DBPlayerController(sysUtilsWrapper);
    }
}
