package com.sadgames.sysutils.platforms.android;

import android.opengl.GLSurfaceView;

import com.sadgames.gl3dengine.GameEventsCallbackInterface;
import com.sadgames.gl3dengine.SysUtilsWrapperInterface;
import com.sadgames.gl3dengine.glrender.GLRendererInterface;
import com.sadgames.gl3dengine.glrender.scene.GLScene;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGLES20Renderer implements GLSurfaceView.Renderer {

    private GLRendererInterface mScene;
    private SysUtilsWrapperInterface sysUtilsWrapper;
    private GameEventsCallbackInterface gameEventsCallBack;

    public AndroidGLES20Renderer(SysUtilsWrapperInterface sysUtilsWrapper) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        mScene = new GLScene(sysUtilsWrapper);
    }

    public GLScene getScene() {
        return mScene.getSceneObject();
    }

    public void setGameEventsCallBack(GameEventsCallbackInterface gameEventsCallBack) {
        this.gameEventsCallBack = gameEventsCallBack;
        mScene.setGameEventsCallBack(gameEventsCallBack);
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
