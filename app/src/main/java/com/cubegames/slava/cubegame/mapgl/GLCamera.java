package com.cubegames.slava.cubegame.mapgl;

import android.opengl.Matrix;

public class GLCamera {

    private float[] viewMatrix;
    private float[] projectionMatrix;
    private float[] cameraPosition;

    public GLCamera(float eyeX,
                    float eyeY,
                    float eyeZ,

                    float centerX,
                    float centerY,
                    float centerZ,

                    float upX,
                    float upY,
                    float upZ) {

        viewMatrix = new float[16];
        projectionMatrix = new float[16];

        initCamera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void initCamera(
            float eyeX,
            float eyeY,
            float eyeZ,

            float centerX,
            float centerY,
            float centerZ,

            float upX,
            float upY,
            float upZ)
    {
        cameraPosition = new float[] {eyeX, eyeY, eyeZ, 1.0f};
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void setProjectionMatrix(int width, int height) {
        float ratio;
        float left = -0.5f;
        float right = 0.5f;
        float bottom = -0.5f;
        float top = 0.5f;
        float near = 2;
        float far = 12;

        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public float[] getmViewMatrix() {
        return viewMatrix;
    }
    public void setViewMatrix(float[] viewMatrix) {
        this.viewMatrix = viewMatrix;
    }
    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }
    public float[] getCameraPosition() {
        return cameraPosition;
    }
}
