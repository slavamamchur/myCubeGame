package com.sadgames.dicegame.logic.server.rest_api.model.entities;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sadgames.dicegame.logic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.GameMapController;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_GAME_MAP;

public class GameMapEntity extends BasicNamedDbEntity {
    @JsonProperty(required = false)
    public long createdDate;
    public long lastUsedDate;
    @JsonProperty(required = false)
    private byte[] binaryData;
    @JsonProperty(required = false)
    private byte[] binaryDataRelief;

    public GameMapEntity() {}

    @SuppressWarnings("unused") public GameMapEntity(String id) {
        this.id = id;
        this.createdDate = System.currentTimeMillis();
    }

    protected GameMapEntity(Parcel in) {
        loadFromParcel(in);
    }

    public static final Creator<GameMapEntity> CREATOR = new Creator<GameMapEntity>() {
        @Override
        public GameMapEntity createFromParcel(Parcel in) {
            return new GameMapEntity(in);
        }

        @Override
        public GameMapEntity[] newArray(int size) {
            return new GameMapEntity[size];
        }
    };

    public long getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }
    public byte[] getBinaryData() {
        return binaryData;
    }
    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }
    public byte[] getBinaryDataRelief() {
        return binaryDataRelief;
    }
    public void setBinaryDataRelief(byte[] binaryDataRelief) {
        this.binaryDataRelief = binaryDataRelief;
    }
    public long getLastUsedDate() {
        return lastUsedDate;
    }
    public void setLastUsedDate(long lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        save2Parcel(dest);
    }

    @Override
    protected void save2Parcel(Parcel dest) {
        super.save2Parcel(dest);

        dest.writeLong(createdDate);
        dest.writeLong(lastUsedDate);
        dest.writeByteArray(binaryData);
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        super.loadFromParcel(in);

        createdDate = in.readLong();
        lastUsedDate = in.readLong();

        try {
            binaryData = in.createByteArray();
        }
        catch(Exception e){
            binaryData = null;
        }
    }

    public static String ACTION_NAME =  URL_GAME_MAP;

    @Override
    public AbstractHttpRequest getController(SysUtilsWrapperInterface sysUtilsWrapper) {
        return new GameMapController(sysUtilsWrapper);
    }
}
