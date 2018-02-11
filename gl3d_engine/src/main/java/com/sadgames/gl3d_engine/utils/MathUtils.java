package com.sadgames.gl3d_engine.utils;

import android.opengl.Matrix;

import javax.vecmath.Vector3f;

public class MathUtils {

    public static float sin(float degree) {
        return (float) Math.sin(Math.toRadians(degree));
    }

    public static float cos(float degree) {
        return (float) Math.cos(Math.toRadians(degree));
    }

    public static Vector3f mulMV(float[] matrix, float[] vector) {
        float[] result = new float[4];
        Matrix.multiplyMV(result, 0, matrix, 0, vector, 0);

        return new Vector3f(result[0], result[1], result[2]);
    }

    public static float[] crossProduct(float[] u, float[] v) {
        return new float[] { (u[1] * v[2]) - (u[2] * v[1]),
                (u[2] * v[0]) - (u[0] * v[2]),
                (u[0] * v[1]) - (u[1] * v[0]) };
    }
}
