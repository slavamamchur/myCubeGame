package com.sadgames.sysutils.platforms.android;

import android.opengl.GLSurfaceView;

import com.badlogic.gdx.backends.android.AndroidGL20;
import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.GLRendererInterface;
import com.sadgames.gl3dengine.glrender.GdxExt;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGLES20Renderer implements GLSurfaceView.Renderer {

    private GLRendererInterface glInternalRenderer;
    private EGLContext eglContext;

    public AndroidGLES20Renderer(GLRendererInterface renderer) {
        this.glInternalRenderer = renderer;
    }

    private void initGDX() {
        AndroidGL20.init();

        GdxExt.gl20 = new AndroidGL20();
        GdxExt.gl = GdxExt.gl20;

        GLES20JniWrapper.setGlEngine(GdxExt.gl);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        eglContext = ((EGL10)EGLContext.getEGL()).eglGetCurrentContext();

        //TODO: initGDX(); -> after GLES20JniWrapper modifying

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
