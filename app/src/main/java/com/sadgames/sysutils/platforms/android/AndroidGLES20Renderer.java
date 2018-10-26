package com.sadgames.sysutils.platforms.android;

import android.opengl.GLSurfaceView;

import com.sadgames.gl3dengine.glrender.GLRendererInterface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//TODO: replace with libGDX renderer and implement interface in GLScene
public class AndroidGLES20Renderer implements GLSurfaceView.Renderer {

    private GLRendererInterface glInternalRenderer;

    public AndroidGLES20Renderer(GLRendererInterface renderer) {
        this.glInternalRenderer = renderer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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
