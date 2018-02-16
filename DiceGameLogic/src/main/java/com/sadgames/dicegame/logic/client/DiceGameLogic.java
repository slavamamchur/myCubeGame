package com.sadgames.dicegame.logic.client;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.sadgames.dicegame.logic.client.entities.DiceGameMap;
import com.sadgames.dicegame.logic.client.entities.items.ChipItem;
import com.sadgames.dicegame.logic.client.entities.items.GameDiceItem;
import com.sadgames.dicegame.logic.server.rest_api.RestApiInterface;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.PointType;
import com.sadgames.gl3d_engine.GameEventsCallbackInterface;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.scene.GLAnimation;
import com.sadgames.gl3d_engine.gl_render.scene.GLCamera;
import com.sadgames.gl3d_engine.gl_render.scene.GLLightSource;
import com.sadgames.gl3d_engine.gl_render.scene.GLScene;
import com.sadgames.gl3d_engine.gl_render.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.GameItemObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.PNodeObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.TopographicMapObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.client.GameConst.ACTION_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.DICE_TEXTURE;
import static com.sadgames.dicegame.logic.client.GameConst.DUDVMAP_TEXTURE;
import static com.sadgames.dicegame.logic.client.GameConst.NORMALMAP_TEXTURE;
import static com.sadgames.dicegame.logic.client.GameConst.ROLLING_DICE_SOUND;
import static com.sadgames.dicegame.logic.client.GameConst.SEA_BOTTOM_TEXTURE;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.CHIP_MESH_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.TERRAIN_MESH_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.scene.GLScene.CAMERA_ZOOM_ANIMATION_DURATION;

public class DiceGameLogic implements GameEventsCallbackInterface {

    private static final long CHIP_ANIMATION_DURATION = 500;

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

    public void playerNextMove(GameInstanceEntity gameInstance) {
        restApiWrapper.moveGameInstance(gameInstance);
    }

    public void requestFinishGame() {
        restApiWrapper.finishGameInstance(gameInstanceEntity);
    }
    public void onGameFinished() {
        gameInstanceEntity.setState(GameInstanceEntity.State.FINISHED);
    }

    public void requestRestartGame() {
        restApiWrapper.restartGameInstance(gameInstanceEntity);
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

    @Override
    public void onPerformUserAction(String action, Object[] params) {
        GameConst.UserActionType actionType = GameConst.UserActionType.values()[ACTION_LIST.indexOf(action)];
        switch (actionType) {
            default:
        }
    }

    @Override
    public void onStopMovingObject(PNodeObject gameObject) {
        if (gameObject instanceof GameDiceItem)
            removeDice((GameDiceItem) gameObject);
    }

    @Override
    public void onRollingObjectStart(PNodeObject gameObject) {
        sysUtilsWrapper.iPlaySound(ROLLING_DICE_SOUND);
    }

    @Override
    public void onRollingObjectStop(PNodeObject gameObject) {
        sysUtilsWrapper.iStopSound();
    }

    @Override
    public void onInitGLCamera(GLCamera camera) {
        camera.directSetPitch(gameEntity._getStartCameraPitch());
        camera.directSetYaw(gameEntity._getStartCameraYaw());
        camera.directSetRoll(gameEntity._getStartCameraRoll());
        camera.setCameraPosition(gameEntity._getStartCameraPosition());
    }

    @Override
    public void onInitLightSource(GLLightSource lightSource) {
        lightSource.setLightPosInModelSpace(new float[] {gameEntity._getStartSunPosition().x,
                gameEntity._getStartSunPosition().y,
                gameEntity._getStartSunPosition().z,
                1.0f});

        lightSource.setLightColour(gameEntity._getStartSunColor());
    }

    @Override
    public void onInitPhysics(DynamicsWorld dynamicsWorld) {
        dynamicsWorld.setGravity(gameEntity._getGravity());
    }

    @Override
    public void onLoadSceneObjects(GLScene glSceneObject, DynamicsWorld dynamicsWorldObject) {
        /** materials lib objects */
        AbstractTexture reflectionMap = new BitmapTexture(sysUtilsWrapper, SEA_BOTTOM_TEXTURE);
        AbstractTexture normalMap = new BitmapTexture(sysUtilsWrapper, NORMALMAP_TEXTURE);
        AbstractTexture dudvMap = new BitmapTexture(sysUtilsWrapper, DUDVMAP_TEXTURE);
        GLShaderProgram program = glSceneObject.getCachedShader(TERRAIN_OBJECT);

        TopographicMapObject terrain = new DiceGameMap(sysUtilsWrapper, program, gameEntity);
        terrain.setGlCubeMapId(reflectionMap.getTextureId());
        terrain.setGlNormalMapId(normalMap.getTextureId());
        terrain.setGlDUDVMapId(dudvMap.getTextureId());
        terrain.loadObject();

        terrain.createRigidBody();
        dynamicsWorldObject.addRigidBody(terrain.get_body());

        glSceneObject.addObject(terrain, TERRAIN_MESH_OBJECT);

        if (gameInstanceEntity != null && gameEntity.getGamePoints() != null)
            placeChips(glSceneObject, program);

        GameDiceItem dice_1 = new GameDiceItem(sysUtilsWrapper, program, DICE_TEXTURE);
        dice_1.loadObject();
        Matrix4f translation = new Matrix4f();
        translation.setIdentity();
        translation.setTranslation(new Vector3f(-100f, GameDiceItem.GAME_DICE_HALF_SIZE, 0));
        dice_1.setModelMatrix(MathUtils.getOpenGlMatrix(translation));
        glSceneObject.addObject(dice_1, DICE_MESH_OBJECT_1);
    }



    public void movingChipAnimation(GLAnimation.AnimationCallBack delegate) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];
        int movedPlayerIndex = -1;

        for (InstancePlayer player : gameInstanceEntity.getPlayers())
            playersOnWayPoints[player.getCurrentPoint()]++;

        AbstractGamePoint endGamePoint = null;
        int playersCnt = 0;

        if (savedPlayers != null)
            for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
                int currentPointIdx = gameInstanceEntity.getPlayers().get(i).getCurrentPoint();

                if (savedPlayers.get(i).getCurrentPoint() != currentPointIdx) {
                    movedPlayerIndex = i;
                    endGamePoint = gameEntity.getGamePoints().get(currentPointIdx);
                    playersCnt = playersOnWayPoints[currentPointIdx] - 1;

                    break;
                }
            }

        if (movedPlayerIndex >= 0)
            try {
                animateChip(delegate, endGamePoint, playersCnt,
                        gl3DScene.getObject( CHIP_MESH_OBJECT + "_" + savedPlayers.get(movedPlayerIndex).getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            restApiWrapper.moveGameInstance(gameInstanceEntity);

        if (savedPlayers != null) savedPlayers.clear();
        savedPlayers = new ArrayList<>(gameInstanceEntity.getPlayers());
    }

    private void animateChip(GLAnimation.AnimationCallBack delegate, AbstractGamePoint endGamePoint, int playersCnt, AbstractGL3DObject chip) throws Exception {
        playersCnt = playersCnt < 0 ? 0 : playersCnt;

        if (endGamePoint == null)
            throw new Exception("Invalid game point.");

        Vector2f chipPlace = getChipPlace(gl3DScene, endGamePoint, playersCnt,
                (gameInstanceEntity.getStepsToGo() == 0) || endGamePoint.getType().equals(PointType.FINISH));

        GLAnimation move;
        move = new GLAnimation(
                chip.getPlace().x, chipPlace.x,
                0f, 0f,
                chip.getPlace().y, chipPlace.y,
                CHIP_ANIMATION_DURATION
        );

        chip.setPlace(chipPlace);
        chip.setAnimation(move);
        move.startAnimation(chip, delegate);
    }

    public void placeChips(GLScene mScene, GLShaderProgram program) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        GameItemObject prevChip = null;
        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            Vector2f chipPlace = getChipPlaceByWayPoint(mScene, playersOnWayPoints, player, true);

            GameItemObject chip = new ChipItem(sysUtilsWrapper, program, player);
            if (prevChip == null)
                chip.loadObject();
            else {
                chip.loadFromObject(prevChip);
                prevChip = chip;
            }
            mScene.addObject(chip, chip.getItemName());

            chip.setInWorldPosition(chipPlace);
        }
    }

    public void moveChips(GLScene mScene) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            Vector2f chipPlace = getChipPlaceByWayPoint(mScene, playersOnWayPoints, player, true);

            AbstractGL3DObject chip = mScene.getObject( CHIP_MESH_OBJECT + "_" + player.getName());

            chip.setInWorldPosition(chipPlace);
        }
    }

    public Vector2f getChipPlaceByWayPoint(GLScene mScene, int[] playersOnWayPoints, InstancePlayer player, boolean rotate) {
        int currentPointIdx = player.getCurrentPoint();
        playersOnWayPoints[currentPointIdx]++;
        int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
        AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

        return getChipPlace(mScene, point, playersCnt, rotate);
    }

    public Vector2f getChipPlace(GLScene mScene, AbstractGamePoint point, int playersCnt, boolean rotate) {
        TopographicMapObject map = (TopographicMapObject) mScene.getObject(TERRAIN_MESH_OBJECT);
        float scaleFactor = map.getGlTexture().getWidth() * 1.0f / TopographicMapObject.DEFAULT_TEXTURE_SIZE;
        double toX2 = point.getxPos() * scaleFactor;
        double toZ2 = point.getyPos() * scaleFactor;

        if(rotate) {
            double angle = getChipRotationAngle(playersCnt);
            toX2 = point.getxPos() * scaleFactor - 7.5f * scaleFactor * Math.sin(angle);
            toZ2 = point.getyPos() * scaleFactor - 7.5f * scaleFactor * Math.cos(angle);
        }

        return map.map2WorldCoord(new Vector2f((float)toX2, (float)toZ2));
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
                    playerNextMove(gameInstanceEntity);
                }
            });

            dice.setPosition(new Vector3f(100, 0, 0));
        }
    }

}
