package com.cubegames.slava.cubegame.gl_render.scene;

import android.content.Context;
import android.content.Intent;
import android.opengl.Matrix;
import android.os.Bundle;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.gl_render.GLAnimation;
import com.cubegames.slava.cubegame.gl_render.scene.fbo.AbstractFBO;
import com.cubegames.slava.cubegame.gl_render.scene.fbo.ColorBufferFBO;
import com.cubegames.slava.cubegame.gl_render.scene.fbo.DepthBufferFBO;
import com.cubegames.slava.cubegame.gl_render.scene.objects.DiceObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.PNode;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.ShadowMapProgram;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.ShapeShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.TerrainShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.WaterShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.mySoundPalyer;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
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
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_ACTION_SHOW_TURN_INFO;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_DICE_VALUE;
import static com.cubegames.slava.cubegame.api.RestApiService.startActionMooveGameInstance;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.SHADOWMAP_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;

public class GLScene {

    public final static Object lockObject = new Object();
    private final static float WAVE_SPEED = 0.04f;

    private Context context;
    private GLCamera camera;
    private GLLightSource lightSource;
    private long simulation_time = 0;
    private boolean isSimulating = false;
    private DiscreteDynamicsWorld _world;
    private GameInstance gameInstanceEntity = null;
    private boolean hasDepthTextureExtension;

    private int mDisplayWidth;
    private int mDisplayHeight;
    private AbstractFBO shadowmapFBO;

    /** Objects cache*/
    private Map<String, GLSceneObject> objects = new HashMap<>();
    /** Shaders cache*/
    private Map<GLObjectType, GLShaderProgram> shaders = new HashMap<>();

    public GLScene(Context context) {
        this.context = context;

        Matrix.setIdentityM(mViewMatrix, 0); //??? save old
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
    public void set_world(DiscreteDynamicsWorld _world) {
        this._world = _world;
    }
    public void setGameInstanceEntity(GameInstance gameInstanceEntity) {
        this.gameInstanceEntity = gameInstanceEntity;
    }
    public boolean hasDepthTextureExtension() {
        return hasDepthTextureExtension;
    }
    public void setHasDepthTextureExtension(boolean hasDepthTextureExtension) {
        this.hasDepthTextureExtension = hasDepthTextureExtension;
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

    public AbstractFBO getShadowmapFBO() {
        return shadowmapFBO;
    }

    public GLAnimation zoomCameraAnimation;
    private float[] mViewMatrix = new float[16];

    public void addObject(GLSceneObject object, String name) {
        objects.put(name, object);
    }

    public void deleteObject(String name) {
        //GLSceneObject object = getObject(name);

        //if (object != null) {
            //object.clearData();
            objects.remove(name);
        //}
    }

    public void clearData() {
        clearObjectsCache();
        clearShaderCache();
    }

    public void clearObjectsCache() {
        for (GLSceneObject object : objects.values())
            object.clearData();

        objects.clear();
    }

    public void clearShaderCache() {
        glUseProgram(0);

        for (GLShaderProgram program : shaders.values()) {
            for (GLShaderParam param : program.getParams().values())
                if (param instanceof GLShaderParamVBO)
                    ((GLShaderParamVBO) param).clearParamDataVBO();

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
                    program = new TerrainShader(context);
                    break;
                case WATER_OBJECT:
                    program = new WaterShader(context);
                    break;
                case SHADOWMAP_OBJECT:
                    program = new ShadowMapProgram(context);
                    break;
                default:
                    program = new ShapeShader(context);
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
    Transform old_transform = new Transform(new Matrix4f(new float[16]));

    private void sendResponseIntent(String action, Bundle params){
        Intent responseIntent = new Intent();
        responseIntent.setAction(action);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtras(params);
        context.sendBroadcast(responseIntent);
    }

    public void generateShadowmapFBO() {
       Color4f clColor = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
       int width = Math.round(mDisplayWidth);
       int height = Math.round(mDisplayHeight);

       shadowmapFBO =
               hasDepthTextureExtension ? new DepthBufferFBO(width, height, clColor) : new ColorBufferFBO(width, height, clColor);
    }

    private void removeDice(DiceObject dice, Transform transform) {
        //toggleActionBarProgress(true);
        gameInstanceEntity.setStepsToGo(dice.getTopFaceDiceValue(transform));

        Bundle params = new Bundle();
        params.putInt(EXTRA_DICE_VALUE, gameInstanceEntity.getStepsToGo());
        sendResponseIntent(ACTION_ACTION_SHOW_TURN_INFO, params);

        startActionMooveGameInstance(context, gameInstanceEntity);

        //TODO: zoom out camera
        /*float[] camera = getCamera().getCameraPosition();
        zoomCameraAnimation = new GLAnimation(GLRenderConsts.GLAnimationType.ZOOM_ANIMATION, camera[1], camera[2], camera[2] - 2f, 1500);
        zoomCameraAnimation.startAnimation(new GLAnimation.AnimationCallBack() {
            @Override
            public void onAnimationEnd() {
                startActionMooveGameInstance(context, gameInstanceEntity);
            }
        });*/

        /*runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                showAnimatedText(String.format("%d\nSteps\nto GO", dice_1_Value));
            }
        }));*/

    }

    private int oldNumContacts = 0;
    private long old_time = System.currentTimeMillis();
    private long old_frame_time = 0;
    private float moveFactor = 0;
    private long frameTime = 0;

    public long getFrameTime() {
        return frameTime;
    }

    private void calculateSceneTransformations() {
        /*if (zoomCameraAnimation != null && zoomCameraAnimation.isInProgress()) {
            float[] mMatrix = new float[16];

            zoomCameraAnimation.animate(mViewMatrix);
            Matrix.multiplyMM(mMatrix, 0, camera.getViewMatrix(), 0, mViewMatrix, 0);
            camera.setViewMatrix(mMatrix);

            //TODO: calc and set position
            //Vector3f camera_pos = new Vector3f(camera.getCameraPosition());
            //Matrix4f m = new Matrix4f(mMatrix);
            //m.transform(camera_pos);
            //camera.setCameraPosition(camera_pos.x, camera_pos.y, camera_pos.z);
        }*/

        try {
            moveFactor += WAVE_SPEED * frameTime / 1000;
            moveFactor %= 1;
        }
        catch (Exception e) {
            moveFactor = 0;
        }

        for (GLSceneObject object : objects.values()) {
            object.setModelMatrix();

            GLAnimation animation = object.getAnimation();
            if (animation != null && animation.isInProgress()) {
                animation.animate(object.getModelMatrix());
            }

            if (isSimulating && object instanceof DiceObject && (((PNode) object).getTag() == 1)
                    && ((PNode) object).get_body() != null) {
                Transform tr = new Transform(new Matrix4f(new float[16]));
                float[] mat = new float[16];
                ((PNode) object).get_body().getWorldTransform(tr).getOpenGLMatrix(mat);

                if (!old_transform.equals(tr)) {
                    old_transform = tr;
                } else {
                    if (((PNode) object).get_body() != null) {
                        _world.removeRigidBody(((PNode) object).get_body());
                        ((PNode) object).set_body(null);
                        removeDice((DiceObject) object, tr);
                    }
                    Matrix.setIdentityM(mat, 0);
                    Matrix.translateM(mat, 0, 100f, 0f, 0);
                }

                object.setModelMatrix(mat);
            }
        }
    }

    public void drawScene() {
        clearRenderBuffers();

        long currentTime = System.currentTimeMillis();
        calculateFrameTime(currentTime);//todo: thread protected

        simulatePhysics(currentTime);
        calculateSceneTransformations();

        renderShadowMapBuffer();
        renderColorBuffer(); //TODO: into fbo
        renderPostEffectsBuffer();
    }

    private void renderShadowMapBuffer() {
        glCullFace(GL_FRONT);

        shadowmapFBO.bindFBO();

        GLShaderProgram program = getCachedShader(SHADOWMAP_OBJECT);
        program.useProgram();

        GLObjectType prevObject = GLObjectType.UNKNOWN_OBJECT;
        for (GLSceneObject object : objects.values()) {
            if (object.getObjectType().equals(GLObjectType.TERRAIN_OBJECT))
                continue;

            program.bindMVPMatrix(object, getLightSource().getViewMatrix(), getLightSource().getProjectionMatrix());

            if (!object.getObjectType().equals(prevObject)) {
                object.prepare(program);
                prevObject = object.getObjectType();
            }

            object.render();
        }
    }

    private void renderPostEffectsBuffer() {
        //TODO:
    }

    private void renderColorBuffer() {
        glCullFace(GL_BACK);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        TerrainShader program = (TerrainShader) getCachedShader(TERRAIN_OBJECT);
        program.useProgram();

        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, shadowmapFBO.getFboTexture());
        program.paramByName(ACTIVE_SHADOWMAP_SLOT_PARAM_NAME).setParamValue(4);

        program.setCameraData(getCamera().getCameraPosition());

        program.setLightSourceData(getLightSource().getLightPosInEyeSpace());
        program.setLightColourValue(getLightSource().getLightColour());

        program.setWaveMovingFactor(moveFactor);

        ///program.paramByName(UX_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / mShadowMapWidth));
        ///program.paramByName(UY_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / mShadowMapHeight));

        GLObjectType prevObject = GLObjectType.UNKNOWN_OBJECT;
        for (GLSceneObject object : objects.values()) {
            program.bindMVPMatrix(object, getCamera().getViewMatrix(), getCamera().getProjectionMatrix());
            program.bindLightSourceMVP(object, getLightSource().getViewMatrix(), getLightSource().getProjectionMatrix(), hasDepthTextureExtension);

            program.setMaterialParams(object);

            if (!object.getObjectType().equals(prevObject)) {
                object.prepare(program);
                prevObject = object.getObjectType();
            }

            object.render();
        }
    }

    private void simulatePhysics(long currentTime) {
        if (isSimulating) {
            _world.stepSimulation(currentTime - simulation_time);
            simulation_time = currentTime;

            for (int i = 0; i < _world.getDispatcher().getNumManifolds(); i++)
                if (_world.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() > 0 && _world.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() != oldNumContacts) {
                    mySoundPalyer.play(context, R.raw.dice_2);

                    oldNumContacts = _world.getDispatcher().getManifoldByIndexInternal(i).getNumContacts();
                    old_time = currentTime;
                }
                else if ((_world.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() == 0))
                        mySoundPalyer.stop();
                else if (currentTime - old_time > 150)
                        mySoundPalyer.stop();

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
        glClearColor(0f, 0.7f, 1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    protected void finalize() throws Throwable {
        stopSimulation();
        clearData();

        super.finalize();
    }
}
