package com.sadgames.dicegame.ui.framework;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;

import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;
import com.sadgames.sysutils.platforms.android.AndroidGLES20Renderer;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_VERTICAL_FOV;

public class MapGLSurfaceView extends GLSurfaceView {

    private static final float TOUCH_SCALE_FACTOR = 22.5f / 320;
    public static final int OGL_ES_20 = 2;

    private AndroidGLES20Renderer mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    private float mScaleFactor = 1.0f;

    private ScaleGestureDetector mScaleDetector;

    public MapGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(OGL_ES_20);

        //TODO: gl set number of samples
        //EGL10 Egl = (EGL10) EGLContext.getEGL();

        mScaleDetector = new ScaleGestureDetector(context.getApplicationContext(), new ScaleListener());
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);

        mRenderer = (AndroidGLES20Renderer) renderer;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        GLCamera camera = mRenderer.getScene().getCamera();

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
                mRenderer.getScene().getLightSource().setLightPosInEyeSpace();
            }

            //requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        //TODO: error cleaning gl buffers
        /*mRenderer.getScene().setRenderStopped(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRenderer.getScene().cleanUp();*/

        //TextureCacheManager.createInstance(AndroidDiceGameUtilsWrapper.createInstance(getContext())).clearCache();

        super.surfaceDestroyed(holder);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            return true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
