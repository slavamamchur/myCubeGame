package com.sadgames.gl3dengine.glrender.scene.camera;

import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;
import com.sadgames.sysutils.common.MathUtils;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_X;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Y;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Z;

public abstract class GLCamera implements GLAnimation.IAnimatedObject {

    public static final float VERTICAL_FOV = 28.0f;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 100.0f;//10 ???

    protected float[] transformMatrix = new float[16];
    protected float[] viewMatrix = new float[16];
    protected float[] projectionMatrix = new float[16];
    protected Vector3f cameraPosition = new Vector3f(0.0f, 0.0f, 0.0f);

    protected float vfov = VERTICAL_FOV;
    protected float zoomed_vfov = VERTICAL_FOV;
    protected float pitch;
    protected float yaw;
    protected float roll;
    protected float aspectRatio;

    public GLCamera(float eyeX, float eyeY, float eyeZ, float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        MathUtils.setIdentityM(transformMatrix, 0);

        setCameraPosition(eyeX, eyeY, eyeZ);
    }

    @SuppressWarnings("unused")
    public GLCamera(Vector3f cameraPosition, float pitch, float yaw, float roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        MathUtils.setIdentityM(transformMatrix, 0);

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
        MathUtils.rotateM(viewMatrix, pitch, yaw, roll);
        MathUtils.translateM(viewMatrix, 0, -cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
    }

    public void setAspectRatio(int width, int height) {
        aspectRatio = (float) width / (float) height;
        updateProjectionMatrix();
    }

    private void updateProjectionMatrix() {
        MathUtils.setIdentityM(projectionMatrix, 0);
        MathUtils.perspectiveM(projectionMatrix, 0 , vfov, aspectRatio, NEAR_PLANE, FAR_PLANE);
    }

    @SuppressWarnings("unused")
    public void setPitch(float pitch) {
        directSetPitch(pitch);
        updateViewMatrix();
    }

    @SuppressWarnings("unused")
    public void setYaw(float yaw) {
        directSetYaw(yaw);
        updateViewMatrix();
    }

    @SuppressWarnings("unused")
    public void setRoll(float roll) {
        directSetRoll(roll);
        updateViewMatrix();
    }

    public void directSetYaw(float yaw) {
        this.yaw = yaw;
    }

    public void directSetRoll(float roll) {
        this.roll = roll;
    }

    public void directSetPitch(float pitch) {
            this.pitch = pitch;
    }

    @SuppressWarnings("unused")
    public void directSetPitchByDirection(Vector3f direction) {
        directSetPitch((float) Math.toDegrees(Math.acos(new Vector2f(direction.x, direction.z).length())));
    }

    @SuppressWarnings("all")
    public void directSetYawByDirection(Vector3f direction) {
        float myaw = (float) Math.toDegrees((Math.atan(direction.x / direction.z)));
        myaw = direction.z > 0 ? myaw - 180 : myaw;
        directSetYaw(-myaw);
    }

    @SuppressWarnings("unused") public float getVfov() {
        return vfov;
    }
    public void setVfov(float vfov) {
        this.vfov = vfov;
        updateProjectionMatrix();
    }

    @SuppressWarnings("all") public float[] getViewMatrix() {
        return viewMatrix;
    }

    @SuppressWarnings("all") public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    @SuppressWarnings("all") public Vector3f getCameraPosition() {
        return cameraPosition;
    }

    @SuppressWarnings("all") public float getPitch() {
        return pitch;
    }

    @SuppressWarnings("unused") public float getYaw() {
        return yaw;
    }

    @SuppressWarnings("unused") public float getRoll() {
        return roll;
    }

    @SuppressWarnings("all") public float[] getTransformMatrix() {
        return transformMatrix;
    }

    @SuppressWarnings("unused") public void setTransformMatrix(float[] transformMatrix) {
        this.transformMatrix = transformMatrix;
    }

    @SuppressWarnings("all") public Vector3f getCameraDirection() {
        return  getCameraDirection(cameraPosition);
    }

    @SuppressWarnings("all")
    public Vector3f getCameraDirection(Vector3f cameraPosition) {
        Vector3f direction = new Vector3f(cameraPosition);
        direction.negate();
        direction.normalize();

        return direction;
    }

    @SuppressWarnings("unused")
    public Vector3f getCameraDirectionByAngles() {
        Vector3f direction = new Vector3f();

        direction.x = (float) (Math.sin(Math.toRadians(getPitch())) * Math.cos(Math.toRadians(getYaw())));
        direction.y = (float) (Math.sin(Math.toRadians(getPitch())) * Math.sin(Math.toRadians(getYaw())));
        direction.z = (float) (Math.cos(Math.toRadians(getPitch())));

        /** method #2 */
        /*direction.x = cos(glm::radians(pitch)) * cos(glm::radians(yaw));
        direction.y = sin(glm::radians(pitch));
        direction.z = cos(glm::radians(pitch)) * sin(glm::radians(yaw));*/

        /** method #3 */
        /*#apply yaw (around y)
        x = x * cos(yaw) - z * sin(yaw)
        z = z * cos(yaw) + x * sin(yaw)

        #apply pitch (around x)
        y = y * cos(roll) - z * sin(roll)
        z = z * cos(roll) + y * sin(roll)

        #apply roll (around z)
        x = x * cos(pitch) - y * sin(pitch)
        y = y * cos(pitch) + x * sin(pitch)*/

        return direction;
    }

    public abstract void rotateX(float angle);
    public abstract void rotateY(float angle);
    public abstract void rotateZ(float angle);

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
