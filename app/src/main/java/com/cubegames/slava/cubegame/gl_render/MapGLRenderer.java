package com.cubegames.slava.cubegame.gl_render;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.cubegames.slava.cubegame.gl_render.scene.GLCamera;
import com.cubegames.slava.cubegame.gl_render.scene.GLLightSource;
import com.cubegames.slava.cubegame.gl_render.scene.GLScene;
import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.LandObject;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static com.cubegames.slava.cubegame.Utils.forceGC_and_Sync;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LAND_INTERPOLATOR_DIM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TERRAIN_MESH_OBJECT;

public class MapGLRenderer implements GLSurfaceView.Renderer {

    private final static float LIGHT_X = -2.2F;
    private final static float LIGHT_Y = 2.2F;
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

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private static final float[] LIGHT_POS_IN_MODEL_SPACE = new float[] {LIGHT_X, LIGHT_Y, LIGHT_Z, 1.0f};

    private GLScene mScene;

    public MapGLRenderer(Context context) {
        this.context = context;
    }

    public void setMapID(String mapID) {
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
        mScene = new GLScene(context);

        mScene.setCamera(new GLCamera(CAMERA_X, CAMERA_Y, CAMERA_Z,
                                      CAMERA_LOOK_X, CAMERA_LOOK_Y, CAMERA_LOOK_Z,
                                      CAMERA_UP_X, CAMERA_UP_Y, CAMERA_UP_Z));

        mScene.setLightSource(new GLLightSource(LIGHT_POS_IN_MODEL_SPACE, mScene.getCamera()));
    }

    private void loadScene() {
        //TODO: use LOD -> 125x125 (half sized textures), 250x250 (full sized)
        GLSceneObject terrain = new LandObject(context, LAND_INTERPOLATOR_DIM, mScene.getCachedShader(TERRAIN_OBJECT), mapID);
        terrain.loadObject();

        mScene.addObject(terrain, TERRAIN_MESH_OBJECT);

        forceGC_and_Sync();
    }

    private void drawScene() {
        mScene.drawScene();
    }

}
