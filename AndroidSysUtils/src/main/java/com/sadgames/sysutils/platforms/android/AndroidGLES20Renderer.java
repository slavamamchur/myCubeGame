package com.sadgames.sysutils.platforms.android;

import android.opengl.GLSurfaceView;

import com.sadgames.gl3d_engine.GameEventsCallbackInterface;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.GLRendererInterface;
import com.sadgames.gl3d_engine.gl_render.scene.GLScene;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGLES20Renderer implements GLSurfaceView.Renderer {

    private GLRendererInterface mScene;
    private SysUtilsWrapperInterface sysUtilsWrapper;
    private GameEventsCallbackInterface gameEventsCallBack;

    public AndroidGLES20Renderer(SysUtilsWrapperInterface sysUtilsWrapper, GameEventsCallbackInterface gameEventsCallBack) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        this.gameEventsCallBack = gameEventsCallBack;
        this.mScene = new GLScene(sysUtilsWrapper, gameEventsCallBack);
    }

    public GLScene getScene() {
        return mScene.getSceneObject();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mScene.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mScene.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mScene.onDrawFrame(gl);
    }

}
