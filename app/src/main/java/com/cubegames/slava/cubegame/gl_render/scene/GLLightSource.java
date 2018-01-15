package com.cubegames.slava.cubegame.gl_render.scene;

import android.opengl.Matrix;

import javax.vecmath.Vector3f;

public class GLLightSource {

    /** Stores a copy of the model matrix specifically for the light position. */
    // private float[] mLightModelMatrix = new float[16];
    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    // private float[] mLightPosInWorldSpace = new float[4];

    private GLCamera mCamera;
    private float[] lightPosInEyeSpace = new float[4];
    private Vector3f lightColour;
    private float[] viewMatrix;
    private float[] projectionMatrix;

    public GLLightSource(float [] lightPos, Vector3f lightColour, GLCamera camera) {
        mCamera = camera;
        this.lightColour = lightColour;

        viewMatrix = new float[16];
        projectionMatrix = new float[16];

        Matrix.setLookAtM(viewMatrix, 0, lightPos[0], lightPos[1], lightPos[2],
                                         -lightPos[0], -lightPos[1], -lightPos[2],
                                         -lightPos[0], 0f, -lightPos[2]); //TODO: error !!!

        setLightPosInEyeSpace(lightPos);
    }

    public float[] getLightPosInEyeSpace() {
        return lightPosInEyeSpace;
    }
    public void setLightPosInEyeSpace(float[] lightPosInEyeSpace) {
        /** for dynamic light */
        /*Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
        mainShader.setLightSourceData(mLightPosInEyeSpace);*/

        /** for static light*/
        Matrix.multiplyMV(this.lightPosInEyeSpace, 0, mCamera.getViewMatrix(), 0, lightPosInEyeSpace, 0);
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
    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(int width, int height) {
        float ratio;
        float left = -0.5f;
        float right = 0.5f;
        float bottom = -0.5f;
        float top = 0.5f;
        float near = 2f;
        float far = 12f;

        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.frustumM(projectionMatrix, 0, 1.1f * left, 1.1f * right, 1.1f * bottom, 1.1f * top, near, far);
    }
}
