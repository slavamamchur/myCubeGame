package com.sadgames.gl3d_engine.gl_render.scene;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import com.sadgames.gl3d_engine.gl_render.IGameEventsCallBack;
import com.sadgames.gl3d_engine.gl_render.scene.fbo.AbstractFBO;
import com.sadgames.gl3d_engine.gl_render.scene.fbo.ColorBufferFBO;
import com.sadgames.gl3d_engine.gl_render.scene.fbo.DepthBufferFBO;
import com.sadgames.gl3d_engine.gl_render.scene.objects.GLSceneObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.PNode;
import com.sadgames.gl3d_engine.gl_render.scene.objects.onScreen2DBox;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GUIRendererProgram;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.ShadowMapProgram;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.ShapeShader;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.TerrainRendererProgram;
import com.sadgames.sysutils.ISysUtilsWrapper;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_EXTENSIONS;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.GL_TEXTURE4;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glGetString;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.SHADOWMAP_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.OES_DEPTH_TEXTURE_EXTENSION;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.SHADOW_MAP_RESOLUTION;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.ShadowMapQuality;
import static com.sadgames.gl3d_engine.gl_render.scene.objects.PNode.MOVING_OBJECT;

public class GLScene {

    public final static Object lockObject = new Object();
    private final static float WAVE_SPEED = 0.04f;
    public final static long CAMERA_ZOOM_ANIMATION_DURATION = 1000;
    public final static float SIMULATION_FRAMES_IN_SEC = 60f;/** FPS */

    private GLCamera camera = null;
    private GLLightSource lightSource = null;
    private long simulation_time = 0;
    private boolean isSimulating = false;
    private DiscreteDynamicsWorld physicalWorld;
    private boolean hasDepthTextureExtension = checkDepthTextureExtension();
    private int oldNumContacts = 0;
    private long old_time = System.currentTimeMillis();
    private long old_frame_time = 0;
    private float moveFactor = 0;
    private long frameTime = 0;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private boolean isRenderStopped = false;

    private Map<String, GLSceneObject> objects = new HashMap<>();
    private Map<GLObjectType, GLShaderProgram> shaders = new HashMap<>();

    private AbstractFBO shadowMapFBO = null;
    private AbstractFBO mainRenderFBO = null;
    private onScreen2DBox postEffects2DScreen = null;
    private GLAnimation zoomCameraAnimation = null;
    private ISysUtilsWrapper sysUtilsWrapper;
    private IGameEventsCallBack gameEventsCallBack = null;

    public GLScene(ISysUtilsWrapper sysUtilsWrapper) {
        this.sysUtilsWrapper = sysUtilsWrapper;
    }

    private static boolean checkDepthTextureExtension() {
        return glGetString(GL_EXTENSIONS).contains(OES_DEPTH_TEXTURE_EXTENSION);
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
    public void setPhysicalWorld(DiscreteDynamicsWorld physicalWorld) {
        this.physicalWorld = physicalWorld;
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
    public onScreen2DBox getPostEffects2DScreen() {
        return postEffects2DScreen;
    }
    public void setPostEffects2DScreen(onScreen2DBox postEffects2DScreen) {
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
    public void setGameEventsCallBack(IGameEventsCallBack gameEventsCallBack) {
        this.gameEventsCallBack = gameEventsCallBack;
    }
    public void setRenderStopped(boolean renderStopped) {
        isRenderStopped = renderStopped;
    }

    public void addObject(GLSceneObject object, String name) {
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
        for (GLSceneObject object : objects.values())
            object.clearData();

        objects.clear();

        if (postEffects2DScreen != null)
            postEffects2DScreen.clearData();
    }

    public void clearShaderCache() {
        glUseProgram(0);

        for (GLShaderProgram program : shaders.values()) {
            /*for (GLShaderParam param : program.getParams().values())
                if (param instanceof GLShaderParamVBO)
                    ((GLShaderParamVBO) param).clearParamDataVBO();*/

            program.deleteProgram();
        }

        shaders.clear();
    }

    public GLSceneObject getObject(String name) {
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
                    program = new ShapeShader(sysUtilsWrapper);
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
       float shadowMapQuality = SHADOW_MAP_RESOLUTION[ShadowMapQuality.HIGH.ordinal()];
       int shadowMapWidth = Math.round(mDisplayWidth * shadowMapQuality);
       int shadowMapHeight = Math.round(mDisplayHeight * shadowMapQuality);

       getLightSource().updateViewProjectionMatrix(shadowMapWidth, shadowMapHeight);
       shadowMapFBO = hasDepthTextureExtension ?
               new DepthBufferFBO(shadowMapWidth, shadowMapHeight, clColor) :
               new ColorBufferFBO(shadowMapWidth, shadowMapHeight, clColor);
    }

    public void generateMainRenderFBO() {
        mainRenderFBO =
                new ColorBufferFBO(Math.round(mDisplayWidth), Math.round(mDisplayHeight), new Color4f(0.0f, 0.0f, 0.0f, 0.0f));
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

        for (GLSceneObject object : objects.values()) {

            GLAnimation animation = object.getAnimation();
            if (animation != null && animation.isInProgress()) {
                animation.animate(object);
            }
            else if (isSimulating && object instanceof PNode && (((PNode) object).getTag() == MOVING_OBJECT)
                    && ((PNode) object).get_body() != null) {

                PNode movingObject = (PNode) object;
                Transform transform = movingObject.getWorldTransformActual();

                if (!movingObject.getWorldTransformOld().equals(transform))
                    movingObject.setWorldTransformMatrix(transform);

                else {
                    physicalWorld.removeRigidBody(movingObject.get_body());
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

        glCullFace(GL_FRONT);

        GLShaderProgram program = getCachedShader(SHADOWMAP_OBJECT);
        program.useProgram();

        /** for shadowbox implementation
        lightSource.updateViewProjectionMatrix(); */

        int prevObject = -1;
        for (GLSceneObject object : objects.values()) {
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
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, mDisplayWidth, mDisplayHeight);

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

        glCullFace(GL_BACK);
        glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        TerrainRendererProgram program = (TerrainRendererProgram) getCachedShader(TERRAIN_OBJECT);
        program.useProgram();

        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, shadowMapFBO.getFboTexture().getTextureId());
        program.paramByName(ACTIVE_SHADOWMAP_SLOT_PARAM_NAME).setParamValue(4);

        synchronized (lockObject) {
            program.setCameraPosition(getCamera().getCameraPosition());
        }

        program.setLightSourcePosition(getLightSource().getLightPosInEyeSpace());
        program.setLightColourValue(getLightSource().getLightColour());

        program.setWaveMovingFactor(moveFactor);

        /** for rgb depth buffers */
        ///program.paramByName(UX_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / mShadowMapWidth));
        ///program.paramByName(UY_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / mShadowMapHeight));

        int prevObject = -1;
        for (GLSceneObject object : objects.values()) {
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

                    if (gameEventsCallBack != null)
                        gameEventsCallBack.onRollingObjectStart(null); //TODO: Get Object ref from Collision example

                    oldNumContacts = physicalWorld.getDispatcher().getManifoldByIndexInternal(i).getNumContacts();
                    old_time = currentTime;
                }
                else if ((physicalWorld.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() == 0))
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
         PNode* pnA = (__bridge PNode*)obA->getUserPointer();
         PNode* pnB = (__bridge PNode*)obB->getUserPointer();*/
    }

    private void calculateFrameTime(long currentTime) {
        frameTime = old_frame_time == 0 ? 0 : currentTime - old_frame_time;
        old_frame_time = currentTime;
    }

    private void clearRenderBuffers() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClearColor(0f, 0.0f, 0f, 0f); //glClearColor(0f, 0.7f, 1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        stopSimulation();
        clearData();
    }
}
