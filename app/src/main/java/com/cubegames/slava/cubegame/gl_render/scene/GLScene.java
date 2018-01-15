package com.cubegames.slava.cubegame.gl_render.scene;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.gl_render.GLAnimation;
import com.cubegames.slava.cubegame.gl_render.scene.objects.DiceObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.PNode;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.ShadowMapProgram;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.ShapeShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.TerrainShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.VBOShaderProgram;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.WaterShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.mySoundPalyer;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Matrix4f;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.GL_TEXTURE4;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glViewport;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_ACTION_SHOW_TURN_INFO;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_DICE_VALUE;
import static com.cubegames.slava.cubegame.api.RestApiService.startActionMooveGameInstance;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.SHADOWMAP_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.RND_SEED__PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.UX_PIXEL_OFFSET_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.UY_PIXEL_OFFSET_PARAM_NAME;

public class GLScene {

    private final static long LAND_ANIMATION_DELAY_MS = 10000L;
    private final static float WAVE_SPEED = 0.07f;

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
    private int mShadowMapWidth;
    private int mShadowMapHeight;

    private int[] fboId;
    private int[] depthTextureId;
    private int[] renderTextureId;

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
    public int getmShadowMapWidth() {
        return mShadowMapWidth;
    }
    public void setmShadowMapWidth(int mShadowMapWidth) {
        this.mShadowMapWidth = mShadowMapWidth;
    }
    public int getmShadowMapHeight() {
        return mShadowMapHeight;
    }
    public void setmShadowMapHeight(int mShadowMapHeight) {
        this.mShadowMapHeight = mShadowMapHeight;
    }

    public int[] getFboId() {
        return fboId;
    }
    public void setFboId(int[] fboId) {
        this.fboId = fboId;
    }
    public int[] getDepthTextureId() {
        return depthTextureId;
    }
    public void setDepthTextureId(int[] depthTextureId) {
        this.depthTextureId = depthTextureId;
    }
    public int[] getRenderTextureId() {
        return renderTextureId;
    }
    public void setRenderTextureId(int[] renderTextureId) {
        this.renderTextureId = renderTextureId;
    }

    public GLAnimation zoomCameraAnimation;
    private float[] mViewMatrix = new float[16];

    public void addObject(GLSceneObject object, String name) {
        objects.put(name, object);
    }

    public void deleteObject(String name) {
        //GLSceneObject object = getObject(name);

        //if (object != null) {
            //object.clearVBOData();
            objects.remove(name);
        //}
    }

    public void clearData() {
        clearObjectsCache();
        clearShaderCache();
    }

    public void clearObjectsCache() {
        for (GLSceneObject object : objects.values())
            object.clearVBOData();

        objects.clear();
    }

    public void clearShaderCache() {
        for (GLShaderProgram program : shaders.values())
            for (GLShaderParam param : program.getParams().values())
                if (param instanceof GLShaderParamVBO)
                    ((GLShaderParamVBO)param).clearParamDataVBO();

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
                    program = new ShadowMapProgram(context, hasDepthTextureExtension);
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

    public void generateShadowFBO() {
        mShadowMapWidth = Math.round(mDisplayWidth/* * 1.5f*/);
        mShadowMapHeight = Math.round(mDisplayHeight/* * 1.5f*/);

        fboId = new int[1];
        depthTextureId = new int[1];
        renderTextureId = new int[1];

        // create a framebuffer object
        GLES20.glGenFramebuffers(1, fboId, 0);

        // create render buffer and bind 16-bit depth buffer
        GLES20.glGenRenderbuffers(1, depthTextureId, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthTextureId[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, mShadowMapWidth, mShadowMapHeight);

        // Try to use a texture depth component
        GLES20.glGenTextures(1, renderTextureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);

        // GL_LINEAR does not make sense for depth texture. However, next tutorial shows usage of GL_LINEAR and PCF. Using GL_NEAREST
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Remove artifact on the edges of the shadowmap
        GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
        GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

        if (!hasDepthTextureExtension) {
            GLES20.glTexImage2D( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mShadowMapWidth, mShadowMapHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

            // specify texture as color attachment
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTextureId[0], 0);

            // attach the texture to FBO depth attachment point
            // (not supported with gl_texture_2d)
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthTextureId[0]);
        }
        else {
            // Use a depth texture
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, mShadowMapWidth, mShadowMapHeight, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_INT, null);

            // Attach the depth texture to FBO depth attachment point
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTextureId[0], 0);
        }

        // check FBO status
        int FBOstatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if(FBOstatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
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
        if (zoomCameraAnimation != null && zoomCameraAnimation.isInProgress()) {
            float[] mMatrix = new float[16];

            zoomCameraAnimation.animate(mViewMatrix);
            Matrix.multiplyMM(mMatrix, 0, camera.getViewMatrix(), 0, mViewMatrix, 0);
            camera.setViewMatrix(mMatrix);

            //TODO: calc and set position
            /*Vector3f camera_pos = new Vector3f(camera.getCameraPosition());
            Matrix4f m = new Matrix4f(mMatrix);
            m.transform(camera_pos);
            camera.setCameraPosition(camera_pos.x, camera_pos.y, camera_pos.z);*/
        }

        for (GLSceneObject object : objects.values()) {
            /*if (object.getObjectType().equals(GLObjectType.TERRAIN_OBJECT))
                setModelMatrix(object);*/

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
        calculateFrameTime(currentTime);

        simulatePhysics(currentTime);
        calculateSceneTransformations();

        renderShadowMapBuffer();
        renderPostEffectsBuffer();
        //renderColorBuffer();
    }

    private void renderShadowMapBuffer() {
        glCullFace(GL_FRONT);

        ///glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

        glViewport(0, 0, mShadowMapWidth, mShadowMapHeight);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        GLShaderProgram program = getCachedShader(SHADOWMAP_OBJECT);
        program.useProgram();

        GLObjectType prevObject = GLObjectType.UNKNOWN_OBJECT;
        for (GLSceneObject object : objects.values()) {
            if (object.getObjectType().equals(GLObjectType.TERRAIN_OBJECT))
                continue;

            bindMVPMatrix(program, object, getLightSource().getViewMatrix(), getLightSource().getProjectionMatrix());

            if (!object.getObjectType().equals(prevObject))
                try {
                    program.linkVertexData(object.getVertexVBO());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            prevObject = object.getObjectType();

            if (object.getObjectType().equals(GLObjectType.DICE_OBJECT))
                GLES20.glDrawArrays(GL_TRIANGLES, 0, 36);
            else {
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, object.getFacesIBOPtr());
                glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, 0);
            }
        }
    }

    private void renderPostEffectsBuffer() {
        //TODO:
    }

    private void renderColorBuffer() {
        glCullFace(GL_BACK);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        GLShaderProgram program = getCachedShader(TERRAIN_OBJECT);
        program.useProgram();

        glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, renderTextureId[0]);
        program.paramByName(ACTIVE_SHADOWMAP_SLOT_PARAM_NAME).setParamValue(4);

        program.setCameraData(getCamera().getCameraPosition());

        program.setLightSourceData(getLightSource().getLightPosInEyeSpace());
        ((VBOShaderProgram)program).setLightColourValue(getLightSource().getLightColour());

        try {
            moveFactor += WAVE_SPEED * frameTime / 1000;
            moveFactor %= 1;
        }
        catch (Exception e) {
            moveFactor = 0;
        }
        program.paramByName(RND_SEED__PARAM_NAME).setParamValue(moveFactor);

        program.paramByName(UX_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / mShadowMapWidth));
        program.paramByName(UY_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / mShadowMapHeight));

        GLObjectType prevObject = GLObjectType.UNKNOWN_OBJECT;
        for (GLSceneObject object : objects.values()) {
            /*GLShaderProgram program = object.getProgram();
            program.useProgram();*/

            bindMVPMatrix(program, object, getCamera().getViewMatrix(), getCamera().getProjectionMatrix());


            float [] lightMVP = new float[16];
            float[] tempResultMatrix = new float[16];
            Matrix.multiplyMM(lightMVP, 0, getLightSource().getViewMatrix(), 0, object.getModelMatrix(), 0);
            Matrix.multiplyMM(tempResultMatrix, 0, getLightSource().getProjectionMatrix(), 0, lightMVP, 0);
            System.arraycopy(tempResultMatrix, 0, lightMVP, 0, 16);

            float bias[] = new float [] {0.5f, 0.0f, 0.0f, 0.0f,
                                         0.0f, 0.5f, 0.0f, 0.0f,
                                         0.0f, 0.0f, 0.5f, 0.0f,
                                         0.5f, 0.5f, 0.5f, 1.0f};
            float[] depthBiasMVP = new float[16];
            if (hasDepthTextureExtension){
                Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, lightMVP, 0);
                System.arraycopy(depthBiasMVP, 0, lightMVP, 0, 16);
            }
            program.paramByName(LIGHT_MVP_MATRIX_PARAM_NAME).setParamValue(lightMVP);

            ((VBOShaderProgram)program).setMaterialParams(object);
            linkVBOData(program, object, prevObject);

            prevObject = object.getObjectType();

            /** USING VBO BUFFER */
            if (object.getObjectType().equals(GLObjectType.DICE_OBJECT))
                GLES20.glDrawArrays(GL_TRIANGLES, 0, 36);
            else {
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, object.getFacesIBOPtr());
                glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, 0);
            }

            /** USING RAM BUFFER */
            /*object.getIndexData().position(0);
            glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, object.getIndexData());*/
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

    public void linkVBOData(GLShaderProgram program, GLSceneObject object, GLObjectType prevObject) {
        try {
            if (!object.getObjectType().equals(prevObject)) {
                program.linkVertexData(object.getVertexVBO());
                program.linkTexelData(object.getTexelVBO());
                program.linkNormalData(object.getNormalVBO());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void bindMVPMatrix(GLShaderProgram program, GLSceneObject object, float[] viewMatrix, float[] projectionMatrix) {
        float[] mMatrix = new float[16];

        /** set for skybox */
        /*Matrix4f mCamera = new Matrix4f(camera.getViewMatrix());
        mCamera.m30 = 0f;
        mCamera.m31 = 0f;
        mCamera.m32 = 0f;*/

        Matrix.multiplyMM(mMatrix, 0, viewMatrix, 0, object.getModelMatrix(), 0);
        if(!(program instanceof ShadowMapProgram))
            program.setMVMatrixData(mMatrix);

        Matrix.multiplyMM(mMatrix, 0, projectionMatrix, 0, mMatrix, 0);
        program.setMVPMatrixData(mMatrix);
    }

    private void setModelMatrix(GLSceneObject object) {
        /** В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.*/
        float angle = -(float)(SystemClock.uptimeMillis() % LAND_ANIMATION_DELAY_MS) / LAND_ANIMATION_DELAY_MS * 360;

        Matrix.setIdentityM(object.getModelMatrix(), 0);
        Matrix.rotateM(object.getModelMatrix(), 0, angle, 0, 1, 0);
    }

    @Override
    protected void finalize() throws Throwable {
        stopSimulation();
        clearData();

        super.finalize();
    }
}
