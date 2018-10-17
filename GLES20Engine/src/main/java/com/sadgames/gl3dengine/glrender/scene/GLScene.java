package com.sadgames.gl3dengine.glrender.scene;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
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
import com.sadgames.gl3dengine.glrender.scene.camera.Orthogonal2DCamera;
import com.sadgames.gl3dengine.glrender.scene.fbo.AbstractFBO;
import com.sadgames.gl3dengine.glrender.scene.fbo.ColorBufferFBO;
import com.sadgames.gl3dengine.glrender.scene.fbo.DepthBufferFBO;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.GUI2DImageObject;
import com.sadgames.gl3dengine.glrender.scene.objects.GameItemObject;
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject;
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.GUIRendererProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.ShadowMapProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.SkyBoxProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.SkyDomeProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.TerrainRendererProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.luaj.vm2.Globals;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_GRAVITY_VECTOR;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_LIGHT_COLOUR;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_LIGHT_X;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_LIGHT_Y;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_LIGHT_Z;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.SHADOW_MAP_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GraphicsQuality;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.OES_DEPTH_TEXTURE_EXTENSION;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOW_MAP_RESOLUTION_SCALE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.WAVE_SPEED;
import static com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject.MOVING_OBJECT;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

public class GLScene extends SceneObjectsTreeItem implements GLRendererInterface {

    public  final static Object lockObject = new Object();
    private final static long CAMERA_ZOOM_ANIMATION_DURATION = 1000;

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
    private boolean isSceneLoaded = false;
    private GLCamera camera = null;
    private GLCamera savedCamera = null;
    private GLLightSource lightSource = null;
    private DiscreteDynamicsWorld physicalWorld = null;
    private Map<GLObjectType, GLShaderProgram> shaders = new HashMap<>();
    private AbstractFBO shadowMapFBO = null;
    private AbstractFBO mainRenderFBO = null;
    private GUI2DImageObject postEffects2DScreen = null;
    private GLAnimation zoomCameraAnimation = null;
    private SysUtilsWrapperInterface sysUtilsWrapper;
    private GameEventsCallbackInterface gameEventsCallBackListener = null;
    private GraphicsQuality graphicsQualityLevel;
    private String backgroundTextureName = null;
    private Globals luaEngine = null;

    public GLScene(SysUtilsWrapperInterface sysUtilsWrapper) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        this.graphicsQualityLevel = getSettingsManager().getGraphicsQualityLevel();
    }

    public GLCamera getCamera() {
        return camera;
    }

    public void setCamera(GLCamera camera) {
        GLCamera oldCam = this.camera;
        this.camera = camera == null ? savedCamera : camera;
        savedCamera = oldCam;

        if (this.camera.getAspectRatio() == -1)
            this.camera.setAspectRatio(mDisplayWidth, mDisplayHeight);

        lightSource.setCamera(this.camera);
    }

    @SuppressWarnings("unused")public Globals getLuaEngine() {
        return luaEngine;
    }
    public void setLuaEngine(Globals luaEngine) {
        this.luaEngine = luaEngine;
    }
    public String getBackgroundTextureName() {
        return backgroundTextureName;
    }
    public void setBackgroundTextureName(String backgroundTextureName) {
        this.backgroundTextureName = backgroundTextureName;
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

    public boolean isSceneLoaded() {
        synchronized (lockObject) {
            return isSceneLoaded;
        }
    }

    public void setSceneLoaded(boolean sceneLoaded) {
        synchronized (lockObject) {
            isSceneLoaded = sceneLoaded;
        }
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
        proceesTreeItems(item -> ((AbstractGL3DObject)item).clearData());
        childs.clear();

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
        return (AbstractGL3DObject) getChild(name);
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
                case SKY_DOME_OBJECT:
                    program = new SkyDomeProgram(sysUtilsWrapper);
                    break;
                default:
                    program = new TerrainRendererProgram(sysUtilsWrapper);
            }

            shaders.put(type, program);
        }

        return program;
    }

    @SuppressWarnings("unused") public GLAnimation createZoomCameraAnimation(float zoomLevel) {
        GLAnimation animation = new GLAnimation(zoomLevel, CAMERA_ZOOM_ANIMATION_DURATION);
        animation.setLuaEngine(luaEngine);

        return animation;
    }

    @SuppressWarnings("unused")
    public GLAnimation createTranslateAnimation(float fromX, float toX, float fromY, float toY, float fromZ, float toZ, long duration) {
        GLAnimation animation = new GLAnimation(fromX, toX, fromY, toY, fromZ, toZ, duration);
        animation.setLuaEngine(luaEngine);

        return animation;
    }

    @SuppressWarnings("unused")
    public GLAnimation createRotateAnimation(float rotationAngle, short rotationAxesMask, long animationDuration) {
        GLAnimation animation = new GLAnimation(rotationAngle, rotationAxesMask, animationDuration);
        animation.setLuaEngine(luaEngine);

        return animation;
    }

    @SuppressWarnings("unused") public Matrix4f createTransform() {
        return new Matrix4f();
    }

    @SuppressWarnings("unused") public Vector3f createVector3f(float vx, float vy, float vz) {
        return new Vector3f(vx, vy, vz);
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
        calculateCameraPosition();
        calculateWavesMovingFactor();

        proceesTreeItems(item -> caclulateObjectsAnimations(item));
    }

    private void caclulateObjectsAnimations(SceneObjectsTreeItem sceneObject) {
        AbstractGL3DObject gl3DObject = (AbstractGL3DObject) sceneObject;

        GLAnimation animation = gl3DObject.getAnimation();
        if (animation != null && animation.isInProgress()) {
            animation.animate(gl3DObject);
        }
        else if (isSimulating && gl3DObject instanceof PNodeObject
                && (((PNodeObject) gl3DObject).getTag() == MOVING_OBJECT)
                && ((PNodeObject) gl3DObject).get_body() != null) {

            PNodeObject movingObject = (PNodeObject) gl3DObject;
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

    private void calculateWavesMovingFactor() {
        try {
            moveFactor += WAVE_SPEED * frameTime / 1000;
            moveFactor %= 1;
        }
        catch (Exception e) {
            moveFactor = 0;
        }
    }

    private void calculateCameraPosition() {
        if (zoomCameraAnimation != null && zoomCameraAnimation.isInProgress()) {
            zoomCameraAnimation.animate(camera);
        }
    }

    private void drawScene() {
        long currentTime = System.currentTimeMillis();
        calculateFrameTime(currentTime);

        if (isRenderStopped) return;

        if (gameEventsCallBackListener != null)
            gameEventsCallBackListener.onBeforeDrawFrame(frameTime);

        //clearRenderBuffers();

        simulatePhysics(currentTime);
        calculateSceneTransformations();

        renderShadowMapBuffer();
        renderColorBuffer();
        /** for post effects image processing */
        //renderPostEffectsBuffer();
    }

    private AbstractGL3DObject prevObject = null;

    private void renderShadowMapBuffer() {
        shadowMapFBO.bind();
        GLES20JniWrapper.glEnableFrontFacesCulling();

        //glEnable(GL_POLYGON_OFFSET_FILL);
        //glPolygonOffset(1f, 2f);

        getCachedShader(SHADOW_MAP_OBJECT).useProgram();
        prevObject = null;
        proceesTreeItems(item -> drawObjectIntoShadowMap(item));

        shadowMapFBO.unbind();

        //glDisable(GL_POLYGON_OFFSET_FILL);
    }

    //TODO: dice texture corrupted by shadowmap texture - low memory or other reason?
    private void drawObjectIntoShadowMap(SceneObjectsTreeItem sceneObject) {
        AbstractGL3DObject gl3DObject = (AbstractGL3DObject) sceneObject;
        GLShaderProgram program = getCachedShader(SHADOW_MAP_OBJECT);

        if (!gl3DObject.isCastShadow())
            return;

        program.bindMVPMatrix(gl3DObject, lightSource.getViewMatrix(), lightSource.getProjectionMatrix());

        if (!gl3DObject.equals(prevObject)) {
            gl3DObject.bindVBO(program);
            prevObject = gl3DObject;
        }

        gl3DObject.render();
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

    private GLShaderProgram program = null;
    private void renderColorBuffer() {
        /** for post effects image processing */
        //mainRenderFBO.bind();

        GLES20JniWrapper.glEnableBackFacesCulling();
        GLES20JniWrapper.glViewport(mDisplayWidth, mDisplayHeight);
        clearRenderBuffers();

        prevObject = null;
        program = null;
        proceesTreeItems(item -> drawObjectIntoColorBuffer(item));

        /** for post effects image processing */
        //mainRenderFBO.unbind();
    }

    private void drawObjectIntoColorBuffer(SceneObjectsTreeItem sceneObject) {
        AbstractGL3DObject gl3DObject = (AbstractGL3DObject) sceneObject;

        if (gl3DObject.getProgram() != program) {
            program = gl3DObject.getProgram();
            program.useProgram();
            program.bindGlobalParams(this);
        }

        if (program == null) return;

        synchronized (lockObject) { program.bindMVPMatrix(gl3DObject, camera.getViewMatrix(),camera.getProjectionMatrix()); }
        program.bindAdditionalParams(this, gl3DObject);

        gl3DObject.bindMaterial();
        if (!gl3DObject.equals(prevObject)) {
            gl3DObject.bindVBO();
            prevObject = gl3DObject;
        }

        gl3DObject.render();

        gl3DObject.unbindTexture(0);
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

                    if (gameEventsCallBackListener != null) {
                        //TODO: for all object with tag "moving"
                        Object collisionObject = ((CollisionObject)physicalWorld.getDispatcher().getManifoldByIndexInternal(i).getBody1()).getUserPointer();
                        gameEventsCallBackListener.onRollingObjectStart((GameItemObject)collisionObject);
                    }

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
    }

    private void calculateFrameTime(long currentTime) {
        frameTime = old_frame_time == 0 ? 0 : currentTime - old_frame_time;
        old_frame_time = currentTime;
    }

    private void clearRenderBuffers() {
        GLES20JniWrapper.glBindFramebuffer(0);
        GLES20JniWrapper.glClearColor(0.48f, 0.62f, 0.68f, 1.0f);
        GLES20JniWrapper.glClear();
    }

    @SuppressWarnings("unused")
    public void cleanUp() {
        stopSimulation();
        clearData();
    }

    public GLCamera createCamIsometric(float xPos, float yPos, float zPos, float pitch, float yaw, float roll) {
        return new FixedIsometricCamera(xPos, yPos, zPos, pitch, yaw,  roll);
    }

    @SuppressWarnings("unused") public GLCamera createCam2D(float landSize) {
        return new Orthogonal2DCamera(landSize);
    }

    private void initScene() {
        camera = createCamIsometric(0f,0.1f,3f,0f,0f,0f);
        lightSource = new GLLightSource(new float[] {DEFAULT_LIGHT_X, DEFAULT_LIGHT_Y, DEFAULT_LIGHT_Z, 1.0f},
                                        DEFAULT_LIGHT_COLOUR,
                                        camera,
                                        sysUtilsWrapper);

        if (gameEventsCallBackListener != null) gameEventsCallBackListener.onInitGLCamera(camera);
        camera.updateViewMatrix();

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
        setSceneLoaded(false);

        /** Create internal shaders before loading scene  */
        getCachedShader(SHADOW_MAP_OBJECT);

        /** for postprocessing effects */
        //createPostEffects2DScreen();

        if (gameEventsCallBackListener != null)
            gameEventsCallBackListener.onLoadSceneObjects(this, physicalWorld);
    }

    private void createPostEffects2DScreen() {
        GLShaderProgram guiShader = getCachedShader(GUI_OBJECT);
        postEffects2DScreen = new GUI2DImageObject(sysUtilsWrapper, guiShader, new Vector4f(-1, 1, 1, -1), false);
        postEffects2DScreen.loadObject();
    }

    private void scenePrepare() {
        hasDepthTextureExtension = checkDepthTextureExtension();
        GLES20JniWrapper.glEnableFacesCulling();
        GLES20JniWrapper.glEnableDepthTest();

        //TODO: glEnable(MULTISAMPLING)

        initScene();
        initPhysics();
        loadScene();
        startSimulation();
    }

    private void updateViewPorts(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        camera.setAspectRatio(width, height);

        if (getSettingsManager().isIn_2D_Mode()) {
            camera.setVfov(camera.getVfov() / 1.5f);
            camera.setZoomed_vfov(camera.getVfov());
        }

        generateShadowMapFBO();
        /** for post effects */
        generateMainRenderFBO();
    }

    private boolean old2D_ModeValue;

    public void switrchTo2DMode() {
        synchronized (lockObject) {
            old2D_ModeValue = getSettingsManager().isIn_2D_Mode();
            getSettingsManager().setIn_2D_Mode(true);
            setCamera(new Orthogonal2DCamera(LAND_SIZE_IN_WORLD_SPACE));
        }
    }

    public void restorePrevViewMode() {
        synchronized (lockObject) {
            getSettingsManager().setIn_2D_Mode(old2D_ModeValue);
            setCamera(null);
        }
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
    public void onSurfaceCreated() {
        scenePrepare();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        updateViewPorts(width, height);
    }

    @Override
    public void onDrawFrame() {
        drawScene();
    }
    /** ------------------------------------------------------------------------------------------*/

}
