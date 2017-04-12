package com.cubegames.slava.cubegame.mapgl;

import android.opengl.Matrix;

public class GLLightSource {

    /** Stores a copy of the model matrix specifically for the light position. */
    // private float[] mLightModelMatrix = new float[16];
    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    // private float[] mLightPosInWorldSpace = new float[4];

    private float[] lightPosInEyeSpace = new float[4];

    public GLLightSource(float [] lightPos, GLCamera camera) {
        /** for dynamic light */
        /*Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
        mainShader.setLightSourceData(mLightPosInEyeSpace);*/

        /** for static light*/
        Matrix.multiplyMV(lightPosInEyeSpace, 0, camera.getmViewMatrix(), 0, lightPos, 0);
    }

    public float[] getLightPosInEyeSpace() {
        return lightPosInEyeSpace;
    }
}
