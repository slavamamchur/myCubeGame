package com.sadgames.gl3dengine.gamelogic.client;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.sadgames.gl3dengine.GameEventsCallbackInterface;
import com.sadgames.gl3dengine.gamelogic.client.entities.GameMap;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.items.InteractiveGameItem;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractSkyObject;
import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.GUI2DImageObject;
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject;
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem;
import com.sadgames.gl3dengine.glrender.scene.objects.SkyDomeObject;
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.InputStream;
import java.util.List;

import javax.vecmath.Vector4f;

import static com.sadgames.gl3dengine.gamelogic.client.GameConst.GameState;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.MAP_BACKGROUND_TEXTURE_NAME;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.MINI_MAP_OBJECT;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_BEFORE_DRAW_FRAME_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_CREATE_DYNAMIC_ITEMS_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_GAME_RESTARTED_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_INIT_CAMERA_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_MOVING_OBJECT_STOP_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_PLAYER_MAKE_TURN_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_PREPARE_MAP_TEXTURE_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_ROLLING_OBJECT_START_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_ROLLING_OBJECT_STOP_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.SKY_BOX_CUBE_MAP_OBJECT;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.SKY_DOME_TEXTURE_NAME;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.TERRAIN_MESH_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Y;
import static com.sadgames.sysutils.common.CommonUtils.forceGCandWait;
import static com.sadgames.sysutils.common.LuaUtils.javaList2LuaTable;

public class GameLogic implements GameEventsCallbackInterface, ResourceFinder {

    private final static String LUA_GAME_LOGIC_SCRIPT = "gameLogic";

    private SysUtilsWrapperInterface sysUtilsWrapper;
    private RestApiInterface restApiWrapper;
    private GLScene gl3DScene;
    private GameMapEntity mapEntity = null;
    private GameEntity gameEntity = null;
    private GameInstanceEntity gameInstanceEntity = null;
    private List<InstancePlayer> savedPlayers = null;
    private Globals luaEngine;

    public GameLogic(SysUtilsWrapperInterface sysUtilsWrapper, RestApiInterface restApiWrapper, GLScene gl3DScene) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        this.restApiWrapper = restApiWrapper;
        this.gl3DScene = gl3DScene;
    }

    public void initScriptEngine() {
        luaEngine = JsePlatform.standardGlobals();
        luaEngine.finder = this;
        luaEngine.loadfile(LUA_GAME_LOGIC_SCRIPT).call(CoerceJavaToLua.coerce(this));

        gl3DScene.setLuaEngine(luaEngine);
    }

    public SysUtilsWrapperInterface getSysUtilsWrapper() {
        return sysUtilsWrapper;
    }
    @SuppressWarnings("unused") public GLScene getGl3DScene() {
        return gl3DScene;
    }
    @SuppressWarnings("unused")
    public RestApiInterface getRestApiWrapper() {
        return restApiWrapper;
    }
    @SuppressWarnings("unused") public Globals getLuaEngine() {
        return luaEngine;
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

    public void requestFinishGame() {
        restApiWrapper.finishGameInstance(gameInstanceEntity);
    }
    public void onGameFinished() {
        gameInstanceEntity.setState(GameState.FINISHED);
    }
    public void requestRestartGame() {
        restApiWrapper.restartGameInstance(gameInstanceEntity);
    }

    public void onGameRestarted() {
        savedPlayers.clear();
        savedPlayers = gameInstanceEntity.createPlayersList();

        luaEngine.get(ON_GAME_RESTARTED_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameInstanceEntity));
    }

    @Override
    public void onPerformUserAction(String action, LuaValue[] params) {
        luaEngine.invokemethod(action, params);
    }

    @Override
    public void onStopMovingObject(PNodeObject gameObject) {
        luaEngine.get(ON_MOVING_OBJECT_STOP_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameObject),
                                                                CoerceJavaToLua.coerce(gameInstanceEntity));
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
        luaEngine.get(ON_INIT_CAMERA_EVENT_HANDLER).call(CoerceJavaToLua.coerce(camera));
    }

    @Override
    public void onInitLightSource(GLLightSource lightSource) {
        lightSource.setLightPosInModelSpace(gameEntity._getStartSunPosition());
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
        TopographicMapObject terrain = new GameMap(sysUtilsWrapper, program, gameEntity, this);
        //terrain.setWaterReflectionMap(skyBoxTexture);
        terrain.loadObject();
        terrain.createRigidBody();
        dynamicsWorldObject.addRigidBody(terrain.get_body());
        glScene.putChild(terrain, TERRAIN_MESH_OBJECT);

        /** WP Test --------------------------------------------------------------------------------------------- */
        Blender3DObject wings = new Blender3DObject(sysUtilsWrapper,
                "5bb7042244dd519c21662955",
                program,
                /*0xFFFFFFFF,*/
                1.0f,
                1);
        wings.setInitialScale(0.16667f);
        //wings.setInitialTranslation(0f, 0.33f, 0f);
        //wings.setTwoSidedSurface(true);

        wings.loadObject();
        //wings.setRotationX(-90f);

        GLAnimation spin = glScene.createRotateAnimation(-360f, ROTATE_BY_Y, 4000);
        spin.setRepeatCount((short) 0);
        wings.setAnimation(spin);
        terrain.putChild(wings, "WP_WINGS");
        wings.animationStart();
        /** ------------------------------------------------------------------------------------------------------ */

        loadGameItems(glScene);
        luaEngine.get(ON_CREATE_DYNAMIC_ITEMS_HANDLER).call(CoerceJavaToLua.coerce(gameEntity), CoerceJavaToLua.coerce(gameInstanceEntity));

        /** sky-dome */
        AbstractTexture skyDomeTexture = TextureCacheManager.getInstance(sysUtilsWrapper).getItem(SKY_DOME_TEXTURE_NAME);
        AbstractSkyObject skyDomeObject = new SkyDomeObject(sysUtilsWrapper, skyDomeTexture, glScene);
        skyDomeObject.setItemName(SKY_BOX_CUBE_MAP_OBJECT);
        skyDomeObject.loadObject();
        glScene.putChild(skyDomeObject, skyDomeObject.getItemName());

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
    public void onPrepareMapTexture(BitmapWrapperInterface textureBmp) {
        luaEngine.get(ON_PREPARE_MAP_TEXTURE_EVENT_HANDLER).call(CoerceJavaToLua.coerce(textureBmp),
                                                                 CoerceJavaToLua.coerce(gameEntity));
    }

    @Override
    public void onBeforeDrawFrame(long frametime) {
        luaEngine.get(ON_BEFORE_DRAW_FRAME_EVENT_HANDLER).call(CoerceJavaToLua.coerce(frametime));
    }


    @Override
    public void onPlayerMakeTurn(GLAnimation.AnimationCallBack delegate) {
        luaEngine.get(ON_PLAYER_MAKE_TURN_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameInstanceEntity),
                                                              javaList2LuaTable(savedPlayers),
                                                              CoerceJavaToLua.coerce(delegate));
        savedPlayers.clear();
        savedPlayers = gameInstanceEntity.createPlayersList();
    }

    @Override
    public InputStream findResource(String name) {
        return getGameEntity().getLuaScript(sysUtilsWrapper, name);
    }

    private void loadGameItems(GLScene glScene) {
        Blender3DObject sceneObject;
        for (InteractiveGameItem item : gameEntity.getGameItems()) {
            sceneObject = item.createSceneObject(sysUtilsWrapper, glScene);
            sceneObject.loadObject();

            if (item.onInitEventHandler != null)
                luaEngine.get(item.onInitEventHandler).call(CoerceJavaToLua.coerce(sceneObject));

            getParentNode(glScene, item).putChild(sceneObject, sceneObject.getItemName());
        }
    }

    private SceneObjectsTreeItem getParentNode(GLScene glScene, InteractiveGameItem item) {
        SceneObjectsTreeItem parentObject = glScene.getObject(item.itemParentName != null ? item.itemParentName : "");

        return parentObject != null ? parentObject : glScene;
    }

}
