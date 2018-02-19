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

    public static void rotateM(float[] target, float pitch, float yaw, float roll) {
        Matrix4f transformer = new Matrix4f();
        Matrix4f transformingObject = new Matrix4f();
        transformingObject.setIdentity();

        transformer.rotX((float) Math.toRadians(pitch));
        transformingObject.mul(transformer);

        transformer.rotY((float) Math.toRadians(yaw));
        transformingObject.mul(transformer);

        transformer.rotZ((float) Math.toRadians(roll));
        transformingObject.mul(transformer);

        System.arraycopy(getOpenGlMatrix(transformingObject), 0, target, 0, 16);
    }

    public static void setIdentityM(float[] sm, int smOffset) {
        for (int i=0 ; i<16 ; i++) {
            sm[smOffset + i] = 0;
        }
        for(int i = 0; i < 16; i += 5) {
            sm[smOffset + i] = 1.0f;
        }
    }

    public static void translateM(
            float[] m, int mOffset,
            float x, float y, float z) {
        for (int i=0 ; i<4 ; i++) {
            int mi = mOffset + i;
            m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
        }
    }

    public static void perspectiveM(float[] m, int offset,
                                    float fovy, float aspect, float zNear, float zFar) {
        float f = 1.0f / (float) Math.tan(fovy * (Math.PI / 360.0));
        float rangeReciprocal = 1.0f / (zNear - zFar);

        m[offset + 0] = f / aspect;
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;

        m[offset + 4] = 0.0f;
        m[offset + 5] = f;
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;

        m[offset + 8] = 0.0f;
        m[offset + 9] = 0.0f;
        m[offset + 10] = (zFar + zNear) * rangeReciprocal;
        m[offset + 11] = -1.0f;

        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
        m[offset + 15] = 0.0f;
    }


}
