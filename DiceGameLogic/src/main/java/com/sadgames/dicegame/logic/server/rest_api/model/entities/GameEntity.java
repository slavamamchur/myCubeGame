package com.sadgames.dicegame.logic.server.rest_api.model.entities;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.dicegame.logic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.GameController;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.items.InteractiveGameItem;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.gl3dengine.glrender.GLRenderConsts;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.client.GameConst.DICE_MESH_OBJECT_1;
import static com.sadgames.dicegame.logic.client.GameConst.TERRAIN_MESH_OBJECT;
import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_GAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject.MOVING_OBJECT;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameEntity extends BasicNamedDbEntity {

    public static String ACTION_NAME =  URL_GAME;
    public static float GAME_DICE_HALF_SIZE = 0.15f;
    public static short BOX_SHAPE_TYPE = 1;
    public static short UNKNOWN_SHAPE_TYPE = 0;
    private static final float DICE_DEFAULT_WEIGHT = 10f;
    public static final String ON_DICE_OBJECT_INIT = "onDiceObjectInit";

    protected List<AbstractGamePoint> gamePoints;
    public String mapId;
    public long createdDate;

    protected List<InteractiveGameItem> gameItems = null;

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

    public InputStream getLuaScript(SysUtilsWrapperInterface sysUtilsWrapper, String name) {
        return sysUtilsWrapper.getResourceStream("scripts/" + name + ".lua");
    }

    //TODO: replace with REST object
    public List<InteractiveGameItem> getGameItems() {
        if (gameItems == null) {
            gameItems = new ArrayList<>();

            InteractiveGameItem dice = createNewItem(DICE_MESH_OBJECT_1,
                                                     TERRAIN_MESH_OBJECT,
                                                     new Vector3f(0f, 0.08f, 0f),
                                                     GAME_DICE_HALF_SIZE,
                                                     false,
                                                     BOX_SHAPE_TYPE,
                                                     DICE_DEFAULT_WEIGHT,
                                                     MOVING_OBJECT,
                                                     TERRAIN_OBJECT,
                                                     ON_DICE_OBJECT_INIT);

            gameItems.add(dice);
        }

        return gameItems;
    }

    @SuppressWarnings("unused")
    public InteractiveGameItem createNewItem(String itemName,
                                             String itemParentName,
                                             float mass,
                                             int tag,
                                             int materialID) {
        return createNewItem(itemName,
                             itemParentName,
                             new Vector3f(0f, 0f, 0f),
                             1f,
                             false,
                             UNKNOWN_SHAPE_TYPE,
                             mass,
                             tag,
                             GLRenderConsts.GLObjectType.values()[materialID],
                             null);
    }

    public InteractiveGameItem createNewItem(String itemName,
                                             String itemParentName,
                                             Vector3f pos,
                                             float initialScale,
                                             boolean hasTwoSidedSurface,
                                             short collisionShapeType,
                                             float mass,
                                             int tag,
                                             GLRenderConsts.GLObjectType type,
                                             String onInitEventHandler) {

        return new InteractiveGameItem(itemName,
                                       itemParentName,
                                       pos,
                                       initialScale,
                                       hasTwoSidedSurface,
                                       collisionShapeType,
                                       mass,
                                       tag,
                                       type,
                                       onInitEventHandler);
    }
}
