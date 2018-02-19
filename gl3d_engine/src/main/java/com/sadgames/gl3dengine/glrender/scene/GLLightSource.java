package com.sadgames.gl3dengine.glrender.scene;

import com.sadgames.sysutils.common.MathUtils;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.scene.GLCamera.FAR_PLANE;

public class GLLightSource {

    /** Stores a copy of the model matrix specifically for the light position. */
    // private float[] mLightModelMatrix = new float[16];

    private GLCamera mCamera;
    private SysUtilsWrapperInterface sysUtilsWrapper;
    private float[] lightPosInModelSpace = new float[4];
    private float[] lightPosInEyeSpace = new float[4];
    private Vector3f lightColour;
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private int width;
    private int height;

    GLLightSource(float[] lightPos, Vector3f lightColour, GLCamera camera, SysUtilsWrapperInterface sysUtilsWrapper) {
        this.mCamera = camera;
        this.lightColour = lightColour;
        this.sysUtilsWrapper = sysUtilsWrapper;

        setLightPosInModelSpace(lightPos);
        MathUtils.setIdentityM(viewMatrix, 0);
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    @SuppressWarnings("all")
    public void updateViewProjectionMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        Vector3f lightDirection = new Vector3f(-lightPosInModelSpace[0], -lightPosInModelSpace[1], -lightPosInModelSpace[2]);

        setViewMatrix(lightDirection);
        setProjectionMatrix(width, height);
    }

    public float[] getLightPosInEyeSpace() {
        return lightPosInEyeSpace;
    }

    public void setLightPosInEyeSpace() {
        /** for dynamic light */
        /*Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);*/

        /** for static light*/
        Vector3f transformedLightPos = sysUtilsWrapper.mulMV(mCamera.getViewMatrix(), lightPosInModelSpace);
        lightPosInEyeSpace[0] = transformedLightPos.x;
        lightPosInEyeSpace[1] = transformedLightPos.y;
        lightPosInEyeSpace[2] = transformedLightPos.z;
    }

    public void setLightPosInModelSpace(float[] lightPosInModelSpace) {
        this.lightPosInModelSpace = lightPosInModelSpace;
        setLightPosInEyeSpace();
    }

    @SuppressWarnings("unused") public float[] getLightPosInModelSpace() {
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

    public void setViewMatrix(Vector3f direction) {
        MathUtils.setIdentityM(viewMatrix, 0);

        /** set view matrix via pitch/roll */
        /*Vector3f center = new Vector3f(direction);
        direction.normalize();

        float pitch = (float) Math.toDegrees(Math.acos(new Vector2f(direction.x, direction.z).length()));
        float yaw = (float) Math.toDegrees((Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        MathUtils.rotateM(viewMatrix, pitch, -yaw, 0.0f);
        MathUtils.translateM(viewMatrix, 0, center.x, center.y, center.z);*/

        /** classic opengl view matrix */
        MathUtils.setLookAtM(viewMatrix, 0, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2],
                direction.x, direction.y, direction.z,
                0f, 1f, 0f);
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(int width, int height) {
        float ratio;

        /** for point and spot lights */
        //float left = -0.5f;
        //float right = 0.5f;
        //float bottom = -0.5f;
        //float top = 0.5f;
        //float near = -2.5f;//2
        //float far = 7f;//12

        if (width > height) {
            ratio = (float) width / height;

            /** for point and spot lights */
            //left *= ratio;
            //right *= ratio;
        } else {
            ratio = (float) height / width;

            /** for point and spot lights */
            //bottom *= ratio;
            //top *= ratio;
        }

        /** for point and spot lights */
        //left *= 1.5f;
        //right *= 1.5f;
        //bottom *= 1.5f;
        //top *= 1.5f;

        MathUtils.setIdentityM(projectionMatrix, 0);
        MathUtils.orthoM(projectionMatrix, 0, -1.75f * ratio, 1.75f * ratio, -1.75f, 1.75f, 1/*NEAR_PLANE*/, FAR_PLANE);

        /** for point and spot lights */
        //MathUtils.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
        //MathUtils.perspectiveM(projectionMatrix, 0 , VERTICAL_FOV, ratio, NEAR_PLANE, FAR_PLANE);
    }

}
