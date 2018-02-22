package com.sadgames.gl3dengine.glrender.scene.camera;

public class FPVCamera extends GLCamera {

    public FPVCamera(float eyeX, float eyeY, float eyeZ, float pitch, float yaw, float roll) {
        super(eyeX, eyeY, eyeZ, pitch, yaw, roll);
    }

    @Override
    public void rotateXAroundViewPoint(float angle) {
        directSetPitch(pitch + angle);
    }

    @Override
    public void rotateYAroundViewPoint(float angle) {
        directSetYaw(yaw + angle);
    }

    @Override
    public void rotateZAroundViewPoint(float angleX, float angleY) {

    }
}
