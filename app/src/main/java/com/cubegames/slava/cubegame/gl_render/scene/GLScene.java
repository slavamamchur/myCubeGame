package com.cubegames.slava.cubegame.gl_render.scene;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import com.cubegames.slava.cubegame.gl_render.GLAnimation;
import com.cubegames.slava.cubegame.gl_render.scene.objects.DiceObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.PNode;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.ShapeShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.TerrainShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.WaterShader;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;
import com.cubegames.slava.cubegame.model.GameInstance;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Matrix4f;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawElements;
import static com.cubegames.slava.cubegame.api.RestApiService.startActionMooveGameInstance;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType;

public class GLScene {

    private final static long LAND_ANIMATION_DELAY_MS = 10000L;

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

    private void removeDice(GLSceneObject dice, final int dice_1_Value) {
        //toggleActionBarProgress(true);
        gameInstanceEntity.setStepsToGo(dice_1_Value);
        startActionMooveGameInstance(context, gameInstanceEntity);//TODO: ??? broadcast

        /*runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                showAnimatedText(String.format("%d\nSteps\nto GO", dice_1_Value));
            }
        }));*/

        //TODO: wait lock ogl draw scene ???
        //try {Thread.sleep(1000);} catch (InterruptedException e) {}
        Matrix.translateM(dice.getModelMatrix(), 0, 0, 100.1f, 0);
    }

    public void drawScene() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (isSimulating) {
            long ctime = System.currentTimeMillis();
            _world.stepSimulation(ctime - simulation_time);
            simulation_time = ctime;
        }

        for (GLSceneObject object : objects.values()) {
            GLShaderProgram program = object.getProgram();
            program.useProgram();

            /*if (object.getObjectType().equals(GLRenderConsts.GLObjectType.TERRAIN_OBJECT) ||
                object.getObjectType().equals(GLObjectType.WATER_OBJECT))
                setModelMatrix(object);*/

            GLAnimation animation = object.getAnimation();
            if (animation != null && animation.isInProgress()) {
                animation.animate(object.getModelMatrix());
            }

            if (isSimulating && object instanceof DiceObject && (((PNode)object).getTag() == 1)
                    && ((PNode)object).get_body() != null) {//TODO:check normals
                Transform tr = new Transform(new Matrix4f(new float[16]));
                float [] mat = new float[16];
                ((PNode)object).get_body().getWorldTransform(tr).getOpenGLMatrix(mat);

                if (!old_transform.equals(tr)) {
                    old_transform = tr;
                }
                else {
                    _world.removeRigidBody(((PNode)object).get_body());
                    Matrix.setIdentityM(mat, 0);
                    Matrix.translateM(mat, 0, 100f, 0f, 0);
                }

                object.setModelMatrix(mat);
            }

            bindMVPMatrix(program, object, getCamera());
            program.setCameraData(getCamera().getCameraPosition());
            program.setLightSourceData(getLightSource().getLightPosInEyeSpace());
            glBindTexture(GL_TEXTURE_2D, object.getGlTextureId());
            program.setTextureSlotData(0);

            linkVBOData(program, object);

            if(program instanceof WaterShader)
                ((WaterShader)program).setRndSeedData((int)System.currentTimeMillis() * 1.0f);

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

    public void linkVBOData(GLShaderProgram program, GLSceneObject object) {
        try {
            program.linkVertexData(object.getVertexVBO());
            program.linkTexelData(object.getTexelVBO());
            program.linkNormalData(object.getNormalVBO());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void bindMVPMatrix(GLShaderProgram program, GLSceneObject object, GLCamera camera) {
        float[] mMatrix = new float[16];

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
