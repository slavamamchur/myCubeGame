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

    public GLLightSource(float [] lightPos, Vector3f lightColour, GLCamera camera) {
        mCamera = camera;
        this.lightColour = lightColour;

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
        Matrix.multiplyMV(this.lightPosInEyeSpace, 0, mCamera.getmViewMatrix(), 0, lightPosInEyeSpace, 0); //TODO: error?
    }

    public Vector3f getLightColour() {
        return lightColour;
    }
    public void setLightColour(Vector3f lightColour) {
        this.lightColour = lightColour;
    }
}
