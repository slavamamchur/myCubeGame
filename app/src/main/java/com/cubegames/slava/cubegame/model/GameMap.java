package com.cubegames.slava.cubegame.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.cubegames.slava.cubegame.api.AbstractHttpRequest;
import com.cubegames.slava.cubegame.api.GameMapController;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.cubegames.slava.cubegame.api.RestConst.URL_GAME_MAP;

public class GameMap extends BasicNamedDbEntity implements Parcelable{
    @JsonProperty(required = false)
    public long createdDate;
    public long updatedDate;
    @JsonProperty(required = false)
    private byte[] binaryData;

    public GameMap() {}

    public GameMap(String id) {
        this.id = id;
        this.createdDate = System.currentTimeMillis();
    }

    protected GameMap(Parcel in) {
        loadFromParcel(in);
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
    public byte[] getBinaryData() {
        return binaryData;
    }
    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }
    public long getUpdatedDate() {
        return updatedDate;
    }
    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
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
        dest.writeLong(updatedDate);
        dest.writeByteArray(binaryData);
        //sourceBitmap.writeToParcel(dest, 0);
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        super.loadFromParcel(in);

        createdDate = in.readLong();
        updatedDate = in.readLong();

        try {
            binaryData = in.createByteArray();
            //destinationBitmap = Bitmap.CREATOR.createFromParcel(in);
        }
        catch(Exception e){
            binaryData = null;
        }
    }

    public static String ACTION_NAME =  URL_GAME_MAP;

    @Override
    public AbstractHttpRequest getController(Context ctx) {
        return new GameMapController(ctx);
    }
}
