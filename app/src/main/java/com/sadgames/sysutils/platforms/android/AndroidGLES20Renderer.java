package com.sadgames.sysutils.platforms.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidGL20;
import com.sadgames.dicegame.ui.framework.MapGLSurfaceView;
import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.GLRendererInterface;
import com.sadgames.gl3dengine.glrender.GdxExt;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGLES20Renderer implements GLSurfaceView.Renderer { //TODO: replace with AndroidGraphics from libGDX

    private GLRendererInterface glInternalRenderer;
    private final Context app;
    private EGLContext eglContext;
    private final View view;

    public AndroidGLES20Renderer(Context context, GLRendererInterface renderer, boolean focusableView) {
        glInternalRenderer = renderer;
        app = context;

        AndroidGL20.init();

        view = new MapGLSurfaceView(context);
        preserveEGLContextOnPause();
        if (focusableView) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
        }
    }

    public AndroidGLES20Renderer(Context context, GLRendererInterface renderer) {
        this(context, renderer, true);
    }

    public Context getApp() {
        return app;
    }
    public EGLContext getEglContext() {
        return eglContext;
    }
    public View getView() {
        return view;
    }

    private void setupGL() {
        GdxExt.gl20 = new AndroidGL20();
        GdxExt.gl = GdxExt.gl20;

        GLES20JniWrapper.setGlEngine(GdxExt.gl);
    }

    public void preserveEGLContextOnPause() {
            try {
                view.getClass().getMethod("setPreserveEGLContextOnPause", boolean.class).invoke(view, true);
            } catch (Exception e) {}
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        eglContext = ((EGL10)EGLContext.getEGL()).eglGetCurrentContext();

        setupGL();

        glInternalRenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glInternalRenderer.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glInternalRenderer.onDrawFrame();
    }

}
