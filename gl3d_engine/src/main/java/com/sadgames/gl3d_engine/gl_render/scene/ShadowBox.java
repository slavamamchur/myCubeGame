package com.sadgames.gl3d_engine.gl_render.scene;

/*import android.opengl.Matrix;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;*/

public class ShadowBox {

    /*private static final float OFFSET = 10;
    private static final float[] UP =  new float[]{0, 1, 0, 0};
    private static final float[] FORWARD =  new float[]{0, 0, -1, 0};
    private static final float SHADOW_DISTANCE = 100;

    private float minX, maxX;
    private float minY, maxY;
    private float minZ, maxZ;
    private float[] lightViewMatrix;
    private GLCamera cam;
    private float aspectRatio;

    private float farHeight, farWidth, nearHeight, nearWidth;


    public ShadowBox(float[] lightViewMatrix, GLCamera camera, float aspectRatio) {
        this.lightViewMatrix = lightViewMatrix;
        this.cam = camera;
        this.aspectRatio = aspectRatio;

        calculateWidthsAndHeights();
    }

    private void calculateWidthsAndHeights() {
        farWidth = (float) (SHADOW_DISTANCE * Math.tan(Math.toRadians(GLCamera.VERTICAL_FOV)));
        nearWidth = (float) (GLCamera.NEAR_PLANE * Math.tan(Math.toRadians(GLCamera.VERTICAL_FOV)));
        farHeight = farWidth / aspectRatio;
        nearHeight = nearWidth / aspectRatio;
    }

    private Vector3f transform(Matrix4f transformM, float[] vector_4f) {
        Vector4f tmp = new Vector4f(vector_4f);
        transformM.transform(tmp);

        return new Vector3f(tmp.x, tmp.y, tmp.z);
    }

    public void update() {
        Matrix4f rotationM = calculateCameraRotationMatrix();

        Vector3f forwardVector = transform(rotationM, FORWARD);

        Vector3f centerFar = new Vector3f(forwardVector);
        centerFar.scale(SHADOW_DISTANCE);
        centerFar.add(cam.getCameraPosition());

        Vector3f centerNear = new Vector3f(forwardVector);
        centerNear.scale(GLCamera.NEAR_PLANE);
        centerNear.add(cam.getCameraPosition());

        Vector4f[] points = calculateFrustumVertices(rotationM, forwardVector, centerNear, centerFar);

        boolean first = true;
        for (Vector4f point : points) {
            if (first) {
                minX = point.x;
                maxX = point.x;
                minY = point.y;
                maxY = point.y;
                minZ = point.z;
                maxZ = point.z;

                first = false;

                continue;
            }
            if (point.x > maxX) {
                maxX = point.x;
            } else if (point.x < minX) {
                minX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            } else if (point.y < minY) {
                minY = point.y;
            }
            if (point.z > maxZ) {
                maxZ = point.z;
            } else if (point.z < minZ) {
                minZ = point.z;
            }
        }

        maxZ += OFFSET;
    }

    protected Vector3f getCenter() {
        float x = (minX + maxX) / 2f;
        float y = (minY + maxY) / 2f;
        float z = (minZ + maxZ) / 2f;

        float[] cen = new float[]{x, y, z, 1};

        Matrix4f invertedLight = new Matrix4f();
        Matrix4f lVM = new Matrix4f(lightViewMatrix);
        invertedLight.invert(lVM);

        Vector3f tmp = transform(invertedLight, cen);

        return new Vector3f(tmp.x, tmp.y, tmp.z);
    }

    *//**
     * @return The width of the "view cuboid" (orthographic projection area).
     *//*
    protected float getWidth() {
        return maxX - minX;
    }

    *//**
     * @return The height of the "view cuboid" (orthographic projection area).
     *//*
    protected float getHeight() {
        return maxY - minY;
    }

    *//**
     * @return The length of the "view cuboid" (orthographic projection area).
     *//*
    protected float getLength() {
        return maxZ - minZ;
    }

    private Matrix4f calculateCameraRotationMatrix() {
        float[] mMatrix = new float[16];
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.rotateM(mMatrix, 0, -cam.getYaw(), 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, -cam.getPitch(), 1, 0, 0);

        return new Matrix4f(mMatrix);
    }

    private Vector4f[] calculateFrustumVertices(Matrix4f rotationM, Vector3f forwardVector, Vector3f centerNear, Vector3f centerFar) {
        Vector3f upVector = transform(rotationM, UP);

        Vector3f rightVector = new Vector3f();
        rightVector.cross(forwardVector, upVector);

        Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);

        Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);

        Vector3f farTop = new Vector3f();
        farTop.add(centerFar, new Vector3f(upVector.x * farHeight, upVector.y * farHeight, upVector.z * farHeight));

        Vector3f farBottom = new Vector3f();
        farBottom.add(centerFar, new Vector3f(downVector.x * farHeight, downVector.y * farHeight, downVector.z * farHeight));

        Vector3f nearTop = new Vector3f();
        nearTop.add(centerNear, new Vector3f(upVector.x * nearHeight, upVector.y * nearHeight, upVector.z * nearHeight));

        Vector3f nearBottom = new Vector3f();
        nearBottom.add(centerNear, new Vector3f(downVector.x * nearHeight, downVector.y * nearHeight, downVector.z * nearHeight));

        Vector4f[] points = new Vector4f[8];
        points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
        points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
        points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
        points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
        points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
        points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
        points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
        points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);

        return points;
    }

    private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction, float width) {
        Vector3f point = new Vector3f();
        point.add(startPoint, new Vector3f(direction.x * width, direction.y * width, direction.z * width));

        Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);
        Matrix4f lVM = new Matrix4f(lightViewMatrix);
        lVM.transform(point4f);

        return point4f;
    }

    public float getMinX() {
        return minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMinZ() {
        return minZ;
    }

    public float getMaxZ() {
        return maxZ;
    }*/
}
