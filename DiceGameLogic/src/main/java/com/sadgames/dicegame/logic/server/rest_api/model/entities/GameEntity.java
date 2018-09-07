package com.sadgames.dicegame.logic.server.rest_api.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.dicegame.logic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.GameController;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_GAME;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameEntity extends BasicNamedDbEntity implements Parcelable{

    public static String ACTION_NAME =  URL_GAME;

    protected List<AbstractGamePoint> gamePoints;
    public String mapId;
    public long createdDate;

    public GameEntity() {}

    protected GameEntity(Parcel in) {
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
    public static final Creator<GameEntity> CREATOR = new Creator<GameEntity>() {
        @Override
        public GameEntity createFromParcel(Parcel in) {
            return new GameEntity(in);
        }

        @Override
        public GameEntity[] newArray(int size) {
            return new GameEntity[size];
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

    @Override
    public AbstractHttpRequest getController(SysUtilsWrapperInterface sysUtilsWrapper) {
        return new GameController(sysUtilsWrapper);
    }

    /** for game init script from web api object -------------------------------------------------*/
    public Vector3f _getGravity() {
        return new Vector3f(0f, -9.8f, 0f);
    }

    public Vector3f _getStartCameraPosition() {
        return new Vector3f(0f, 3f, 3f);
    }
    public float    _getStartCameraPitch() {
        return 45f;
    }
    public float    _getStartCameraYaw() {
        return 0f;
    }
    public float    _getStartCameraRoll() {
        return 0f;
    }
    public float    _getStartCameraVFOV() {
        return 35.0f;
    }

    public Vector3f _getStartSunPosition() {
        return new Vector3f(-2.2f, 1.7f, -3.2f);
    }
    public Vector3f _getStartSunColor() {
        return new Vector3f(1.0f, 1.0f, 0.8f);
    }

    public void loadSceneObjects(SysUtilsWrapperInterface sysUtilsWrapper, GLScene glScene, DynamicsWorld dynamicsWorldObject) {}
}
