package com.sadgames.dicegame.logic.server.rest_api.model.entities.points;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.vecmath.Vector2f;

import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_GAME_POINT;

/**
 * Abstract game point
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractGamePoint implements Parcelable{

    public int xPos;
    public int yPos;
    public PointType type;
    public int nextPointIndex;

    public AbstractGamePoint() {}

    public AbstractGamePoint(NewPointRequest other) {
        this.xPos = other.getxPos();
        this.yPos = other.getyPos();
        this.type = other.getType();
        this.nextPointIndex = other.getNextIndex();
    }

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

    public Vector2f asVector2f() {
        return new Vector2f(xPos, yPos);
    }
    public Vector2f asVector2fLua(float scaleFactor) {
        return new Vector2f(xPos * scaleFactor, yPos * scaleFactor);
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

    public static String urlForActionName(){
        return URL_GAME_POINT;
    }
}
