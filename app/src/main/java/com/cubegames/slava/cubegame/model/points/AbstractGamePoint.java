package com.cubegames.slava.cubegame.model.points;

import android.os.Parcel;
import android.os.Parcelable;

import static com.cubegames.slava.cubegame.api.RestConst.URL_GAME_POINT;

/**
 * Abstract game point
 */
public class AbstractGamePoint implements Parcelable{

    protected int xPos;
    protected int yPos;
    protected PointType type;
    protected int nextPointIndex;

    public AbstractGamePoint() {}

    protected AbstractGamePoint(Parcel in) {
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

    public static final Creator<AbstractGamePoint> CREATOR = new Creator<AbstractGamePoint>() {
        @Override
        public AbstractGamePoint createFromParcel(Parcel in) {
            return new AbstractGamePoint(in);
        }

        @Override
        public AbstractGamePoint[] newArray(int size) {
            return new AbstractGamePoint[size];
        }
    };

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

    public int getNextPointIndex() {
        return nextPointIndex;
    }
    public void setNextPointIndex(int nextPointIndex) {
        this.nextPointIndex = nextPointIndex;
    }

    protected void save2Parcel(Parcel dest) {
        dest.writeInt(xPos);
        dest.writeInt(yPos);
        dest.writeInt(type.ordinal());
        dest.writeInt(nextPointIndex);
    }

    protected void loadFromParcel(Parcel in) {
        xPos = in.readInt();
        yPos = in.readInt();
        type = PointType.values()[in.readInt()];
        nextPointIndex = in.readInt();
    }

    public static String getActionName(){
        return URL_GAME_POINT;
    }
}
