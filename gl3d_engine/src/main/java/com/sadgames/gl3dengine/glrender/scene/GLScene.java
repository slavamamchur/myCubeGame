package com.sadgames.gl3dengine.glrender.scene;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import com.sadgames.gl3dengine.GameEventsCallbackInterface;
import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.GLRendererInterface;
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;
import com.sadgames.gl3dengine.glrender.scene.camera.FixedIsometricCamera;
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;
import com.sadgames.gl3dengine.glrender.scene.fbo.AbstractFBO;
import com.sadgames.gl3dengine.glrender.scene.fbo.ColorBufferFBO;
import com.sadgames.gl3dengine.glrender.scene.fbo.DepthBufferFBO;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.GUI2DImageObject;
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.GUIRendererProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.ShadowMapProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.ShapeShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.SkyBoxProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.TerrainRendererProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Color4f;
import javax.vecmath.Vector4f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_PITCH;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_ROLL;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_X;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_Y;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_YAW;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_Z;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_GRAVITY_VECTOR;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_LIGHT_COLOUR;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_LIGHT_X;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_LIGHT_Y;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_LIGHT_Z;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.SHADOW_MAP_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GraphicsQuality;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.OES_DEPTH_TEXTURE_EXTENSION;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOW_MAP_RESOLUTION_SCALE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.WAVE_SPEED;
import static com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject.MOVING_OBJECT;
import static com.sadgames.sysutils.common.CommonUtils.forceGCandWait;

public class GLScene implements GLRendererInterface {

    public  final static Object lockObject = new Object();

    private boolean isSimulating = false;
    private boolean hasDepthTextureExtension;
    private int oldNumContacts = 0;
    private long old_time = System.currentTimeMillis();
    private long old_frame_time = 0;
    private float moveFactor = 0;
    private long frameTime = 0;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private boolean isRenderStopped = false;
    private GLCamera camera = null;
    private GLLightSource lightSource = null;
    private DiscreteDynamicsWorld physicalWorld = null;
    private Map<String, AbstractGL3DObject> objects = new HashMap<>();
    private Map<GLObjectType, GLShaderProgram> shaders = new HashMap<>();
    private AbstractFBO shadowMapFBO = null;
    private AbstractFBO mainRenderFBO = null;
    private GUI2DImageObject postEffects2DScreen = null;
    private GLAnimation zoomCameraAnimation = null;
    private SysUtilsWrapperInterface sysUtilsWrapper;
    private GameEventsCallbackInterface gameEventsCallBackListener = null;
    private GraphicsQuality graphicsQualityLevel;

    public GLScene(SysUtilsWrapperInterface sysUtilsWrapper) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        this.graphicsQualityLevel = sysUtilsWrapper.iGetSettingsManager().getGraphicsQualityLevel();
    }

    public GLCamera getCamera() {
        return camera;
    }
    public GLLightSource getLightSource() {
        return lightSource;
    }
    @SuppressWarnings("unused") public boolean hasDepthTextureExtension() {
        return hasDepthTextureExtension;
    }
    @SuppressWarnings("unused") public int getmDisplayWidth() {
        return mDisplayWidth;
    }
    @SuppressWarnings("unused") public int getmDisplayHeight() {
        return mDisplayHeight;
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
    @SuppressWarnings("unused") public void setRenderStopped(boolean renderStopped) {
        isRenderStopped = renderStopped;
    }
    public AbstractFBO getShadowMapFBO() {
        return shadowMapFBO;
    }
    public float getMoveFactor() {
        return moveFactor;
    }

    public void addObject(AbstractGL3DObject object, String name) {
        objects.put(name, object);
    }
    @SuppressWarnings("unused") public void deleteObject(String name) {
        objects.remove(name);
    }

    @SuppressWarnings("all")
    public boolean checkDepthTextureExtension() {
        return GLES20JniWrapper.glExtensions().contains(OES_DEPTH_TEXTURE_EXTENSION);
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

    private void clearShaderCache() {
        GLES20JniWrapper.glUseProgram(0);

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
                case SHADOW_MAP_OBJECT:
                    program = new ShadowMapProgram(sysUtilsWrapper);
                    break;
                case GUI_OBJECT:
                    program = new GUIRendererProgram(sysUtilsWrapper);
                    break;
                case SKY_BOX_OBJECT:
                    program = new SkyBoxProgram(sysUtilsWrapper);
                    break;
                default:
                    program = new ShapeShaderProgram(sysUtilsWrapper);
            }

            shaders.put(type, program);
        }

        return program;
    }

    private void startSimulation() {
        isSimulating = true;
    }

    private void stopSimulation() {
        isSimulating = false;
    }

    private void generateShadowMapFBO() {
       Color4f clColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
       float shadowMapResolutionScaleFactor = SHADOW_MAP_RESOLUTION_SCALE[graphicsQualityLevel.ordinal()];
       int shadowMapWidth = Math.round(mDisplayWidth * shadowMapResolutionScaleFactor);
       int shadowMapHeight = Math.round(mDisplayHeight * shadowMapResolutionScaleFactor);

       getLightSource().updateViewProjectionMatrix(shadowMapWidth, shadowMapHeight);
       shadowMapFBO = hasDepthTextureExtension ?
               new DepthBufferFBO(shadowMapWidth, shadowMapHeight, clColor) :
               new ColorBufferFBO(shadowMapWidth, shadowMapHeight, clColor);
    }

    /** for post effects image processing */
    @SuppressWarnings("unused")
    private void generateMainRenderFBO() {
        mainRenderFBO = new ColorBufferFBO(Math.round(mDisplayWidth),
                                           Math.round(mDisplayHeight),
                                           new Color4f(0.0f, 0.7f, 1.0f, 1.0f));
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
            else if (isSimulating && object instanceof PNodeObject
                    && (((PNodeObject) object).getTag() == MOVING_OBJECT)
                    && ((PNodeObject) object).get_body() != null) {

                PNodeObject movingObject = (PNodeObject) object;
                Transform transform = movingObject.getWorldTransformActual();

                if (!movingObject.getWorldTransformOld().equals(transform))
                    movingObject.setWorldTransformMatrix(transform);

                else {
                    physicalWorld.removeRigidBody(movingObject.get_body());
                    movingObject.set_body(null);

                    if (gameEventsCallBackListener != null)
                        gameEventsCallBackListener.onStopMovingObject(movingObject);
                }
            }
        }
    }

    private void drawScene() {
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

        GLES20JniWrapper.glEnableFrontFacesCulling();

        GLShaderProgram program = getCachedShader(SHADOW_MAP_OBJECT);
        program.useProgram();

        AbstractGL3DObject prevObject = null;
        for (AbstractGL3DObject object : objects.values()) {
            if (!object.isCastShadow())
                continue;

            program.bindMVPMatrix(object, getLightSource().getViewMatrix(), getLightSource().getProjectionMatrix());

            if (!object.equals(prevObject)) {
                object.bindVBO(program);
                prevObject = object;
            }
            object.render();
        }

        shadowMapFBO.unbind();
    }

    /** for post effects image processing */
    private void renderPostEffectsBuffer() {
        GLES20JniWrapper.glViewport(mDisplayWidth, mDisplayHeight);

        postEffects2DScreen.getProgram().useProgram();

        postEffects2DScreen.setGlTexture(mainRenderFBO.getFboTexture());
        postEffects2DScreen.bindMaterial();
        postEffects2DScreen.bindVBO();

        postEffects2DScreen.render();
    }

    private void renderColorBuffer() {//TODO: add group of objects processing (with single transform matrix) -> TreeNodeObject as root, map and chips pool
        /** for post effects image processing */
        ///mainRenderFBO.bind();

        GLES20JniWrapper.glEnableBackFacesCulling();
        GLES20JniWrapper.glViewport(mDisplayWidth, mDisplayHeight);

        drawChilds();

        /** for post effects image processing */
        ///mainRenderFBO.unbind();
    }

    private void drawChilds() {
        GLShaderProgram program = null;
        AbstractGL3DObject prevObject = null;

        for (AbstractGL3DObject object : objects.values()) {
            if (object.getProgram() != program) {
                program = object.getProgram();
                program.useProgram();
                program.bindGlobalParams(this);
            }

            if (program == null) continue;

            synchronized (lockObject) { program.bindMVPMatrix(object, camera.getViewMatrix(),camera.getProjectionMatrix()); }
            program.bindAdditionalParams(this, object);

            object.bindMaterial();
            if (!object.equals(prevObject)) {
                object.bindVBO();
                prevObject = object;
            }

            object.render();
        }
    }

    private void simulatePhysics(long currentTime) {
        if (isSimulating && physicalWorld != null) {

            float realInterval = frameTime / 1000f;
            float discreteInterval = 1 / 60f;
            for (float i = 0; i < (realInterval / discreteInterval); i++)
                physicalWorld.stepSimulation(discreteInterval);
            ///physicalWorld.stepSimulation((currentTime - simulation_time) / 1000f, 4, 1f / SIMULATION_FRAMES_IN_SEC );
            ///simulation_time = currentTime;

            for (int i = 0; i < physicalWorld.getDispatcher().getNumManifolds(); i++)
                if (   physicalWorld.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() > 0
                    && physicalWorld.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() != oldNumContacts
                    ) {

                    if (gameEventsCallBackListener != null)
                        gameEventsCallBackListener.onRollingObjectStart(null); //TODO: Get Object ref from Collision example

                    oldNumContacts = physicalWorld.getDispatcher().getManifoldByIndexInternal(i).getNumContacts();
                    old_time = currentTime;
                }
                else if ((physicalWorld.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() == 0))
                    if (gameEventsCallBackListener != null)
                        gameEventsCallBackListener.onRollingObjectStop(null); //TODO: Get Object ref from Collision example
                else if (currentTime - old_time > 150)
                        if (gameEventsCallBackListener != null)
                            gameEventsCallBackListener.onRollingObjectStop(null); //TODO: Get Object ref from Collision example

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
        GLES20JniWrapper.glBindFramebuffer(0);
        GLES20JniWrapper.glClearColor(0.0f, 0.7f, 1.0f, 1.0f);
        GLES20JniWrapper.glClear();
    }

    @SuppressWarnings("unused")
    public void cleanUp() {
        stopSimulation();
        clearData();
    }

    private void initScene() {
        camera = new FixedIsometricCamera(DEFAULT_CAMERA_X,
                              DEFAULT_CAMERA_Y,
                              DEFAULT_CAMERA_Z,
                              DEFAULT_CAMERA_PITCH,
                              DEFAULT_CAMERA_YAW,
                              DEFAULT_CAMERA_ROLL);

        if (gameEventsCallBackListener != null)
            gameEventsCallBackListener.onInitGLCamera(camera);

        lightSource = new GLLightSource(new float[] {DEFAULT_LIGHT_X, DEFAULT_LIGHT_Y, DEFAULT_LIGHT_Z, 1.0f},
                                        DEFAULT_LIGHT_COLOUR,
                                        getCamera(),
                                        sysUtilsWrapper);

        if (gameEventsCallBackListener != null)
            gameEventsCallBackListener.onInitLightSource(lightSource);
    }

    private void initPhysics() {
        BroadphaseInterface _broadphase = new DbvtBroadphase();

        DefaultCollisionConfiguration _collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher _dispatcher = new CollisionDispatcher(_collisionConfiguration);

        SequentialImpulseConstraintSolver _solver = new SequentialImpulseConstraintSolver();

        physicalWorld = new DiscreteDynamicsWorld(_dispatcher, _broadphase, _solver, _collisionConfiguration);
        physicalWorld.setGravity(DEFAULT_GRAVITY_VECTOR);

        if (gameEventsCallBackListener != null)
            gameEventsCallBackListener.onInitPhysics(physicalWorld);
    }

    private void loadScene() {
        /** Create internal shaders before loading scene  */
        getCachedShader(SHADOW_MAP_OBJECT);

        /** for postprocessing effects */
        ///createPostEffects2DScreen();

        if (gameEventsCallBackListener != null)
            gameEventsCallBackListener.onLoadSceneObjects(this, physicalWorld);

        forceGCandWait();
    }

    private void createPostEffects2DScreen() {
        GLShaderProgram guiShader = getCachedShader(GUI_OBJECT);
        postEffects2DScreen = new GUI2DImageObject(sysUtilsWrapper, guiShader, new Vector4f(-1, 1, 1, -1));
        postEffects2DScreen.loadObject();
    }

    private void scenePrepare() {
        hasDepthTextureExtension = checkDepthTextureExtension();
        GLES20JniWrapper.glEnableFacesCulling();
        GLES20JniWrapper.glEnableDepthTest();

        initScene();
        initPhysics();
        loadScene();
        startSimulation();
    }

    private void updateViewPorts(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        camera.setAspectRatio(width, height);

        generateShadowMapFBO();
        /** for post effects */
        ///generateMainRenderFBO();
    }

    /** GLRendererInterface implementation -------------------------------------------------------*/
    @Override
    public void setGameEventsCallBackListener(GameEventsCallbackInterface gameEventsCallBackListener) {
        this.gameEventsCallBackListener = gameEventsCallBackListener;
    }

    @Override
    public GLScene getScene() {
        return this;
    }

    @Override
    public DiscreteDynamicsWorld getPhysicalWorldObject() {
        return physicalWorld;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        scenePrepare();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        updateViewPorts(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawScene();
    }
    /** ------------------------------------------------------------------------------------------*/

}
