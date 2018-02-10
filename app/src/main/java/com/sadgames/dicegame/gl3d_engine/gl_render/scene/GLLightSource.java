package com.sadgames.dicegame.gl3d_engine.gl_render.scene;

import android.opengl.Matrix;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.gl3d_engine.gl_render.scene.GLCamera.FAR_PLANE;

public class GLLightSource {

    /** Stores a copy of the model matrix specifically for the light position. */
    // private float[] mLightModelMatrix = new float[16];

    private GLCamera mCamera;
    private float[] lightPosInModelSpace = new float[4];
    private float[] lightPosInEyeSpace = new float[4];
    private Vector3f lightColour;
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private int width;
    private int height;

    private ShadowBox shadowBox;

    public GLLightSource(float [] lightPos, Vector3f lightColour, GLCamera camera) {
        mCamera = camera;
        this.lightColour = lightColour;

        setLightPosInModelSpace(lightPos);
        Matrix.setIdentityM(viewMatrix, 0);
    }

    public void updateViewProjectionMatrix(int width, int height) {
        this.width = width;
        this.height = height;

        /** for shadowbox implementation
        ///shadowBox = new ShadowBox(viewMatrix, mCamera, (float) width / (float) height); //TODO: empty viewMatrix ??? */

        Vector3f lightDirection = new Vector3f(-lightPosInModelSpace[0], -lightPosInModelSpace[1], -lightPosInModelSpace[2]);
        setViewMatrix(lightDirection, null);

        /** for shadowbox implementation
        ///shadowBox.update(); */

        setProjectionMatrix(width, height);
        /** for shadowbox implementation
        ///setViewMatrix(width, height, lightDirection, shadowBox); */
    }

    public float[] getLightPosInEyeSpace() {
        return lightPosInEyeSpace;
    }
    public void setLightPosInEyeSpace() {
        /** for dynamic light
        /*Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
        mainShader.setLightSourcePosition(mLightPosInEyeSpace);*/

        /** for static light*/
        Matrix.multiplyMV(this.lightPosInEyeSpace, 0, mCamera.getViewMatrix(), 0, lightPosInModelSpace, 0);
    }

    public void setLightPosInModelSpace(float[] lightPosInModelSpace) {
        this.lightPosInModelSpace = lightPosInModelSpace;
        setLightPosInEyeSpace();
    }
    public float[] getLightPosInModelSpace() {
        return lightPosInModelSpace;
    }

    public Vector3f getLightColour() {
        return lightColour;
    }
    public void setLightColour(Vector3f lightColour) {
        this.lightColour = lightColour;
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }
    public void setViewMatrix(Vector3f direction, ShadowBox shadowBox) {
        Matrix.setIdentityM(viewMatrix, 0);
        Vector3f center = shadowBox != null ? shadowBox.getCenter() : new Vector3f(lightPosInModelSpace);
        center.negate();

        direction.normalize();
        float pitch = (float) Math.toDegrees(Math.acos(new Vector2f(direction.x, direction.z).length()));
        Matrix.rotateM(viewMatrix, 0, pitch, 1, 0, 0);

        float yaw = (float) Math.toDegrees((Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        Matrix.rotateM(viewMatrix, 0, -yaw, 0, 1, 0);

        Matrix.translateM(viewMatrix, 0, center.x, center.y, center.z);

        /** classic opengl viewmatrix
        Matrix.setLookAtM(viewMatrix, 0, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2],
                -lightPosInModelSpace[0], -lightPosInModelSpace[1], -lightPosInModelSpace[2],
                -lightPosInModelSpace[0], 0f, -lightPosInModelSpace[2]);*/

        /*Matrix.setLookAtM(viewMatrix, 0, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2],
                center.x, center.y, center.z,
                0, 0, 1);*/
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }
    public void setProjectionMatrix(int width, int height) {
        float ratio;
        float left = -0.5f;
        float right = 0.5f;
        float bottom = -0.5f;
        float top = 0.5f;
        float near = -2.5f;//2
        float far = 7f;//12

        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        left *= 1.5f;
        right *= 1.5f;
        bottom *= 1.5f;
        top *= 1.5f;

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.orthoM(projectionMatrix, 0, -1.75f * ratio, 1.75f * ratio, -1.75f, 1.75f, 1/*NEAR_PLANE*/, FAR_PLANE);
        /** for shadowbox implementation
        ///Matrix.orthoM(projectionMatrix, 0, shadowBox.getMinX(), shadowBox.getMaxX(), shadowBox.getMinY(), shadowBox.getMaxY(), shadowBox.getMinZ(), shadowBox.getMaxZ());

        /** for point and spot lights
        //Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
        //Matrix.perspectiveM(projectionMatrix, 0 , VERTICAL_FOV, ratio, NEAR_PLANE, FAR_PLANE); */
    }

    /** for shadowbox implementation
    private void myOrthoM(float width, float height, float length) {
        Matrix.setIdentityM(projectionMatrix, 0);
        projectionMatrix[0]  =  2f / (width);
        projectionMatrix[5]  =  2f / (height);
        projectionMatrix[10] = -2f / (length);
    } */

}
