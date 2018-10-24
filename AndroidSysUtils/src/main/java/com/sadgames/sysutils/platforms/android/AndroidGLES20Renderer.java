package com.sadgames.sysutils.platforms.android;

import android.opengl.GLSurfaceView;

import com.sadgames.gl3dengine.glrender.GLRendererInterface;
import com.sadgames.gl3dengine.glrender.scene.GLScene;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGLES20Renderer implements GLSurfaceView.Renderer {

    private GLRendererInterface glInternalRenderer;

    public AndroidGLES20Renderer() {
        glInternalRenderer = new GLScene();
    }

    public GLScene getScene() {
        return glInternalRenderer.getScene();
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
