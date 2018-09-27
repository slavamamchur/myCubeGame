package com.sadgames.gl3dengine.glrender.scene.camera;

public class FPVCamera extends GLCamera {

    public FPVCamera(float eyeX, float eyeY, float eyeZ, float pitch, float yaw, float roll) {
        super(eyeX, eyeY, eyeZ, pitch, yaw, roll);
    }

    @Override
    public void rotateX(float angle) {
        directSetPitch(pitch + angle);
    }

    @Override
    public void rotateY(float angle) {
        directSetYaw(yaw + angle);
    }

    @Override
    public void rotateZ(float angle) {
        directSetRoll(roll + angle);
    }
}
