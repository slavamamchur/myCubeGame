package com.sadgames.dicegame.ui.framework;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_VERTICAL_FOV;

public class MapGLSurfaceView extends GLSurfaceView20 {

    private static final float TOUCH_SCALE_FACTOR = 22.5f / 320;
    public static final int OGL_ES_20 = 2;

    private float mPreviousX;
    private float mPreviousY;
    private float mScaleFactor = 1.0f;

    private GLScene scene = null;
    private ScaleGestureDetector mScaleDetector;

    public MapGLSurfaceView(Context context, ResolutionStrategy resolutionStrategy) {
        super(context, resolutionStrategy, OGL_ES_20);

        //TODO: gl set number of samples
        //EGL10 Egl = (EGL10) EGLContext.getEGL();

        mScaleDetector = new ScaleGestureDetector(context.getApplicationContext(), new ScaleListener());
    }

    public void setScene(GLScene scene) {
        this.scene = scene;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        GLCamera camera = scene.getCamera();

        float oldScaleFactor = mScaleFactor;
        mScaleDetector.onTouchEvent(e);

        if (oldScaleFactor != mScaleFactor) {
            /** Zoom camera */
            camera.setVfov(DEFAULT_CAMERA_VERTICAL_FOV / mScaleFactor);
            //requestRender();
        }
        else if (e.getAction() == MotionEvent.ACTION_MOVE) {

            float dx = x - mPreviousX;
            float dy = y - mPreviousY;

            synchronized (GLScene.lockObject) {
                camera.rotateX(-dy * TOUCH_SCALE_FACTOR / 2);
                camera.rotateY(dx * TOUCH_SCALE_FACTOR * 2);

                camera.updateViewMatrix();
                scene.getLightSource().setLightPosInEyeSpace();
            }

            //requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            return true;
        }
    }

}
