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
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;
import com.sadgames.gl3dengine.glrender.scene.camera.Orthogonal2DCamera;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractSkyObject;
import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.GUI2DImageObject;
import com.sadgames.gl3dengine.glrender.scene.objects.GameItemObject;
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject;
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem;
import com.sadgames.gl3dengine.glrender.scene.objects.SkyDomeObject;
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.common.MathUtils;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import static com.sadgames.dicegame.logic.client.GameConst.ACTION_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.CHIP_MESH_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.DICE_MESH_OBJECT_1;
import static com.sadgames.dicegame.logic.client.GameConst.MAP_BACKGROUND_TEXTURE_NAME;
import static com.sadgames.dicegame.logic.client.GameConst.MINI_MAP_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.ON_BEFORE_DRAW_FRAME_EVENT_HANDLER;
import static com.sadgames.dicegame.logic.client.GameConst.ON_ROLLING_OBJECT_START_EVENT_HANDLER;
import static com.sadgames.dicegame.logic.client.GameConst.ON_ROLLING_OBJECT_STOP_EVENT_HANDLER;
import static com.sadgames.dicegame.logic.client.GameConst.SKY_BOX_CUBE_MAP_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.SKY_DOME_TEXTURE_NAME;
import static com.sadgames.dicegame.logic.client.GameConst.TERRAIN_MESH_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.UserActionType;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;
import static com.sadgames.sysutils.common.CommonUtils.forceGCandWait;

public class DiceGameLogic implements GameEventsCallbackInterface, ResourceFinder {

    private static final long CHIP_ANIMATION_DURATION = 500;
    private final static long CAMERA_ZOOM_ANIMATION_DURATION = 1000;

    private SysUtilsWrapperInterface sysUtilsWrapper;
    private RestApiInterface restApiWrapper;
    private GLScene gl3DScene;
    private GameMapEntity mapEntity = null;
    private GameEntity gameEntity = null;
    private GameInstanceEntity gameInstanceEntity = null;
    private List<InstancePlayer> savedPlayers = null;
    private Globals luaEngine;

    public DiceGameLogic(SysUtilsWrapperInterface sysUtilsWrapper, RestApiInterface restApiWrapper, GLScene gl3DScene) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        this.restApiWrapper = restApiWrapper;
        this.gl3DScene = gl3DScene;

        initScriptEngine();
    }

    private void initScriptEngine() {
        luaEngine = JsePlatform.standardGlobals();
        luaEngine.finder = this;

        LuaValue luaSysUtilsWrapper = CoerceJavaToLua.coerce(sysUtilsWrapper);
        LuaValue luaGl3DScene = CoerceJavaToLua.coerce(gl3DScene);

        luaEngine.loadfile("scripts/gameLogic.lua").call(luaSysUtilsWrapper, luaGl3DScene);
    }

    @SuppressWarnings("unused") public GameMapEntity getMapEntity() {
        return mapEntity;
    }
    public void setMapEntity(GameMapEntity mapEntity) {
        this.mapEntity = mapEntity;
    }
    @SuppressWarnings("unused") public GameEntity getGameEntity() {
        return gameEntity;
    }
    public void setGameEntity(GameEntity gameEntity) {
        this.gameEntity = gameEntity;
    }
    @SuppressWarnings("unused") public GameInstanceEntity getGameInstanceEntity() {
        return gameInstanceEntity;
    }
    public void setGameInstanceEntity(GameInstanceEntity gameInstanceEntity) {
        this.gameInstanceEntity = gameInstanceEntity;
    }
    @SuppressWarnings("unused") public List<InstancePlayer> getSavedPlayers() {
        return savedPlayers;
    }
    public void setSavedPlayers(List<InstancePlayer> savedPlayers) {
        this.savedPlayers = savedPlayers;
    }

    @SuppressWarnings("all")
    public void playTurn() {
        gl3DScene.setZoomCameraAnimation(new GLAnimation(1 / 2f, CAMERA_ZOOM_ANIMATION_DURATION));
        gl3DScene.getZoomCameraAnimation().startAnimation(null, () -> rollDice());
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
        UserActionType actionType = UserActionType.values()[ACTION_LIST.indexOf(action)];
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
        luaEngine.get(ON_ROLLING_OBJECT_START_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameObject));
    }

    @Override
    public void onRollingObjectStop(PNodeObject gameObject) {
        luaEngine.get(ON_ROLLING_OBJECT_STOP_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameObject));
    }

    @Override
    public void onInitGLCamera(GLCamera camera) {
        /** for future init from Game Level object */
        /*camera.directSetPitch(gameEntity._getStartCameraPitch());
        camera.directSetYaw(gameEntity._getStartCameraYaw());
        camera.directSetRoll(gameEntity._getStartCameraRoll());
        camera.setCameraPosition(gameEntity._getStartCameraPosition());
        camera.setVfov(gameEntity._getStartCameraVFOV());*/

        if (!sysUtilsWrapper.iGetSettingsManager().isIn_2D_Mode())
            camera.rotateX(22.5f);
        //else
            //camera.setZoomLevel(0.7f);

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
        ///GLRenderConsts.GraphicsQuality graphicsQuality = sysUtilsWrapper.iGetSettingsManager().getGraphicsQualityLevel();

        TextureCacheManager.getNewInstance(sysUtilsWrapper);
        TextureCacheManager.getInstance(sysUtilsWrapper).getItem(MAP_BACKGROUND_TEXTURE_NAME);
        glScene.setBackgroundTextureName(MAP_BACKGROUND_TEXTURE_NAME);

        GLShaderProgram program = glScene.getCachedShader(TERRAIN_OBJECT);

        /** Terrain map */
        TopographicMapObject terrain = new DiceGameMap(sysUtilsWrapper, program, gameEntity);
        //terrain.setWaterReflectionMap(skyBoxTexture);
        terrain.loadObject();
        terrain.createRigidBody();
        dynamicsWorldObject.addRigidBody(terrain.get_body());
        glScene.putChild(terrain, TERRAIN_MESH_OBJECT);

        /** players chips */
        if (gameInstanceEntity != null && gameEntity.getGamePoints() != null)
            placePlayersChips(terrain, program);

        /** gaming dice */
        GameDiceItem gameDice_1 = new GameDiceItem(sysUtilsWrapper, program);
        gameDice_1.loadObject();
        Matrix4f transformMatrix = new Matrix4f();
        transformMatrix.setIdentity();
        transformMatrix.setTranslation(new Vector3f(-100f, 0f, 0f));
        gameDice_1.setModelMatrix(MathUtils.getOpenGlMatrix(transformMatrix));
        glScene.putChild(gameDice_1, DICE_MESH_OBJECT_1);

        /** sky-box */
        AbstractTexture skyDomeTexture = TextureCacheManager.getInstance(sysUtilsWrapper).getItem(SKY_DOME_TEXTURE_NAME);
        AbstractSkyObject skyBoxObject =
                new SkyDomeObject(sysUtilsWrapper,/*skyBoxTexture*/skyDomeTexture, glScene);
        skyBoxObject.loadObject();
        glScene.putChild(skyBoxObject, SKY_BOX_CUBE_MAP_OBJECT);

        /** mini-map gui-box */
        if (!sysUtilsWrapper.iGetSettingsManager().isIn_2D_Mode()) {
            GUI2DImageObject miniMapView = new GUI2DImageObject(sysUtilsWrapper,
                    glScene.getCachedShader(GUI_OBJECT),
                    new Vector4f(-1, 1, -0.75f, 0.5f), true);
            miniMapView.loadObject();
            miniMapView.setGlTexture(terrain.getGlTexture());
            glScene.putChild(miniMapView, MINI_MAP_OBJECT);
        }

        forceGCandWait();
        restApiWrapper.removeLoadingSplash();
    }

    @Override
    public void onBeforeDrawFrame(long frametime) {
        luaEngine.get(ON_BEFORE_DRAW_FRAME_EVENT_HANDLER).call(CoerceJavaToLua.coerce(frametime));

        /*if (!sysUtilsWrapper.iGetSettingsManager().isIn_2D_Mode())
            gl3DScene.getObject(MINI_MAP_OBJECT).setGlTexture(gl3DScene.getShadowMapFBO().getFboTexture());*/
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

    private void placePlayersChips(SceneObjectsTreeItem parent, GLShaderProgram program) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        GameItemObject prevChip = null;
        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            Blender3DObject chip = new ChipItem(sysUtilsWrapper, program, player);

            Vector2f chipPlace = getChipPlaceByWayPoint(parent, playersOnWayPoints, player, true);
            chip.setInWorldPosition(chipPlace);

            if (prevChip == null)
                chip.loadObject();
            else
                chip.loadFromObject(prevChip);

            parent.putChild(chip, chip.getItemName());

            prevChip = chip;
        }
    }

    private void moveChips(GLScene mScene) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);

            ChipItem chip = (ChipItem) mScene.getObject(CHIP_MESH_OBJECT + "_" + player.getName());
            chip.setInWorldPosition(getChipPlaceByWayPoint(chip.getParent(), playersOnWayPoints, player, true));
        }
    }

    @SuppressWarnings("all")
    private Vector2f getChipPlaceByWayPoint(SceneObjectsTreeItem parent, int[] playersOnWayPoints, InstancePlayer player, boolean rotate) {
        int currentPointIdx = player.getCurrentPoint();
        playersOnWayPoints[currentPointIdx]++;
        int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
        AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

        return getChipPlace(parent, point, playersCnt, rotate);
    }

    private Vector2f getChipPlace(SceneObjectsTreeItem parent, AbstractGamePoint point, int playersCnt, boolean rotate) {
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

    private boolean old2D_ModeValue;
    private void switrchTo2DMode() {
        synchronized (GLScene.lockObject) {
            old2D_ModeValue = sysUtilsWrapper.iGetSettingsManager().isIn_2D_Mode();
            sysUtilsWrapper.iGetSettingsManager().setIn_2D_Mode(true);
            gl3DScene.setCamera(new Orthogonal2DCamera(LAND_SIZE_IN_WORLD_SPACE));
        }
    }

    private void restorePrevViewMode() {
        synchronized (GLScene.lockObject) {
            sysUtilsWrapper.iGetSettingsManager().setIn_2D_Mode(old2D_ModeValue);
            gl3DScene.setCamera(null);
        }
    }

    private void rollDice() {
        switrchTo2DMode();

        GameDiceItem dice_1 = (GameDiceItem)gl3DScene.getObject(DICE_MESH_OBJECT_1);
        dice_1.createRigidBody();
        dice_1.generateInitialTransform();
        dice_1.generateForceVector();

        gl3DScene.getPhysicalWorldObject().addRigidBody(dice_1.get_body());
    }

    private void removeDice(GameDiceItem dice) {
        //toggleActionBarProgress(true);
        if (gameInstanceEntity != null) {
            gameInstanceEntity.setStepsToGo(dice.getTopFaceDiceValue());
            restApiWrapper.showTurnInfo(gameInstanceEntity);

            restorePrevViewMode();
            dice.setPosition(new Vector3f(100, 0, 0));

            gl3DScene.setZoomCameraAnimation(new GLAnimation(1 * 2f, CAMERA_ZOOM_ANIMATION_DURATION));
            gl3DScene.getZoomCameraAnimation().startAnimation(null, () -> playerNextMove(gameInstanceEntity));

        }
    }

    @Override
    public InputStream findResource(String name) {
        return sysUtilsWrapper.getResourceStream(name);
    }
}
