package com.sadgames.dicegame.ui.framework;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;

import com.sadgames.gl3dengine.glrender.scene.GLCamera;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.platforms.android.AndroidDiceGameUtilsWrapper;
import com.sadgames.sysutils.platforms.android.AndroidGLES20Renderer;

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
        mScaleDetector = new ScaleGestureDetector(context.getApplicationContext(), new ScaleListener());
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);

        mRenderer = (AndroidGLES20Renderer) renderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        GLCamera camera = mRenderer.getScene().getCamera();

        float oldScaleFactor = mScaleFactor;
        mScaleDetector.onTouchEvent(e);

        if (oldScaleFactor != mScaleFactor) {
            /** Zoom camera */
            camera.setVfov(GLCamera.VERTICAL_FOV / mScaleFactor);
            requestRender();
        }
        else if (e.getAction() == MotionEvent.ACTION_MOVE) {

            float dx = x - mPreviousX;
            float dy = y - mPreviousY;

            synchronized (GLScene.lockObject) {
                /** turn head vertically */
                camera.directSetPitch(camera.getPitch() + (dy * TOUCH_SCALE_FACTOR / 2));

                /** Rotate camera around scene */
                camera.rotateYAroundViewPoint(dx * TOUCH_SCALE_FACTOR * 2);

                /** turn head horizontally */
                ///camera.directSetYaw(camera.getYaw() + (dx * TOUCH_SCALE_FACTOR));

                camera.updateViewMatrix();
                mRenderer.getScene().getLightSource().setLightPosInEyeSpace();
            }

            requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { //TODO: error cleaning gl buffers and clear texture cache !!!

        /*mRenderer.getScene().setRenderStopped(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRenderer.getScene().cleanUp();*/

        TextureCacheManager.getInstance(AndroidDiceGameUtilsWrapper.getInstance(getContext())).clearCache();

        super.surfaceDestroyed(holder);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            return true;
        }
    }

}
