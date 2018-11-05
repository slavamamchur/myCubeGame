package com.sadgames.gl3dengine;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.sadgames.gl3dengine.glrender.scene.GLScene;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_VERTICAL_FOV;

/**
 * Created by Slava Mamchur on 05.11.2018.
 */

public class MyGestureListener implements GestureDetector.GestureListener {

    private static final float TOUCH_SCALE_FACTOR = 22.5f / 320;

    private float mPreviousX;
    private float mPreviousY;
    private float mScaleFactor = 1.0f;
    float oldScaleFactor;
    private GLScene scene;

    public MyGestureListener(GLScene scene) {
        this.scene = scene;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        oldScaleFactor = mScaleFactor;
        mScaleFactor *= distance / initialDistance;

        if (oldScaleFactor != mScaleFactor) {
            scene.getCamera().setVfov(DEFAULT_CAMERA_VERTICAL_FOV / mScaleFactor);
            //requestRender();
        }

        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
