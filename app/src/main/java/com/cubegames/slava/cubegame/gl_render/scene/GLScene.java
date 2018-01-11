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

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawElements;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_ACTION_SHOW_TURN_INFO;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_DICE_VALUE;
import static com.cubegames.slava.cubegame.api.RestApiService.startActionMooveGameInstance;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.RND_SEED__PARAM_NAME;

public class GLScene {

    private final static long LAND_ANIMATION_DELAY_MS = 10000L;
    private final static float WAVE_SPEED = 0.03f;

    private Context context;
    private GLCamera camera;
    private GLLightSource lightSource;
    private long simulation_time = 0;
    private boolean isSimulating = false;
    private DiscreteDynamicsWorld _world;
    private GameInstance gameInstanceEntity = null;

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

    private void removeDice(DiceObject dice, Transform transform) {
        //toggleActionBarProgress(true);
        gameInstanceEntity.setStepsToGo(dice.getTopFaceDiceValue(transform, camera.getmViewMatrix()));

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


    /** Collision example --------------------------------------------
    /*const btCollisionObject* obA = contactManifold->getBody0();
            const btCollisionObject* obB = contactManifold->getBody1();

    //6
    PNode* pnA = (__bridge PNode*)obA->getUserPointer();
    PNode* pnB = (__bridge PNode*)obB->getUserPointer();*/

    private int oldNumContacts = 0;
    private long old_time = System.currentTimeMillis();
    private long old_frame_time = 0;
    private float moveFactor = 0;
    private long frameTime = 0;

    public long getFrameTime() {
        return frameTime;
    }

    public void drawScene() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        long currentTime = System.currentTimeMillis();
        frameTime = old_frame_time == 0 ? 0 : currentTime - old_frame_time;
        old_frame_time = currentTime;

        if (isSimulating) {
            _world.stepSimulation(currentTime - simulation_time);
            simulation_time = currentTime;

            for (int i = 0; i < _world.getDispatcher().getNumManifolds(); i++)
                if (_world.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() > 0 && _world.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() != oldNumContacts) {
                    mySoundPalyer.play(context, R.raw.dice_2);

                    oldNumContacts = _world.getDispatcher().getManifoldByIndexInternal(i).getNumContacts();
                    old_time = currentTime; //System.currentTimeMillis();
                }
                else if ((_world.getDispatcher().getManifoldByIndexInternal(i).getNumContacts() == 0))
                        mySoundPalyer.stop();
                else if (/*System.currentTimeMillis()*/ currentTime - old_time > 150)
                        mySoundPalyer.stop();

        }

        GLShaderProgram program = getCachedShader(TERRAIN_OBJECT);
        program.useProgram();

        if (zoomCameraAnimation != null && zoomCameraAnimation.isInProgress()) {
            float[] mMatrix = new float[16];

            zoomCameraAnimation.animate(mViewMatrix);
            Matrix.multiplyMM(mMatrix, 0, camera.getmViewMatrix(), 0, mViewMatrix, 0);
            camera.setViewMatrix(mMatrix);

            //TODO: calc and set position
            /*Vector3f camera_pos = new Vector3f(camera.getCameraPosition());
            Matrix4f m = new Matrix4f(mMatrix);

            m.transform(camera_pos);
            camera.setCameraPosition(camera_pos.x, camera_pos.y, camera_pos.z);*/
        }

        program.setCameraData(getCamera().getCameraPosition());

        program.setLightSourceData(getLightSource().getLightPosInEyeSpace());

        GLObjectType prevObject = GLObjectType.UNKNOWN_OBJECT;
        for (GLSceneObject object : objects.values()) {
//            GLShaderProgram program = object.getProgram();
//            program.useProgram();

            /*if (object.getObjectType().equals(GLObjectType.TERRAIN_OBJECT))
                setModelMatrix(object);*/

            GLAnimation animation = object.getAnimation();
            if (animation != null && animation.isInProgress()) {
                animation.animate(object.getModelMatrix());
            }

            if (isSimulating && object instanceof DiceObject && (((PNode)object).getTag() == 1)
                    && ((PNode)object).get_body() != null) {
                Transform tr = new Transform(new Matrix4f(new float[16]));
                float [] mat = new float[16];
                ((PNode)object).get_body().getWorldTransform(tr).getOpenGLMatrix(mat);

                if (!old_transform.equals(tr)) {
                    old_transform = tr;
                }
                else {
                    if (((PNode)object).get_body() != null) {
                        _world.removeRigidBody(((PNode) object).get_body());
                        ((PNode)object).set_body(null);
                        removeDice((DiceObject)object, tr);
                    }
                    Matrix.setIdentityM(mat, 0);
                    Matrix.translateM(mat, 0, 100f, 0f, 0);
                }

                object.setModelMatrix(mat);
            }

            bindMVPMatrix(program, object, getCamera());

            ((VBOShaderProgram)program).setMaterialParams(object);

            linkVBOData(program, object, prevObject);
            prevObject = object.getObjectType();


            try {
                moveFactor += WAVE_SPEED * frameTime / 1000;
                moveFactor %= 1;
            }
            catch (Exception e) {
                moveFactor = 0;
            }
            program.paramByName(RND_SEED__PARAM_NAME).setParamValue(moveFactor);

            /** USING VBO BUFFER */
            if (object.getObjectType().equals(GLObjectType.DICE_OBJECT))
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
            else {
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, object.getFacesIBOPtr());
                glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, 0);
            }

            /** USING RAM BUFFER */
            /*object.getIndexData().position(0);
            glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, object.getIndexData());*/
        }
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

    public void bindMVPMatrix(GLShaderProgram program, GLSceneObject object, GLCamera camera) {
        float[] mMatrix = new float[16];

        /** set for skybox */
        /*Matrix4f mCamera = new Matrix4f(camera.getmViewMatrix());
        mCamera.m30 = 0f;
        mCamera.m31 = 0f;
        mCamera.m32 = 0f;*/

        Matrix.multiplyMM(mMatrix, 0, camera.getmViewMatrix(), 0, object.getModelMatrix(), 0);
        program.setMVMatrixData(mMatrix);

        Matrix.multiplyMM(mMatrix, 0, camera.getProjectionMatrix(), 0, mMatrix, 0);
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
