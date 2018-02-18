package com.sadgames.gl3dengine.glrender.scene;

import android.opengl.Matrix;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.scene.GLAnimation.ROTATE_BY_X;
import static com.sadgames.gl3dengine.glrender.scene.GLAnimation.ROTATE_BY_Y;
import static com.sadgames.gl3dengine.glrender.scene.GLAnimation.ROTATE_BY_Z;
import static com.sadgames.sysutils.common.MathUtils.cos;
import static com.sadgames.sysutils.common.MathUtils.sin;

public class GLCamera implements GLAnimation.IAnimatedObject {

    public static final float VERTICAL_FOV = 28.0f;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 10.0f;//100

    private float[] transformMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private Vector3f cameraPosition = new Vector3f(0.0f, 0.0f, 0.0f);

    private float vfov = VERTICAL_FOV;
    private float zoomed_vfov = VERTICAL_FOV;
    private float pitch;
    private float yaw;
    private float roll;
    private float aspectRatio;

    public GLCamera(float eyeX, float eyeY, float eyeZ, float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        Matrix.setIdentityM(transformMatrix, 0);

        setCameraPosition(eyeX, eyeY, eyeZ);
    }

    public GLCamera(Vector3f cameraPosition, float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        Matrix.setIdentityM(transformMatrix, 0);

        setCameraPosition(cameraPosition);
    }

    public void setCameraPosition(float eyeX, float eyeY, float eyeZ) {
        cameraPosition.x = eyeX;
        cameraPosition.y = eyeY;
        cameraPosition.z = eyeZ;

        updateViewMatrix();
    }

    public void setCameraPosition(Vector3f cameraPosition) {
        this.cameraPosition = cameraPosition;

        updateViewMatrix();
    }

    public void updateViewMatrix() {
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, pitch, 1, 0, 0);
        Matrix.rotateM(viewMatrix, 0, yaw, 0, 1, 0);
        Matrix.rotateM(viewMatrix, 0, roll, 0, 0, 1);
        Matrix.translateM(viewMatrix, 0, -cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
    }

    public void setAspectRatio(int width, int height) {
        aspectRatio = (float) width / (float) height;
        updateProjectionMatrix();
    }

    private void updateProjectionMatrix() {
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.perspectiveM(projectionMatrix, 0 , vfov, aspectRatio, NEAR_PLANE, FAR_PLANE);
    }

    public void setPitch(float pitch) {
        if (pitch < 0 || pitch > 90)
            return;

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
    public void directSetRoll(float roll) {
        this.roll = roll;
    }

    public void directSetPitch(float pitch) {
        if (pitch < 0 || pitch > 90)
            return;

        this.pitch = pitch;
    }
    public void directSetPitchByDirection(Vector3f direction) {
        directSetPitch((float) Math.toDegrees(Math.acos(new Vector2f(direction.x, direction.z).length())));
    }

    public void directSetYaw(float yaw) {
        this.yaw = yaw;
    }
    public void directSetYawByDirection(Vector3f direction) {
        float myaw = (float) Math.toDegrees((Math.atan(direction.x / direction.z)));
        myaw = direction.z > 0 ? myaw - 180 : myaw;
        directSetYaw(-myaw);
    }

    public float getVfov() {
        return vfov;
    }
    public void setVfov(float vfov) {
        this.vfov = vfov;
        updateProjectionMatrix();
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }
    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }
    public Vector3f getCameraPosition() {
        return cameraPosition;
    }
    public float getPitch() {
        return pitch;
    }//TODO: move camera and save direction !!!
    public float getYaw() {
        return yaw;
    }
    public float getRoll() {
        return roll;
    }
    public float[] getTransformMatrix() {
        return transformMatrix;
    }
    public void setTransformMatrix(float[] transformMatrix) {
        this.transformMatrix = transformMatrix;
    }

    public Vector3f getCameraDirection() {
        return  getCameraDirection(cameraPosition);
    }
    public Vector3f getCameraDirection(Vector3f cameraPosition) {
        Vector3f direction = new Vector3f(cameraPosition);
        direction.negate();
        direction.normalize();

        /** by camera rotation */ //TODO: get direction vector and pass into shader param
        /*direction.x = (float) (Math.sin(Math.toRadians(getPitch())) * Math.cos(Math.toRadians(getYaw())));
        direction.y = (float) (Math.sin(Math.toRadians(getPitch())) * Math.sin(Math.toRadians(getYaw())));
        direction.z = (float) (Math.cos(Math.toRadians(getPitch())));

        direction.x = cos(glm::radians(pitch)) * cos(glm::radians(yaw));
        direction.y = sin(glm::radians(pitch));
        direction.z = cos(glm::radians(pitch)) * sin(glm::radians(yaw));

        #apply yaw (around y)
        x = x * cos(yaw) - z * sin(yaw)
        z = z * cos(yaw) + x * sin(yaw)

        #apply pitch (around x)
        y = y * cos(roll) - z * sin(roll)
        z = z * cos(roll) + y * sin(roll)

        #apply roll (around z)
        x = x * cos(pitch) - y * sin(pitch)
        y = y * cos(pitch) + x * sin(pitch) */

        return direction;
    }

    public void rotateAroundViewPoint(float angle) {
        Vector3f cameraPos = getCameraPosition();
        Vector3f direction = getCameraDirection();

        cameraPos.x = cos(angle) * (cameraPos.x - direction.x) - sin(angle) * (cameraPos.z - direction.z) + direction.x;
        cameraPos.z = sin(angle) * (cameraPos.x - direction.x) + cos(angle) * (cameraPos.z - direction.z) + direction.z;

        directSetYawByDirection(getCameraDirection(cameraPos));
    }

    @Override
    public float[] getTransformationMatrix() {
        return getTransformMatrix();
    }

    @Override
    public void setPosition(Vector3f position) {
        setCameraPosition(position);
    }

    @Override
    public void setRotation(float angle, short rotationAxesMask) {
        if ((rotationAxesMask & ROTATE_BY_X) != 0) pitch += angle;
        if ((rotationAxesMask & ROTATE_BY_Y) != 0) yaw += angle;
        if ((rotationAxesMask & ROTATE_BY_Z) != 0) roll += angle;

        updateViewMatrix();
    }

    @Override
    public void setZoomLevel(float zoomLevel) {
        setVfov(zoomed_vfov / zoomLevel);
    }

    @Override
    public void onAnimationEnd() {
        zoomed_vfov = vfov;
    }
}