package com.sadgames.sysutils.common;


import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Matrix4f;

public class MathUtils {

    public static float sin(float degree) {
        return (float) Math.sin(Math.toRadians(degree));
    }

    public static float cos(float degree) {
        return (float) Math.cos(Math.toRadians(degree));
    }

    public static float[] crossProduct(float[] u, float[] v) {
        return new float[] { (u[1] * v[2]) - (u[2] * v[1]),
                (u[2] * v[0]) - (u[0] * v[2]),
                (u[0] * v[1]) - (u[1] * v[0]) };
    }

    public static float[] getOpenGlMatrix(Matrix4f matrix) {
        float[] glMatrix = new float[16];
        Transform transform = new Transform(matrix);
        transform.getOpenGLMatrix(glMatrix);

        return glMatrix;
    }
}
