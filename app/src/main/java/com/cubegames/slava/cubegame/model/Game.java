package com.cubegames.slava.cubegame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;

import java.util.ArrayList;
import java.util.List;

import static com.cubegames.slava.cubegame.api.RestConst.URL_GAME;

public class Game extends BasicNamedDbEntity implements Parcelable{

    public static String ACTION_NAME =  URL_GAME;

    private List<AbstractGamePoint> gamePoints;
    private String mapId;
    private long createdDate;

    public Game() {}

    protected Game(Parcel in) {
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
    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public List<AbstractGamePoint> getGamePoints() {
        return gamePoints;
    }
    public void setGamePoints(List<AbstractGamePoint> gamePoints) {
        this.gamePoints = gamePoints;
    }

    public String getMapId() {
        return mapId;
    }
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public void addPoint(AbstractGamePoint point) {
        if(point == null) return;

        if (gamePoints == null) {
            gamePoints = new ArrayList<>();
        }

        gamePoints.add(point);
    }

    public long getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    protected void save2Parcel(Parcel dest) {
        super.save2Parcel(dest);

        dest.writeTypedList(gamePoints);
        dest.writeString(mapId);
        dest.writeLong(createdDate);
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        super.loadFromParcel(in);

        gamePoints = in.createTypedArrayList(AbstractGamePoint.CREATOR);
        mapId = in.readString();
        createdDate = in.readLong();
    }

}
