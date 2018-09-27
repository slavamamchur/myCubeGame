package com.sadgames.gl3dengine.glrender.scene.camera;

import com.sadgames.sysutils.common.MathUtils;

import javax.vecmath.Vector3f;

public class Orthogonal2DCamera extends GLCamera {

    private float landSize;
    private float scaleFactor = 1.0f;

    public Orthogonal2DCamera(float landSize) {
        super(new Vector3f(0, landSize / 2f, 0), 90, 0, 0);

        this.vfov = 90;
        this.zoomed_vfov = 90;
        this.landSize = landSize;
    }

    @Override
    public void setVfov(float vfov) {
        this.vfov = vfov;
        this.scaleFactor = vfov/ 90f;

        updateProjectionMatrix();
    }

    @Override
    protected void updateProjectionMatrix() {
        float landHalfSize = landSize / 2f;
        float left = -landHalfSize * aspectRatio * scaleFactor;
        float right = landHalfSize * aspectRatio * scaleFactor;
        float bottom = -landHalfSize * scaleFactor;
        float top = landHalfSize * scaleFactor;

        MathUtils.setIdentityM(projectionMatrix, 0);
        MathUtils.orthoM(projectionMatrix, 0, left, right, bottom, top, NEAR_PLANE, FAR_PLANE);
    }

    @Override public void rotateX(float angle) {}
    @Override public void rotateY(float angle) {}
    @Override public void rotateZ(float angle) {}
}
