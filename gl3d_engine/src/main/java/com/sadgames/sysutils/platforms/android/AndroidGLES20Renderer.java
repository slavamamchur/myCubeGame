package com.sadgames.sysutils.platforms.android;

import android.opengl.GLSurfaceView;

import com.sadgames.gl3d_engine.gl_render.GLRendererInterface;
import com.sadgames.gl3d_engine.gl_render.GameEventsCallbackInterface;
import com.sadgames.gl3d_engine.gl_render.scene.GLScene;
import com.sadgames.sysutils.SysUtilsWrapperInterface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGLES20Renderer implements GLSurfaceView.Renderer {

    private String mapID = null; /** for map editor */
    private GLRendererInterface mScene;
    private SysUtilsWrapperInterface sysUtilsWrapper;
    private GameEventsCallbackInterface gameEventsCallBack;

    public AndroidGLES20Renderer(SysUtilsWrapperInterface sysUtilsWrapper, GameEventsCallbackInterface gameEventsCallBack) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        this.gameEventsCallBack = gameEventsCallBack;
    }

    public GLScene getScene() {
        return mScene.getSceneObject();
    }
    public void setMapID(String mapID) {
        this.mapID = mapID;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mScene = new GLScene(sysUtilsWrapper, gameEventsCallBack);
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
