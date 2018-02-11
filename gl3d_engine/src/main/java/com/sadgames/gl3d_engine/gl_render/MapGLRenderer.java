package com.sadgames.gl3d_engine.gl_render;

import android.graphics.RectF;
import android.opengl.GLSurfaceView;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.sadgames.gl3d_engine.gl_render.scene.GLCamera;
import com.sadgames.gl3d_engine.gl_render.scene.GLLightSource;
import com.sadgames.gl3d_engine.gl_render.scene.GLScene;
import com.sadgames.gl3d_engine.gl_render.scene.objects.onScreen2DBox;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.ISysUtilsWrapper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glEnable;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.SHADOWMAP_OBJECT;
import static com.sadgames.sysutils.JavaPlatformUtils.forceGC_and_Sync;

public class MapGLRenderer implements GLSurfaceView.Renderer {

    private final static float    DEFAULT_LIGHT_X        = -2.20F;
    private final static float    DEFAULT_LIGHT_Y        =  1.70F;
    private final static float    DEFAULT_LIGHT_Z        = -3.20F;

    private final static Vector3f DEFAULT_LIGHT_COLOUR   = new Vector3f(1.0f, 1.0f, 0.6f);
    private static final Vector3f DEFAULT_GRAVITY_VECTOR = new Vector3f(0f, -9.8f, 0f);

    private final static float    DEFAULT_CAMERA_X       = 0f;
    private final static float    DEFAULT_CAMERA_Y       = 4f;
    private final static float    DEFAULT_CAMERA_Z       = 4f;
    private final static float    DEFAULT_CAMERA_PITCH   = 45.0f;
    private final static float    DEFAULT_CAMERA_YAW     = 0.0f;
    private final static float    DEFAULT_CAMERA_ROLL    = 0.0f;

    private String mapID = null; /** for map editor */
    private DiscreteDynamicsWorld physicalWorldObject;
    private GLScene mScene;
    private ISysUtilsWrapper sysUtilsWrapper;
    private IGameEventsCallBack gameEventsCallBack = null;

    public MapGLRenderer(ISysUtilsWrapper sysUtilsWrapper) {
        this.sysUtilsWrapper = sysUtilsWrapper;
    }
    public GLScene getScene() {
        return mScene;
    }
    public void setMapID(String mapID) {
        this.mapID = mapID;
    }
    public DiscreteDynamicsWorld getPhysicalWorldObject() {
        return physicalWorldObject;
    }
    public void setGameEventsCallBack(IGameEventsCallBack gameEventsCallBack) {
        this.gameEventsCallBack = gameEventsCallBack;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initGLRender();
        initScene();
        initPhysics();

        loadScene();

        mScene.setPhysicalWorld(physicalWorldObject);
        mScene.startSimulation();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mScene.setmDisplayWidth(width);
        mScene.setmDisplayHeight(height);
        mScene.getCamera().setAspectRatio(width, height);

        mScene.generateShadowMapFBO();
        /** for post effects
        ///mScene.generateMainRenderFBO(); */
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawScene();
    }

    private void initGLRender() {
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    private void initScene() {
        mScene = new GLScene(sysUtilsWrapper);
        mScene.setGameEventsCallBack(gameEventsCallBack);

        GLCamera camera = new GLCamera(DEFAULT_CAMERA_X, DEFAULT_CAMERA_Y, DEFAULT_CAMERA_Z, DEFAULT_CAMERA_PITCH, DEFAULT_CAMERA_YAW, DEFAULT_CAMERA_ROLL);
        if (gameEventsCallBack != null)
            gameEventsCallBack.onInitGLCamera(camera);
        mScene.setCamera(camera);

        GLLightSource lightSource = new GLLightSource(new float[] {DEFAULT_LIGHT_X, DEFAULT_LIGHT_Y, DEFAULT_LIGHT_Z, 1.0f}, DEFAULT_LIGHT_COLOUR, mScene.getCamera());
        if (gameEventsCallBack != null)
            gameEventsCallBack.onInitLightSource(lightSource);
        mScene.setLightSource(lightSource);
    }

    private void initPhysics() {
        BroadphaseInterface _broadphase = new DbvtBroadphase();

        DefaultCollisionConfiguration _collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher _dispatcher = new CollisionDispatcher(_collisionConfiguration);

        SequentialImpulseConstraintSolver _solver = new SequentialImpulseConstraintSolver();

        physicalWorldObject = new DiscreteDynamicsWorld(_dispatcher, _broadphase, _solver, _collisionConfiguration);
        physicalWorldObject.setGravity(DEFAULT_GRAVITY_VECTOR);

        if (gameEventsCallBack != null)
            gameEventsCallBack.onInitPhysics(physicalWorldObject);
    }

    private void loadScene() {
        /** Create internal shaders before loading scene  */
        mScene.getCachedShader(SHADOWMAP_OBJECT);

        /** for postprocessing effects */
        createPostEffects2DScreen();

        if (gameEventsCallBack != null)
            gameEventsCallBack.onLoadSceneObjects(mScene, physicalWorldObject);

        forceGC_and_Sync();
    }

    private void createPostEffects2DScreen() {
        GLShaderProgram guiShader = mScene.getCachedShader(GUI_OBJECT);
        mScene.setPostEffects2DScreen(new onScreen2DBox(sysUtilsWrapper, guiShader, new RectF(-1, 1, 0, 0)));
        mScene.getPostEffects2DScreen().loadObject();
    }

    private void drawScene() {
        mScene.drawScene();
    }

}
