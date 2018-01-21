package com.cubegames.slava.cubegame.gl_render.scene;

import android.opengl.Matrix;

import javax.vecmath.Vector3f;

public class GLLightSource {

    /** Stores a copy of the model matrix specifically for the light position. */
    // private float[] mLightModelMatrix = new float[16];

    private GLCamera mCamera;
    private float[] lightPosInModelSpace = new float[4];
    private float[] lightPosInEyeSpace = new float[4];
    private Vector3f lightColour;
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];

    public GLLightSource(float [] lightPos, Vector3f lightColour, GLCamera camera) {
        mCamera = camera;
        this.lightColour = lightColour;
        setLightPosInModelSpace(lightPos);
    }

    public void updateViewProjectionMatrix(int width, int height) {
        setViewMatrix(width, height);
        setProjectionMatrix(width, height);
    }

    public float[] getLightPosInEyeSpace() {
        return lightPosInEyeSpace;
    }
    public void setLightPosInEyeSpace() {
        /** for dynamic light */
        /*Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
        mainShader.setLightSourceData(mLightPosInEyeSpace);*/

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
    public void setViewMatrix(int width, int height) {
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2],
                -lightPosInModelSpace[0], -lightPosInModelSpace[1], -lightPosInModelSpace[2],
                -lightPosInModelSpace[0], 0f, -lightPosInModelSpace[2]);
    }

    /*private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
        direction.normalise();
        center.negate();
        lightViewMatrix.setIdentity();
        float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
        Matrix4f.rotate(pitch, new Vector3f(1, 0, 0), lightViewMatrix, lightViewMatrix);
        float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        Matrix4f.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0), lightViewMatrix,
                lightViewMatrix);
        Matrix4f.translate(center, lightViewMatrix, lightViewMatrix);
    }*/

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
        float far = 100f;//12f;

        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.frustumM(projectionMatrix, 0, 1.1f * left, 1.1f * right, 1.1f * bottom, 1.1f * top, near, far);
        //Matrix.orthoM(projectionMatrix, 0, 1.1f * left, 1.1f * right, 1.1f * bottom, 1.1f * top, near, far);
    }
}
