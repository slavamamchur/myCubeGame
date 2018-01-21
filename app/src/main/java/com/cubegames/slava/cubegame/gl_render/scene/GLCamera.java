package com.cubegames.slava.cubegame.gl_render.scene;

import android.opengl.Matrix;

import javax.vecmath.Vector3f;

public class GLCamera {

    public static final float VERT_FOV = 27.0f;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 100.0f;

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] cameraPosition = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    private float pitch;
    private float yaw;
    private float roll;

    public GLCamera(float eyeX, float eyeY, float eyeZ, float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;

        setCameraPosition(eyeX, eyeY, eyeZ);
    }

    public void setCameraPosition(float eyeX, float eyeY, float eyeZ) {
        cameraPosition[0] = eyeX;
        cameraPosition[1] = eyeY;
        cameraPosition[2] = eyeZ;

        updateViewMatrix();
    }

    private void updateViewMatrix() {
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, pitch, 1, 0, 0);
        Matrix.rotateM(viewMatrix, 0, yaw, 0, 1, 0);
        Matrix.rotateM(viewMatrix, 0, roll, 0, 0, 1);
        Matrix.translateM(viewMatrix, 0, -cameraPosition[0], -cameraPosition[1], -cameraPosition[2]);
    }

    public void setProjectionMatrix(int width, int height) {
        float aspectRatio = (float) width / (float) height;

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.perspectiveM(projectionMatrix, 0 , VERT_FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        updateViewMatrix();
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        updateViewMatrix();
    }

    public void setRoll(float roll) {
        this.roll = roll;
        updateViewMatrix();
    }

    /*private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }*/

    public float[] getViewMatrix() {
        return viewMatrix;
    }
    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }
    public float[] getCameraPosition() {
        return cameraPosition;
    }
    public Vector3f getCameraPosition3f() {
        return new Vector3f(cameraPosition);
    }
    public float getPitch() {
        return pitch;
    }
    public float getYaw() {
        return yaw;
    }
    public float getRoll() {
        return roll;
    }
}
