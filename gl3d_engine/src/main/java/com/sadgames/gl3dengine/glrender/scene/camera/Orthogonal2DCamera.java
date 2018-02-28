package com.sadgames.gl3dengine.glrender.scene.camera;

import javax.vecmath.Vector3f;

public class Orthogonal2DCamera extends GLCamera {

    public Orthogonal2DCamera(Vector3f cameraPosition, float vfov) {
        super(cameraPosition, 0, 0, 0);

        this.vfov = vfov;
        this.zoomed_vfov = vfov;
    }

    @Override
    protected void updateProjectionMatrix() {
        //TODO: Orthogonal projection
    }

    @Override public void rotateX(float angle) {}
    @Override public void rotateY(float angle) {}
    @Override public void rotateZ(float angle) {}
}
