package com.sadgames.gl3dengine.glrender.scene.camera;

import javax.vecmath.Vector3f;

import static com.sadgames.sysutils.common.MathUtils.cos;
import static com.sadgames.sysutils.common.MathUtils.sin;

public class FixedIsometricCamera extends GLCamera {

    public FixedIsometricCamera(float eyeX, float eyeY, float eyeZ, float pitch, float yaw, float roll) {
        super(eyeX, eyeY, eyeZ, pitch, yaw, roll);
    }

    @Override
    public void rotateX(float angle) {
        Vector3f cameraPos = new Vector3f(getCameraPosition());
        Vector3f direction = getCameraDirection();

        cameraPos.y = cos(angle) * (cameraPos.y - direction.y) - sin(angle) * (cameraPos.z - direction.z) + direction.y;
        cameraPos.z = sin(angle) * (cameraPos.y - direction.y) + cos(angle) * (cameraPos.z - direction.z) + direction.z;

        float oldPitch = pitch;
        directSetPitchByDirection(getCameraDirection(cameraPos));

        if (pitch >= 1.5f && pitch <= 90.0f)
            cameraPosition = cameraPos;
        else
            pitch = oldPitch;
    }

    @Override
    public void rotateY(float angle) {
        Vector3f cameraPos = getCameraPosition();
        Vector3f direction = getCameraDirection();

        cameraPos.x = cos(angle) * (cameraPos.x - direction.x) - sin(angle) * (cameraPos.z - direction.z) + direction.x;
        cameraPos.z = sin(angle) * (cameraPos.x - direction.x) + cos(angle) * (cameraPos.z - direction.z) + direction.z;

        directSetYawByDirection(getCameraDirection(cameraPos));
    }

    @Override
    public void rotateZ(float angle) {

    }
}
