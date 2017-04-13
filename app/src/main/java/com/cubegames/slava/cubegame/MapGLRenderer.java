package com.cubegames.slava.cubegame;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.cubegames.slava.cubegame.mapgl.GLCamera;
import com.cubegames.slava.cubegame.mapgl.GLLightSource;
import com.cubegames.slava.cubegame.mapgl.GLScene;
import com.cubegames.slava.cubegame.mapgl.GLSceneObject;
import com.cubegames.slava.cubegame.mapgl.TerrainObject;
import com.cubegames.slava.cubegame.mapgl.TerrainShader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.LAND_INTERPOLATOR_DIM;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.TERRAIN_MESH_OBJECT;

class MapGLRenderer implements GLSurfaceView.Renderer {

    private final static float LIGHT_X = -1.2F;
    private final static float LIGHT_Y = 2.2F;
    private final static float LIGHT_Z = -1.2F;

    private final static float CAMERA_X = 0;
    private final static float CAMERA_Y = 2;
    private final static float CAMERA_Z = -3.5F;

    private final static float CAMERA_LOOK_X = 0;
    private final static float CAMERA_LOOK_Y = 0;
    private final static float CAMERA_LOOK_Z = 0.5F;

    private final static float CAMERA_UP_X = 0;
    private final static float CAMERA_UP_Y = 1;
    private final static float CAMERA_UP_Z = 0;

    private Context context;

    private String mapID;

    private TerrainShader mainShader;

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private static final float[] LIGHT_POS_IN_MODEL_SPACE = new float[] {LIGHT_X, LIGHT_Y, LIGHT_Z, 1.0f};

    private GLScene mScene;

    MapGLRenderer(Context context) {
        this.context = context;
    }

    void setMapID(String mapID) {
        this.mapID = mapID;
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
        mainShader = new TerrainShader(context);
        mScene = new GLScene(context);

        mScene.setCamera(new GLCamera(CAMERA_X, CAMERA_Y, CAMERA_Z,
                                      CAMERA_LOOK_X, CAMERA_LOOK_Y, CAMERA_LOOK_Z,
                                      CAMERA_UP_X, CAMERA_UP_Y, CAMERA_UP_Z));

        mScene.setLightSource(new GLLightSource(LIGHT_POS_IN_MODEL_SPACE, mScene.getCamera()));
    }

    private void loadScene() {
        GLSceneObject terrain = new TerrainObject(context, LAND_INTERPOLATOR_DIM, mainShader, mapID);
        terrain.loadObject();

        mScene.addObject(terrain, TERRAIN_MESH_OBJECT);

        System.gc();
        try {Thread.sleep(3000);} catch (InterruptedException e) {}
    }

    private void drawScene() {
        mScene.drawScene();
    }

}
