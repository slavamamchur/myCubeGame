package com.sadgames.dicegame.logic.client;

import android.graphics.PointF;

import com.sadgames.dicegame.logic.client.entities.items.GameDiceItem;
import com.sadgames.dicegame.logic.server.rest_api.RestApiInterface;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.scene.GLAnimation;
import com.sadgames.gl3d_engine.gl_render.scene.GLScene;
import com.sadgames.gl3d_engine.gl_render.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.TopographicMapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector3f;

import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.CHIP_MESH_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.TERRAIN_MESH_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.scene.GLScene.CAMERA_ZOOM_ANIMATION_DURATION;

public class DiceGameLogic {

    private SysUtilsWrapperInterface sysUtilsWrapper;
    private RestApiInterface restApiWrapper;
    private GLScene gl3DScene;
    private GameMapEntity mapEntity = null;
    private GameEntity gameEntity = null;
    private GameInstanceEntity gameInstanceEntity = null;
    private List<InstancePlayer> savedPlayers = null;

    public DiceGameLogic(SysUtilsWrapperInterface sysUtilsWrapper, RestApiInterface restApiWrapper, GLScene gl3DScene) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        this.restApiWrapper = restApiWrapper;
        this.gl3DScene = gl3DScene;
    }

    public GameMapEntity getMapEntity() {
        return mapEntity;
    }
    public void setMapEntity(GameMapEntity mapEntity) {
        this.mapEntity = mapEntity;
    }
    public GameEntity getGameEntity() {
        return gameEntity;
    }
    public void setGameEntity(GameEntity gameEntity) {
        this.gameEntity = gameEntity;
    }
    public GameInstanceEntity getGameInstanceEntity() {
        return gameInstanceEntity;
    }
    public void setGameInstanceEntity(GameInstanceEntity gameInstanceEntity) {
        this.gameInstanceEntity = gameInstanceEntity;
    }
    public List<InstancePlayer> getSavedPlayers() {
        return savedPlayers;
    }
    public void setSavedPlayers(List<InstancePlayer> savedPlayers) {
        this.savedPlayers = savedPlayers;
    }

    public void playTurn() {
        gl3DScene.setZoomCameraAnimation(new GLAnimation(1 / 2f, CAMERA_ZOOM_ANIMATION_DURATION));
        gl3DScene.getZoomCameraAnimation().startAnimation(null, new GLAnimation.AnimationCallBack() {
            @Override
            public void onAnimationEnd() {
                rollDice();
            }
        });
    }

    public void onGameFinished() {
        gameInstanceEntity.setState(GameInstanceEntity.State.FINISHED);
    }

    public void onGameRestarted() {
        gameInstanceEntity.setState(GameInstanceEntity.State.WAIT);
        gameInstanceEntity.setCurrentPlayer(0);
        gameInstanceEntity.setStepsToGo(0);
        for (InstancePlayer player : gameInstanceEntity.getPlayers()) {
            player.setCurrentPoint(0);
            player.setFinished(false);
            player.setSkipped(false);
        }

        updateMap();
    }

    private void updateMap() {
        if (mapEntity == null || mapEntity.getId() == null)
            return;

        savedPlayers.clear();
        savedPlayers = new ArrayList<>(gameInstanceEntity.getPlayers());

        moveChips(gl3DScene);
    }

    public void moveChips(GLScene mScene) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            PointF chipPlace = getChipPlaceByWayPoint(mScene, playersOnWayPoints, player, true);

            AbstractGL3DObject chip = mScene.getObject( CHIP_MESH_OBJECT + "_" + player.getName());

            chip.setInWorldPosition(chipPlace);
        }
    }

    public PointF getChipPlaceByWayPoint(GLScene mScene, int[] playersOnWayPoints, InstancePlayer player, boolean rotate) {
        int currentPointIdx = player.getCurrentPoint();
        playersOnWayPoints[currentPointIdx]++;
        int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
        AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

        return getChipPlace(mScene, point, playersCnt, rotate);
    }

    public PointF getChipPlace(GLScene mScene, AbstractGamePoint point, int playersCnt, boolean rotate) {
        TopographicMapObject map = (TopographicMapObject) mScene.getObject(TERRAIN_MESH_OBJECT);
        float scaleFactor = map.getGlTexture().getWidth() * 1.0f / TopographicMapObject.DEFAULT_TEXTURE_SIZE;
        double toX2 = point.getxPos() * scaleFactor;
        double toZ2 = point.getyPos() * scaleFactor;

        if(rotate) {
            double angle = getChipRotationAngle(playersCnt);
            toX2 = point.getxPos() * scaleFactor - 7.5f * scaleFactor * Math.sin(angle);
            toZ2 = point.getyPos() * scaleFactor - 7.5f * scaleFactor * Math.cos(angle);
        }

        return map.map2WorldCoord(new PointF((float)toX2, (float)toZ2));
    }

    private static double getChipRotationAngle(int playersCnt) {
        if (playersCnt == 0)
            return  0;

        int part = 8, b;
        double angle;

        do {
            angle = 360 / part;
            b = part - 1;

            part /= 2;
        } while ( ((playersCnt & part) == 0) && (part != 1) );

        return Math.toRadians((2 * playersCnt - b) * angle);
    }

    private void rollDice() {
        GameDiceItem dice_1 = (GameDiceItem)gl3DScene.getObject(DICE_MESH_OBJECT_1);
        dice_1.createRigidBody();
        ///Transform tr = new Transform(new Matrix4f(dice_1.getModelMatrix()));
        ///dice_1.get_body().setWorldTransformMatrix(tr);
        Random rnd = new Random(System.currentTimeMillis());
        int direction = rnd.nextInt(2);
        float fy = 2f + rnd.nextInt(3) * 1f;
        float fxz = fy * 2f / 3f;
        fxz = direction == 1 && (rnd.nextInt(2) > 0) ? -1*fxz : fxz;
        dice_1.get_body().setLinearVelocity(direction == 0 ? new Vector3f(0f,fy,fxz) : new Vector3f(fxz,fy,0f));
        gl3DScene.getPhysicalWorldObject().addRigidBody(dice_1.get_body());
    }

    public void removeDice(GameDiceItem dice) {
        //toggleActionBarProgress(true);
        if (gameInstanceEntity != null) {
            gameInstanceEntity.setStepsToGo(dice.getTopFaceDiceValue());
            restApiWrapper.showTurnInfo(gameInstanceEntity);

            gl3DScene.setZoomCameraAnimation(new GLAnimation(1 * 2f, CAMERA_ZOOM_ANIMATION_DURATION));
            gl3DScene.getZoomCameraAnimation().startAnimation(null, new GLAnimation.AnimationCallBack() {
                @Override
                public void onAnimationEnd() {
                    restApiWrapper.moveGameInstance(gameInstanceEntity);
                }
            });

            dice.setPosition(new Vector3f(100, 0, 0));
        }
    }

}
