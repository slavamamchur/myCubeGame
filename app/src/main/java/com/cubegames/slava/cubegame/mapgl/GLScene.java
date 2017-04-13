package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

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

public class GLScene {

    private final static long ANIMATION_DELAY_MS = 10000L;

    private Context context;
    private GLCamera camera;
    private GLLightSource lightSource;

    private Map<String, GLSceneObject> objects = new HashMap<>();

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

    public void addObject(GLSceneObject object, String name) {
        objects.put(name, object);
    }

    public void deleteObject(String name) {
        GLSceneObject object = getObject(name);

        if (object != null) {
            object.clearVBOData();
            objects.remove(name);
        }
    }

    public void clearScene() {
        for (GLSceneObject object : objects.values()) {
            object.clearVBOData();
        }

        objects.clear();
    }

    public GLSceneObject getObject(String name) {
        return objects.get(name);
    }

    public void drawScene() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (GLSceneObject object : objects.values()) {
            GLShaderProgram program = object.getProgram();
            program.useProgram();

            if (object.getObjectType().equals(GLRenderConsts.GLObjectType.TERRAIN_OBJECT))
                setModelMatrix(object);

            program.bindMatrix(object, getCamera());

            if (program instanceof TerrainShader) {
                ((TerrainShader) program).setCameraData(getCamera().getCameraPosition());
                ((TerrainShader) program).setLightSourceData(getLightSource().getLightPosInEyeSpace());

                try {
                    ((TerrainShader) program).linkVBOData(object);

                    glBindTexture(GL_TEXTURE_2D, object.getGlTextureId());
                    ((TerrainShader) program).setTextureSlotData(0);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, object.getFacesIBOPtr());
            glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, 0);
        }
    }

    private void setModelMatrix(GLSceneObject object) {
        /** В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.*/
        float angle = -(float)(SystemClock.uptimeMillis() % ANIMATION_DELAY_MS) / ANIMATION_DELAY_MS * 360;

        Matrix.setIdentityM(object.getModelMatrix(), 0);
        Matrix.rotateM(object.getModelMatrix(), 0, angle, 0, 1, 0);
    }

}
