package com.sadgames.dicegame.ui.platforms.android.framework;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.sadgames.dicegame.game_logic.items.ChipObject;
import com.sadgames.dicegame.game_logic.items.DiceObject;
import com.sadgames.dicegame.game_logic.items.GameMapObject;
import com.sadgames.dicegame.rest_api.RestApiService;
import com.sadgames.dicegame.rest_api.model.entities.ErrorEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameMapEntity;
import com.sadgames.dicegame.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.dicegame.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.dicegame.rest_api.model.entities.points.PointType;
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
import com.sadgames.sysutils.platforms.android.AndroidDiceGameUtilsWrapper;
import com.sadgames.sysutils.platforms.android.AndroidGLES20Renderer;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.game_logic.GameConsts.DUDVMAP_TEXTURE;
import static com.sadgames.dicegame.game_logic.GameConsts.NORMALMAP_TEXTURE;
import static com.sadgames.dicegame.game_logic.GameConsts.ROLLING_DICE_SOUND;
import static com.sadgames.dicegame.game_logic.GameConsts.SEA_BOTTOM_TEXTURE;
import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_ACTION_SHOW_TURN_INFO;
import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_MAP_IMAGE_RESPONSE;
import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_UPLOAD_IMAGE_RESPONSE;
import static com.sadgames.dicegame.rest_api.RestApiService.EXTRA_DICE_VALUE;
import static com.sadgames.dicegame.rest_api.RestApiService.EXTRA_ERROR_OBJECT;
import static com.sadgames.dicegame.rest_api.RestApiService.startActionMooveGameInstance;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.CHIP_MESH_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.TERRAIN_MESH_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.scene.GLScene.CAMERA_ZOOM_ANIMATION_DURATION;

public class MapFragment extends Fragment implements GameEventsCallbackInterface {

    private static final long CHIP_ANIMATION_DURATION = 500;

    private BaseItemDetailsActivity.WebErrorHandler webErrorHandler = null;
    private GameMapEntity mapEntity = null;
    private GameEntity gameEntity = null;
    private GameInstanceEntity gameInstanceEntity = null;
    private Bitmap cachedBitmap;/** Disable editor */
    private SysUtilsWrapperInterface sysUtilsWrapper;

    public List<InstancePlayer> savedPlayers = null;
    public GLSurfaceView glMapSurfaceView;
    public AndroidGLES20Renderer glRenderer;

    public interface ChipAnimadedDelegate {
        void onAnimationEnd();
    }

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        glMapSurfaceView = new MapGLSurfaceView(getContext());
        sysUtilsWrapper = new AndroidDiceGameUtilsWrapper(getContext());
        glRenderer = new AndroidGLES20Renderer(sysUtilsWrapper, this);

        return  glMapSurfaceView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        glMapSurfaceView.setRenderer(glRenderer);
    }

    @Override
    public void onPause() {
        glMapSurfaceView.onPause();

        /*glRenderer.getScene().setRenderStopped(true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        glRenderer.getScene().cleanUp();*/

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        glMapSurfaceView.onResume();
    }

    public void setIntentFilters(IntentFilter intentFilter) {
        intentFilter.addAction(ACTION_MAP_IMAGE_RESPONSE);
        intentFilter.addAction(ACTION_UPLOAD_IMAGE_RESPONSE);
    }

    public void setWebErrorHandler(BaseItemDetailsActivity.WebErrorHandler webErrorHandler) {
        this.webErrorHandler = webErrorHandler;
    }

    public boolean handleWebServiceResponseAction(Intent intent) {
        if (intent.getAction().equals(ACTION_UPLOAD_IMAGE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(EXTRA_ERROR_OBJECT);
            if (error != null) {

                if (webErrorHandler != null)
                    webErrorHandler.onError(error);
            }
            else {
                RestApiService.startActionGetMapImage(getContext(), mapEntity);
            }

            return true;
        }
        else if (intent.getAction().equals(ACTION_MAP_IMAGE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(EXTRA_ERROR_OBJECT);

            if (error == null) {
                //clearImage();
            }
            else
                if (webErrorHandler != null)
                    webErrorHandler.onError(error);

            return true;
        }
        else
            return false;
    }

    public void InitMap(GameMapEntity map, BaseItemDetailsActivity.WebErrorHandler errorHandler) {
        setMapEntity(map);
        glRenderer.setMapID(map == null ? null : map.getId());

        setWebErrorHandler(errorHandler);
    }

    public void InitMap(GameEntity game, BaseItemDetailsActivity.WebErrorHandler errorHandler) {
        setGameEntity(game);

        if (game != null) {
            GameMapEntity map = new GameMapEntity();
            map.setId(game.getMapId());
            InitMap(map, errorHandler);
        }
        else
            setWebErrorHandler(errorHandler);
    }

    public void InitMap(GameInstanceEntity gameInst, BaseItemDetailsActivity.WebErrorHandler errorHandler) {
        setGameInstanceEntity(gameInst);
        savedPlayers = new ArrayList<>(gameInst != null ? gameInst.getPlayers() : null);
        InitMap(gameInst == null ? null : gameInst.getGame(), errorHandler);

        if (gameInst == null)
            setWebErrorHandler(errorHandler);
    }

    private void sendResponseIntent(String action, Bundle params){
        Intent responseIntent = new Intent();
        responseIntent.setAction(action);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtras(params);
        getContext().sendBroadcast(responseIntent);
    }

    private void removeDice(DiceObject dice) {
        //toggleActionBarProgress(true);
        if (gameInstanceEntity != null) {
            gameInstanceEntity.setStepsToGo(dice.getTopFaceDiceValue());

            Bundle params = new Bundle();
            params.putInt(EXTRA_DICE_VALUE, gameInstanceEntity.getStepsToGo());
            sendResponseIntent(ACTION_ACTION_SHOW_TURN_INFO, params);

            glRenderer.getScene().setZoomCameraAnimation(new GLAnimation(1 * 2f, CAMERA_ZOOM_ANIMATION_DURATION));
            glRenderer.getScene().getZoomCameraAnimation().startAnimation(null, new GLAnimation.AnimationCallBack() {
                @Override
                public void onAnimationEnd() {
                    startActionMooveGameInstance(getContext(), gameInstanceEntity);
                }
            });

            dice.setPosition(new Vector3f(100, 0, 0));
        }
    }

    @Override
    public void onStopMovingObject(PNodeObject gameObject) {
        if (gameObject instanceof DiceObject)
            removeDice((DiceObject) gameObject);
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

        TopographicMapObject terrain = new GameMapObject(sysUtilsWrapper, program, gameEntity);
        terrain.setGlCubeMapId(reflectionMap.getTextureId());
        terrain.setGlNormalMapId(normalMap.getTextureId());
        terrain.setGlDUDVMapId(dudvMap.getTextureId());
        terrain.loadObject();

        terrain.createRigidBody();
        dynamicsWorldObject.addRigidBody(terrain.get_body());

        glSceneObject.addObject(terrain, TERRAIN_MESH_OBJECT);

        if (gameInstanceEntity != null && gameEntity.getGamePoints() != null)
            placeChips(glSceneObject, program);

        DiceObject dice_1 = new DiceObject(sysUtilsWrapper, program);
        dice_1.loadObject();
        Matrix.setIdentityM(dice_1.getModelMatrix(), 0);
        Matrix.translateM(dice_1.getModelMatrix(), 0, -100f, 0.1f, 0);
        glSceneObject.addObject(dice_1, DICE_MESH_OBJECT_1);
    }

    public void placeChips(GLScene mScene, GLShaderProgram program) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        GameItemObject prevChip = null;
        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            PointF chipPlace = getChipPlace(mScene, playersOnWayPoints, player, true);

            GameItemObject chip = new ChipObject(sysUtilsWrapper, program, 0xFF000000 | player.getColor());
            if (prevChip == null)
                chip.loadObject();
            else {
                chip.loadFromObject(prevChip);
                prevChip = chip;
            }
            chip.setItemName(CHIP_MESH_OBJECT + "_" + String.format("%d", i));
            mScene.addObject(chip, chip.getItemName());

            chip.setInWorldPosition(chipPlace);
        }
    }

    public void moveChips(GLScene mScene) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            PointF chipPlace = getChipPlace(mScene, playersOnWayPoints, player, true);

            AbstractGL3DObject chip = mScene.getObject( CHIP_MESH_OBJECT + "_" + String.format("%d", i));

            chip.setInWorldPosition(chipPlace);
        }
    }

    public PointF getChipPlace(GLScene mScene, int[] playersOnWayPoints, InstancePlayer player, boolean rotate) {
        int currentPointIdx = player.getCurrentPoint();
        playersOnWayPoints[currentPointIdx]++;
        int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
        AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

        return getChipPlace(mScene, point, playersCnt, rotate);
    }

    public PointF getChipPlace(GLScene mScene, AbstractGamePoint point, int playersCnt, boolean rotate) {
        double toX2 = point.getxPos();
        double toZ2 = point.getyPos();

        if(rotate) {
            double angle = getChipRotationAngle(playersCnt);
            toX2 = point.getxPos() - 10 * Math.sin(angle);
            toZ2 = point.getyPos() - 10 * Math.cos(angle);
        }

        return ((TopographicMapObject) mScene.getObject(TERRAIN_MESH_OBJECT)).map2WorldCoord(new PointF((float)toX2, (float)toZ2));
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

    public void movingChipAnimation(ChipAnimadedDelegate delegate) {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];
        int movedPlayerIndex = -1;

        for (InstancePlayer player : gameInstanceEntity.getPlayers())
            playersOnWayPoints[player.getCurrentPoint()]++;

        AbstractGamePoint endGamePoint = null;
        int playersCnt = 0;

        if (savedPlayers != null)
            for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
                int currentPointIdx = gameInstanceEntity.getPlayers().get(i).getCurrentPoint();
                //playersOnWayPoints[currentPointIdx]++;

                if (savedPlayers.get(i).getCurrentPoint() != currentPointIdx) {
                    movedPlayerIndex = i;
                    endGamePoint = gameEntity.getGamePoints().get(currentPointIdx);
                    playersCnt = playersOnWayPoints[currentPointIdx] - 1;

                    break;
                }
            }

        if (movedPlayerIndex >= 0)
            try {
                animateChip(delegate, endGamePoint, playersCnt, glRenderer.getScene().getObject( CHIP_MESH_OBJECT + "_" + String.format("%d", movedPlayerIndex)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            startActionMooveGameInstance(getContext(), gameInstanceEntity);

        if (savedPlayers != null)
            savedPlayers.clear();
        savedPlayers = new ArrayList<>(gameInstanceEntity.getPlayers());
    }

    private void animateChip(final ChipAnimadedDelegate delegate, AbstractGamePoint endGamePoint, int playersCnt, AbstractGL3DObject chip) throws Exception {
        playersCnt = playersCnt < 0 ? 0 : playersCnt;

        if (endGamePoint == null)
            throw new Exception("Invalid game point.");

        PointF chipPlace = getChipPlace(glRenderer.getScene(), endGamePoint, playersCnt,
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
        move.startAnimation(chip, new GLAnimation.AnimationCallBack() {

            @Override
            public void onAnimationEnd() {
                if (delegate != null) delegate.onAnimationEnd();
            }
        });
    }

    public void saveMapImage(Intent data, GameMapEntity map){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        if (cursor != null){
            if(cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                RestApiService.startActionUploadMapImage(getContext(), map, picturePath);
            }

            cursor.close();
        }
    }

    public void setMapEntity(GameMapEntity mapEntity) {
        this.mapEntity = mapEntity;
    }
    public void setGameEntity(GameEntity gameEntity) {
        this.gameEntity = gameEntity;
    }
    public void setGameInstanceEntity(GameInstanceEntity gameInstanceEntity) {
        this.gameInstanceEntity = gameInstanceEntity;
    }
    public Bitmap getBitmap() {
        return cachedBitmap;
    }

    public void updateMap() {
        if (mapEntity == null || mapEntity.getId() == null)
            return;

        savedPlayers.clear();
        savedPlayers = new ArrayList<>(gameInstanceEntity.getPlayers());

        moveChips(glRenderer.getScene());
    }


}
