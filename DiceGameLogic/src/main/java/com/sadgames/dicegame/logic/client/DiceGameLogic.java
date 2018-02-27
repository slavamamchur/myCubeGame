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
import com.sadgames.gl3dengine.GameEventsCallbackInterface;
import com.sadgames.gl3dengine.glrender.GLRenderConsts;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.GameItemObject;
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject;
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem;
import com.sadgames.gl3dengine.glrender.scene.objects.SkyBoxObject;
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.CubeMapTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.SkyBoxProgram;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.common.MathUtils;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.client.GameConst.ACTION_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.CHIP_MESH_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.DICE_MESH_OBJECT_1;
import static com.sadgames.dicegame.logic.client.GameConst.DICE_TEXTURE;
import static com.sadgames.dicegame.logic.client.GameConst.ROLLING_DICE_SOUND;
import static com.sadgames.dicegame.logic.client.GameConst.SKY_BOX_CUBE_MAP_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.SKY_BOX_TEXTURE_NAME;
import static com.sadgames.dicegame.logic.client.GameConst.TERRAIN_MESH_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.SKY_BOX_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;

public class DiceGameLogic implements GameEventsCallbackInterface {

    private static final long CHIP_ANIMATION_DURATION = 500;
    private final static long CAMERA_ZOOM_ANIMATION_DURATION = 1000;

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
        /** for future init from Game Level object */
        /*camera.directSetPitch(gameEntity._getStartCameraPitch());
        camera.directSetYaw(gameEntity._getStartCameraYaw());
        camera.directSetRoll(gameEntity._getStartCameraRoll());
        camera.setCameraPosition(gameEntity._getStartCameraPosition());
        camera.setVfov(gameEntity._getStartCameraVFOV());*/

        /** for test */
        camera.rotateX(22.5f);
        camera.updateViewMatrix();
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
    public void onLoadSceneObjects(GLScene glScene, DynamicsWorld dynamicsWorldObject) {
        GLRenderConsts.GraphicsQuality graphicsQuality = sysUtilsWrapper.iGetSettingsManager().getGraphicsQualityLevel();

        /** Skybox and water reflection map texture */
        CubeMapTexture skyBoxTexture =
            new CubeMapTexture(sysUtilsWrapper, gameEntity._getSkyBoxTextureNames(), SKY_BOX_TEXTURE_NAME);
        TextureCacheManager.getInstance(sysUtilsWrapper).putItem(skyBoxTexture,
                                                                 skyBoxTexture.getTextureName(),
                                                                 skyBoxTexture.getTextureSize());

        GLShaderProgram program = glScene.getCachedShader(TERRAIN_OBJECT);
        /** Terrain map */
        TopographicMapObject terrain = new DiceGameMap(sysUtilsWrapper, program, gameEntity);
        if (GLRenderConsts.GraphicsQuality.ULTRA.equals(graphicsQuality))
            terrain.setWaterReflectionMap(skyBoxTexture);
        terrain.loadObject();
        terrain.createRigidBody();
        dynamicsWorldObject.addRigidBody(terrain.get_body());
        glScene.putChild(terrain, TERRAIN_MESH_OBJECT);

        /** players chips */
        if (gameInstanceEntity != null && gameEntity.getGamePoints() != null)
            placePlayersChips(terrain, program);

        /** gaming dice */
        GameDiceItem gameDice_1 = new GameDiceItem(sysUtilsWrapper, program, DICE_TEXTURE);
        gameDice_1.loadObject();
        Matrix4f transformMatrix = new Matrix4f();
        transformMatrix.setIdentity();
        transformMatrix.setTranslation(new Vector3f(-100f, GameDiceItem.GAME_DICE_HALF_SIZE, 0));
        gameDice_1.setModelMatrix(MathUtils.getOpenGlMatrix(transformMatrix));
        glScene.putChild(gameDice_1, DICE_MESH_OBJECT_1);

        /** debug shadow map gui-box */
        /*GUI2DImageObject shadowMapView = new GUI2DImageObject(sysUtilsWrapper,
                                                              glScene.getCachedShader(GUI_OBJECT),
                                                              new Vector4f(-1, 1, 0, 0), false);
        shadowMapView.loadObject();
        shadowMapView.setGlTexture(glScene.getShadowMapFBO().getFboTexture());
        glScene.putChild(shadowMapView,"DEBUG_SHADOW_MAP_VIEW");*/

        /** sky-box */
        GLCamera camera = glScene.getCamera();
        SkyBoxProgram shader = (SkyBoxProgram) glScene.getCachedShader(SKY_BOX_OBJECT);
        shader.setCamera(camera);
        SkyBoxObject skyBoxObject = new SkyBoxObject(sysUtilsWrapper, skyBoxTexture, shader);
        skyBoxObject.loadObject();
        glScene.putChild(skyBoxObject, SKY_BOX_CUBE_MAP_OBJECT);
    }

    @Override
    public void onBeforeDrawFrame(long frametime) {
        SkyBoxObject skyBox = (SkyBoxObject) gl3DScene.getObject(SKY_BOX_CUBE_MAP_OBJECT);
        float angle = skyBox.getRotationAngle() + 0.5f * frametime / 250f;
        skyBox.setRotationAngle(angle > 360f ? 360f - angle : angle);
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

        Vector2f chipPlace = getChipPlace(chip.getParent(), endGamePoint, playersCnt,
                (gameInstanceEntity.getStepsToGo() == 0) || endGamePoint.getType().equals(PointType.FINISH));

        GLAnimation move = new GLAnimation(
                                            chip.getPlace().x, chipPlace.x,
                                           0f, 0f,
                                            chip.getPlace().y, chipPlace.y,
                                            CHIP_ANIMATION_DURATION
                                           );

        chip.setPlace(chipPlace);
        chip.setAnimation(move);
        move.startAnimation(chip, delegate);
    }

    public void placePlayersChips(SceneObjectsTreeItem parent, GLShaderProgram program) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        GameItemObject prevChip = null;
        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            GameItemObject chip = new ChipItem(sysUtilsWrapper, program, player);

            Vector2f chipPlace = getChipPlaceByWayPoint(parent, playersOnWayPoints, player, true);
            chip.setInWorldPosition(chipPlace);

            if (prevChip == null) chip.loadObject(); else chip.loadFromObject(prevChip);

            parent.putChild(chip, chip.getItemName());

            prevChip = chip;
        }
    }

    public void moveChips(GLScene mScene) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);

            ChipItem chip = (ChipItem) mScene.getObject(CHIP_MESH_OBJECT + "_" + player.getName());
            chip.setInWorldPosition(getChipPlaceByWayPoint(chip.getParent(), playersOnWayPoints, player, true));
        }
    }

    public Vector2f getChipPlaceByWayPoint(SceneObjectsTreeItem parent, int[] playersOnWayPoints, InstancePlayer player, boolean rotate) {
        int currentPointIdx = player.getCurrentPoint();
        playersOnWayPoints[currentPointIdx]++;
        int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
        AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

        return getChipPlace(parent, point, playersCnt, rotate);
    }

    public Vector2f getChipPlace(SceneObjectsTreeItem parent, AbstractGamePoint point, int playersCnt, boolean rotate) {
        TopographicMapObject map = (TopographicMapObject) parent;
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
