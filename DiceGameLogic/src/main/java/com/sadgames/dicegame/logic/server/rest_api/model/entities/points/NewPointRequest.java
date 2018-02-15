package com.sadgames.dicegame.logic.server.rest_api.model.entities.points;

import android.os.Parcel;
import android.os.Parcelable;

public class NewPointRequest implements Parcelable{

    private String gameId;
    private int xPos;
    private int yPos;
    private PointType type;
    private int nextIndex;
    private int flyIndex;

    public NewPointRequest() {}

    public NewPointRequest(AbstractGamePoint other) {
        this.xPos = other.xPos;
        this.yPos = other.yPos;
        this.type = other.type;
        this.nextIndex = other.nextPointIndex;
    }

    protected NewPointRequest(Parcel in) {
        gameId = in.readString();
        xPos = in.readInt();
        yPos = in.readInt();
        type = PointType.values()[in.readInt()];
        nextIndex = in.readInt();
        flyIndex = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gameId);
        dest.writeInt(xPos);
        dest.writeInt(yPos);
        dest.writeInt(type.ordinal());
        dest.writeInt(nextIndex);
        dest.writeInt(flyIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<NewPointRequest> CREATOR = new Creator<NewPointRequest>() {
        @Override
        public NewPointRequest createFromParcel(Parcel in) {
            return new NewPointRequest(in);
        }

        @Override
        public NewPointRequest[] newArray(int size) {
            return new NewPointRequest[size];
        }
    };

    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getxPos() {
        return xPos;
    }
    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }
    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public PointType getType() {
        return type;
    }
    public void setType(PointType type) {
        this.type = type;
    }

    public int getNextIndex() {
        return nextIndex;
    }
    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    public int getFlyIndex() {
        return flyIndex;
    }
    public void setFlyIndex(int flyIndex) {
        this.flyIndex = flyIndex;
    }

}
