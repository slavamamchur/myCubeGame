package com.sadgames.gl3d_engine.gl_render.scene;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import com.sadgames.gl3d_engine.GameEventsCallbackInterface;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.GLES20APIWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.GLRendererInterface;
import com.sadgames.gl3d_engine.gl_render.scene.fbo.AbstractFBO;
import com.sadgames.gl3d_engine.gl_render.scene.fbo.ColorBufferFBO;
import com.sadgames.gl3d_engine.gl_render.scene.fbo.DepthBufferFBO;
import com.sadgames.gl3d_engine.gl_render.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.GUI2DImageObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.PNodeObject;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GUIRendererProgram;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.ShadowMapProgram;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.ShapeShaderProgram;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.TerrainRendererProgram;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.FBO_TEXTURE_SLOT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.SHADOWMAP_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GraphicsQuality;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.OES_DEPTH_TEXTURE_EXTENSION;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.SHADOW_MAP_RESOLUTION_SCALE;
import static com.sadgames.gl3d_engine.gl_render.scene.objects.PNodeObject.MOVING_OBJECT;
import static com.sadgames.sysutils.common.CommonUtils.forceGC_and_Sync;

public class GLScene implements GLRendererInterface {

    private final static float    DEFAULT_LIGHT_X        = -2.20F;
    private final static float    DEFAULT_LIGHT_Y        =  1.70F;
    private final static float    DEFAULT_LIGHT_Z        = -3.20F;

    private final static Vector3f DEFAULT_LIGHT_COLOUR   = new Vector3f(1.0f, 1.0f, 0.8f);
    private static final Vector3f DEFAULT_GRAVITY_VECTOR = new Vector3f(0f, -9.8f, 0f);

    private final static float    DEFAULT_CAMERA_X       = 0f;
    private final static float    DEFAULT_CAMERA_Y       = 4f;
    private final static float    DEFAULT_CAMERA_Z       = 4f;
    private final static float    DEFAULT_CAMERA_PITCH   = 45.0f;
    private final static float    DEFAULT_CAMERA_YAW     = 0.0f;
    private final static float    DEFAULT_CAMERA_ROLL    = 0.0f;

    public  final static Object   lockObject             = new Object();
    private final static float    WAVE_SPEED             = 0.04f;
    public  final static long     CAMERA_ZOOM_ANIMATION_DURATION = 1000;
    public  final static float    SIMULATION_FRAMES_IN_SEC = 60f;
    /** FPS */

    private GLCamera camera = null;
    private GLLightSource lightSource = null;
    private long simulation_time = 0;
    private boolean isSimulating = false;
    private DiscreteDynamicsWorld physicalWorldObject = null;
    private boolean hasDepthTextureExtension;
    private int oldNumContacts = 0;
    private long old_time = System.currentTimeMillis();
    private long old_frame_time = 0;
    private float moveFactor = 0;
    private long frameTime = 0;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private boolean isRenderStopped = false;

    private Map<String, AbstractGL3DObject> objects = new HashMap<>();
    private Map<GLObjectType, GLShaderProgram> shaders = new HashMap<>();

    private AbstractFBO shadowMapFBO = null;
    private AbstractFBO mainRenderFBO = null;
    private GUI2DImageObject postEffects2DScreen = null;
    private GLAnimation zoomCameraAnimation = null;
    private SysUtilsWrapperInterface sysUtilsWrapper;
    private GameEventsCallbackInterface gameEventsCallBack = null;
    private GLES20APIWrapperInterface glES20Wrapper;
    private GraphicsQuality graphicsQualityLevel;

    public GLScene(SysUtilsWrapperInterface sysUtilsWrapper, GameEventsCallbackInterface gameEventsCallBack) {
        glES20Wrapper = sysUtilsWrapper.iGetGLES20WrapperInterface();

        this.sysUtilsWrapper = sysUtilsWrapper;
        this.gameEventsCallBack = gameEventsCallBack;
        this.hasDepthTextureExtension = checkDepthTextureExtension();
        this.graphicsQualityLevel = sysUtilsWrapper.iGetSettingsManager().getGraphicsQualityLevel();

        glES20Wrapper.glEnableFacesCulling();
        glES20Wrapper.glEnableDepthTest();
    }

    private boolean checkDepthTextureExtension() {
        return glES20Wrapper.glExtensions().contains(OES_DEPTH_TEXTURE_EXTENSION);
    }

    public GLCamera getCamera() {
        return camera;
    }
    public void setCamera(GLCamera camera) {
        this.camera = camera;
    }
    public GLLightSource getLightSource() {
        return lightSource;
    }
    public void setLightSource(GLLightSource lightSource) {
        this.lightSource = lightSource;
    }
    public boolean hasDepthTextureExtension() {
        return hasDepthTextureExtension;
    }
    public int getmDisplayWidth() {
        return mDisplayWidth;
    }
    public void setmDisplayWidth(int mDisplayWidth) {
        this.mDisplayWidth = mDisplayWidth;
    }
    public int getmDisplayHeight() {
        return mDisplayHeight;
    }
    public void setmDisplayHeight(int mDisplayHeight) {
        this.mDisplayHeight = mDisplayHeight;
    }
    public AbstractFBO getShadowMapFBO() {
        return shadowMapFBO;
    }
    public GUI2DImageObject getPostEffects2DScreen() {
        return postEffects2DScreen;
    }
    public void setPostEffects2DScreen(GUI2DImageObject postEffects2DScreen) {
        this.postEffects2DScreen = postEffects2DScreen;
    }
    public GLAnimation getZoomCameraAnimation() {
        return zoomCameraAnimation;
    }
    public void setZoomCameraAnimation(GLAnimation zoomCameraAnimation) {
        this.zoomCameraAnimation = zoomCameraAnimation;
    }
    public long getFrameTime() {
        return frameTime;
    }
    public void setSimulation_time(long simulation_time) {
        this.simulation_time = simulation_time;
    }
    public void setGameEventsCallBack(GameEventsCallbackInterface gameEventsCallBack) {
        this.gameEventsCallBack = gameEventsCallBack;
    }
    public void setRenderStopped(boolean renderStopped) {
        isRenderStopped = renderStopped;
    }
    public void addObject(AbstractGL3DObject object, String name) {
        objects.put(name, object);
    }
    public void deleteObject(String name) {
        objects.remove(name);
    }

    private void clearData() {
        clearFBOs();
        clearObjectsCache();
        clearShaderCache();
    }

    private void clearFBOs() {
        if (shadowMapFBO != null)
            shadowMapFBO.cleanUp();

        if (mainRenderFBO != null)
            mainRenderFBO.cleanUp();
    }

    private void clearObjectsCache() {
        for (AbstractGL3DObject object : objects.values())
            object.clearData();

        objects.clear();

        if (postEffects2DScreen != null)
            postEffects2DScreen.clearData();
    }

    public void clearShaderCache() {
        glES20Wrapper.glUseProgram(0);

        for (GLShaderProgram program : shaders.values())
            program.deleteProgram();

        shaders.clear();
    }

    public AbstractGL3DObject getObject(String name) {
        return objects.get(name);
    }

    public GLShaderProgram getCachedShader(GLObjectType type) {
        GLShaderProgram program = shaders.get(type);

        if (program == null) {
            switch (type) {
                case TERRAIN_OBJECT:
                    program = new TerrainRendererProgram(sysUtilsWrapper);
                    break;
                case SHADOWMAP_OBJECT:
                    program = new ShadowMapProgram(sysUtilsWrapper);
                    break;
                case GUI_OBJECT:
                    program = new GUIRendererProgram(sysUtilsWrapper);
                    break;
                default:
                    program = new ShapeShaderProgram(sysUtilsWrapper);
            }

            shaders.put(type, program);
        }

        return program;
    }

    public void startSimulation() {
        simulation_time = System.currentTimeMillis();
        isSimulating = true;
    }

    public void stopSimulation() {
        isSimulating = false;
    }

    public void generateShadowMapFBO() {
       Color4f clColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
       float shadowMapResolutionScaleFactor = SHADOW_MAP_RESOLUTION_SCALE[graphicsQualityLevel.ordinal()];
       int shadowMapWidth = Math.round(mDisplayWidth * shadowMapResolutionScaleFactor);
       int shadowMapHeight = Math.round(mDisplayHeight * shadowMapResolutionScaleFactor);

       getLightSource().updateViewProjectionMatrix(shadowMapWidth, shadowMapHeight);
       shadowMapFBO = hasDepthTextureExtension ?
               new DepthBufferFBO(glES20Wrapper, shadowMapWidth, shadowMapHeight, clColor) :
               new ColorBufferFBO(glES20Wrapper, shadowMapWidth, shadowMapHeight, clColor);
    }

    public void generateMainRenderFBO() {
        mainRenderFBO =
                new ColorBufferFBO(glES20Wrapper, Math.round(mDisplayWidth), Math.round(mDisplayHeight), new Color4f(0.0f, 0.0f, 0.0f, 0.0f));
    }

    private void calculateSceneTransformations() {

        if (zoomCameraAnimation != null && zoomCameraAnimation.isInProgress()) {
            zoomCameraAnimation.animate(camera);
        }

        try {
            moveFactor += WAVE_SPEED * frameTime / 1000;
            moveFactor %= 1;
        }
        catch (Exception e) {
            moveFactor = 0;
        }

        for (AbstractGL3DObject object : objects.values()) {

            GLAnimation animation = object.getAnimation();
            if (animation != null && animation.isInProgress()) {
                animation.animate(object);
            }
            else if (isSimulating && object instanceof PNodeObject && (((PNodeObject) object).getTag() == MOVING_OBJECT)
                    && ((PNodeObject) object).get_body() != null) {

                PNodeObject movingObject = (PNodeObject) object;
                Transform transform = movingObject.getWorldTransformActual();

                if (!movingObject.getWorldTransformOld().equals(transform))
                    movingObject.setWorldTransformMatrix(transform);

                else {
                    physicalWorldObject.removeRigidBody(movingObject.get_body());
                    movingObject.set_body(null);

                    if (gameEventsCallBack != null)
                        gameEventsCallBack.onStopMovingObject(movingObject);
                }
            }
        }
    }

    public void drawScene() {
        if (isRenderStopped)
            return;

        clearRenderBuffers();

        long currentTime = System.currentTimeMillis();
        calculateFrameTime(currentTime);

        simulatePhysics(currentTime);
        calculateSceneTransformations();

        renderShadowMapBuffer();
        renderColorBuffer();
        /** for post effects image processing */
        ///renderPostEffectsBuffer();
    }

    private void renderShadowMapBuffer() {
        shadowMapFBO.bind();
        glES20Wrapper.glCullFrontFace();

        GLShaderProgram program = getCachedShader(SHADOWMAP_OBJECT);
        program.useProgram();

        /** for shadowbox implementation
        lightSource.updateViewProjectionMatrix(); */

        int prevObject = -1;
        for (AbstractGL3DObject object : objects.values()) {
            if (!object.isCastShadow())
                continue;

            program.bindMVPMatrix(object, getLightSource().getViewMatrix(), getLightSource().getProjectionMatrix());

            if (object.getVertexVBO().getVboPtr() != prevObject) {
                object.prepare(program);
                prevObject = object.getVertexVBO().getVboPtr();
            }
            object.render();
        }

        shadowMapFBO.unbind();
    }

    private void renderPostEffectsBuffer() {
        glES20Wrapper.glBindFramebuffer(0);
        glES20Wrapper.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        GLShaderProgram program = postEffects2DScreen.getProgram();
        program.useProgram();

        postEffects2DScreen.setGlTexture(mainRenderFBO.getFboTexture());
        program.setMaterialParams(postEffects2DScreen);

        postEffects2DScreen.prepare();
        postEffects2DScreen.render();
    }

    private void renderColorBuffer() {
        /** for post effects image processing */
        ///mainRenderFBO.bind();
        glES20Wrapper.glCullBackFace();
        glES20Wrapper.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        TerrainRendererProgram program = (TerrainRendererProgram) getCachedShader(TERRAIN_OBJECT);
        program.useProgram();

        glES20Wrapper.glActiveTexture(FBO_TEXTURE_SLOT);
        glES20Wrapper.glBindTexture2D(shadowMapFBO.getFboTexture().getTextureId());
        program.paramByName(ACTIVE_SHADOWMAP_SLOT_PARAM_NAME).setParamValue(4);

        synchronized (lockObject) {
            program.setCameraPosition(getCamera().getCameraPosition());
        }

        program.setLightSourcePosition(getLightSource().getLightPosInEyeSpace());
        program.setLightColourValue(getLightSource().getLightColour());

        program.setWaveMovingFactor(GraphicsQuality.LOW.equals(graphicsQualityLevel) ? -1f : moveFactor);

        /** for rgb depth buffers */
        ///program.paramByName(UX_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / mShadowMapWidth));
        ///program.paramByName(UY_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / mShadowMapHeight));

        int prevObject = -1;
        for (AbstractGL3DObject object : objects.values()) {
            synchronized (lockObject) {
                program.bindMVPMatrix(object, getCamera().getViewMatrix(), getCamera().getProjectionMatrix());
            }

            program.bindLightSourceMVP(object, getLightSource().getViewMatrix(), getLightSource().getProjectionMatrix(), hasDepthTextureExtension);

            program.setMaterialParams(object);

            if (object.getVertexVBO().getVboPtr() != prevObject) {
                object.prepare(program);
                prevObject = object.getVertexVBO().getVboPtr();
            }

            object.render();
        }

        /** for post effects image processing */
        ///mainRenderFBO.unbind();

        /** for shadowmap debugging */
        GLShaderProgram gui_program = postEffects2DScreen.getProgram();
        gui_program.useProgram();

        postEffects2DScreen.setGlTexture(shadowMapFBO.getFboTexture());
        gui_program.setMaterialParams(postEffects2DScreen);

        postEffects2DScreen.prepare();
        postEffects2DScreen.render();
    }

    private void simulatePhysics(long currentTime) {
        if (isSimulating && physicalWorldObject != null) {

            float realInterval = frameTime / 1000f;
            float discreteInterval = 1 / 60f;
            for (float i = 0; i < (realInterval / discreteInterval); i++)
                physicalWorldObject.stepSimulation(discreteInterval);

            ///physicalWorldObject.stepSimulation((currentTime - simulation_time) / 1000f, 4, 1f / SIMULATION_FRAMES_IN_SEC );
            ///simulation_time = currentTime;

            for (int i = 0; i < physicalWorldObject.getDispatcher().getNumManifolds(); i++)
                if (   physicalWorldObject.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() > 0
                    && physicalWorldObject.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() != oldNumContacts
                    ) {

                    if (gameEventsCallBack != null)
                        gameEventsCallBack.onRollingObjectStart(null); //TODO: Get Object ref from Collision example

                    oldNumContacts = physicalWorldObject.getDispatcher().getManifoldByIndexInternal(i).getNumContacts();
                    old_time = currentTime;
                }
                else if ((physicalWorldObject.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() == 0))
                    if (gameEventsCallBack != null)
                        gameEventsCallBack.onRollingObjectStop(null); //TODO: Get Object ref from Collision example
                else if (currentTime - old_time > 150)
                        if (gameEventsCallBack != null)
                            gameEventsCallBack.onRollingObjectStop(null); //TODO: Get Object ref from Collision example

        }

        /** Collision example --------------------------------------------
         /*const btCollisionObject* obA = contactManifold->getBody0();
         const btCollisionObject* obB = contactManifold->getBody1();

         //6
         PNodeObject* pnA = (__bridge PNodeObject*)obA->getUserPointer();
         PNodeObject* pnB = (__bridge PNodeObject*)obB->getUserPointer();*/
    }

    private void calculateFrameTime(long currentTime) {
        frameTime = old_frame_time == 0 ? 0 : currentTime - old_frame_time;
        old_frame_time = currentTime;
    }

    private void clearRenderBuffers() {
        glES20Wrapper.glBindFramebuffer(0);
        glES20Wrapper.glSetClearColor(0f, 0.0f, 0f, 0f);
        glES20Wrapper.glClear();
    }

    public void cleanUp() {
        stopSimulation();
        clearData();
    }

    private void initScene() {
        GLCamera camera = new GLCamera(DEFAULT_CAMERA_X, DEFAULT_CAMERA_Y, DEFAULT_CAMERA_Z, DEFAULT_CAMERA_PITCH, DEFAULT_CAMERA_YAW, DEFAULT_CAMERA_ROLL);
        if (gameEventsCallBack != null)
            gameEventsCallBack.onInitGLCamera(camera);
        setCamera(camera);

        GLLightSource lightSource = new GLLightSource(new float[] {DEFAULT_LIGHT_X, DEFAULT_LIGHT_Y, DEFAULT_LIGHT_Z, 1.0f}, DEFAULT_LIGHT_COLOUR, getCamera());
        if (gameEventsCallBack != null)
            gameEventsCallBack.onInitLightSource(lightSource);
        setLightSource(lightSource);
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
        getCachedShader(SHADOWMAP_OBJECT);

        /** for postprocessing effects */
        createPostEffects2DScreen();

        if (gameEventsCallBack != null)
            gameEventsCallBack.onLoadSceneObjects(this, physicalWorldObject);

        forceGC_and_Sync();
    }

    private void createPostEffects2DScreen() {
        GLShaderProgram guiShader = getCachedShader(GUI_OBJECT);
        setPostEffects2DScreen(new GUI2DImageObject(sysUtilsWrapper, guiShader, new Vector4f(-1, 1, 0, 0)));
        getPostEffects2DScreen().loadObject();
    }

    @Override
    public GLScene getSceneObject() {
        return this;
    }

    @Override
    public DiscreteDynamicsWorld getPhysicalWorldObject() {
        return physicalWorldObject;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initScene();
        initPhysics();

        loadScene();

        startSimulation();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setmDisplayWidth(width);
        setmDisplayHeight(height);
        getCamera().setAspectRatio(width, height);

        generateShadowMapFBO();
        /** for post effects
         ///generateMainRenderFBO(); */
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawScene();
    }
}
