package com.cubegames.slava.cubegame.gl_render;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.cubegames.slava.cubegame.gl_render.scene.GLCamera;
import com.cubegames.slava.cubegame.gl_render.scene.GLLightSource;
import com.cubegames.slava.cubegame.gl_render.scene.GLScene;
import com.cubegames.slava.cubegame.gl_render.scene.objects.ColorShapeObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.DiceObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.LandObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.WaterObject;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;
import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static com.cubegames.slava.cubegame.Utils.forceGC_and_Sync;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.CHIP_MESH_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.CHIP_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TERRAIN_MESH_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.WATER_MESH_OBJECT;

public class MapGLRenderer implements GLSurfaceView.Renderer {

    private final static float LIGHT_X = -2.2F;
    private final static float LIGHT_Y = 1.7F;
    private final static float LIGHT_Z = -3.2F;

    private final static float CAMERA_X = 0;
    private final static float CAMERA_Y = 2;
    private final static float CAMERA_Z = -4;

    private final static float CAMERA_LOOK_X = 0;
    private final static float CAMERA_LOOK_Y = 0;
    private final static float CAMERA_LOOK_Z = 0;

    private final static float CAMERA_UP_X = 0;
    private final static float CAMERA_UP_Y = 1;
    private final static float CAMERA_UP_Z = 0;

    private Context context;

    private String mapID;
    private Game gameEntity = null;
    private GameInstance gameInstanceEntity = null;

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private static final float[] LIGHT_POS_IN_MODEL_SPACE = new float[] {LIGHT_X, LIGHT_Y, LIGHT_Z, 1.0f};

    private GLScene mScene;

    public MapGLRenderer(Context context) {
        this.context = context;
    }

    public GLScene getmScene() {
        return mScene;
    }

    public void setMapID(String mapID) {
        this.mapID = mapID;
    }
    public void setGameEntity(Game gameEntity) {
        this.gameEntity = gameEntity;
    }
    public void setGameInstanceEntity(GameInstance gameInstanceEntity) {
        this.gameInstanceEntity = gameInstanceEntity;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initGLRender();
        initScene();
        loadScene();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        mScene.getCamera().setProjectionMatrix(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawScene();
    }

    private void initGLRender() {
        glClearColor(0f, 0.7f, 1f, 1f);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    private void initScene() {
        mScene = new GLScene(context);

        mScene.setCamera(new GLCamera(CAMERA_X, CAMERA_Y, CAMERA_Z,
                                      CAMERA_LOOK_X, CAMERA_LOOK_Y, CAMERA_LOOK_Z,
                                      CAMERA_UP_X, CAMERA_UP_Y, CAMERA_UP_Z));

        mScene.setLightSource(new GLLightSource(LIGHT_POS_IN_MODEL_SPACE, mScene.getCamera()));
    }

    private void loadScene() {
        GLSceneObject water = new WaterObject(context, mScene.getCachedShader(WATER_OBJECT));
        water.loadObject();
        mScene.addObject(water, WATER_MESH_OBJECT);

        GLSceneObject terrain = new LandObject(context, mScene.getCachedShader(TERRAIN_OBJECT), gameEntity);
        terrain.loadObject();
        mScene.addObject(terrain, TERRAIN_MESH_OBJECT);

        if (gameInstanceEntity != null && gameEntity.getGamePoints() != null)
            placeChips();

        GLSceneObject dice_1 = new DiceObject(context, mScene.getCachedShader(CHIP_OBJECT));
        dice_1.loadObject();
        Matrix.setIdentityM(dice_1.getModelMatrix(), 0);
        Matrix.scaleM(dice_1.getModelMatrix(), 0, 0.1f, 0.1f, 0.1f);
        Matrix.rotateM(dice_1.getModelMatrix(), 0, -45, 0, 1, 0);
        Matrix.translateM(dice_1.getModelMatrix(), 0, 0, 5f, 0);
        GLAnimation animation = new GLAnimation(GLRenderConsts.GLAnimationType.TRANSLATE_ANIMATION,
                0, 0,
                5f, 1f,
                0, 0,
                500
        );
        animation.setBaseMatrix(Arrays.copyOf(dice_1.getModelMatrix(), 16));
        dice_1.setAnimation(animation);
        mScene.addObject(dice_1, DICE_MESH_OBJECT_1);

        forceGC_and_Sync();

        animation.startAnimation();
    }

    public void placeChips() {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            int currentPointIdx = player.getCurrentPoint();
            playersOnWayPoints[currentPointIdx]++;
            int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
            AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

            GLSceneObject chip = new ColorShapeObject(context, mScene.getCachedShader(CHIP_OBJECT), 0xFF000000 | player.getColor());
            chip.loadObject();

            PointF chipPlace = getChipPlace(point, playersCnt, true);
            Matrix.setIdentityM(chip.getModelMatrix(), 0);
            Matrix.translateM(chip.getModelMatrix(), 0, chipPlace.x, -0.1f, chipPlace.y);

            mScene.addObject(chip, CHIP_MESH_OBJECT + "_" + String.format("%d", i));
        }
    }

    public void moveChips() {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            int currentPointIdx = player.getCurrentPoint();
            playersOnWayPoints[currentPointIdx]++;
            int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
            AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

            GLSceneObject chip = getmScene().getObject( CHIP_MESH_OBJECT + "_" + String.format("%d", i));

            PointF chipPlace = getChipPlace(point, playersCnt, true);
            Matrix.setIdentityM(chip.getModelMatrix(), 0);
            Matrix.translateM(chip.getModelMatrix(), 0, chipPlace.x, -0.1f, chipPlace.y);
        }
    }

    public PointF getChipPlace(AbstractGamePoint endGamePoint, int playersCnt, boolean rotate) {
        double toX2 = endGamePoint.getxPos();
        double toZ2 = endGamePoint.getyPos();

        if(rotate) {
            double angle = getChipRotationAngle(playersCnt);
            toX2 = endGamePoint.getxPos() - 10 * Math.sin(angle);
            toZ2 = endGamePoint.getyPos() - 10 * Math.cos(angle);
        }

        LandObject land = (LandObject) mScene.getObject(TERRAIN_MESH_OBJECT);
        return land.tex2WorldCoord(new PointF((float)toX2, (float)toZ2));
    }

    private double getChipRotationAngle(int playersCnt) {
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

    private void drawScene() {
        mScene.drawScene();
    }

}
