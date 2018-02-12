package com.sadgames.sysutils.platforms.android;

import android.opengl.GLES20;

import com.sadgames.sysutils.GLES20APIWrapperInterface;

public class AndroidGLES20APIWrapper implements GLES20APIWrapperInterface {

    private static final Object lockObject = new Object();
    private static AndroidGLES20APIWrapper instance = null;

    public static AndroidGLES20APIWrapper getInstance(){
        synchronized (lockObject) {
            return instance != null ? instance : new AndroidGLES20APIWrapper();
        }
    }

    @Override
    public void glEnableFacesCulling() {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void glEnableDepthTest() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public String glExtensions() {
        return GLES20.glGetString(GLES20.GL_EXTENSIONS);
    }

    @Override
    public void glUseProgram(int id) {
        GLES20.glUseProgram(id);
    }

    @Override
    public void glCullFrontFace() {
        GLES20.glCullFace(GLES20.GL_FRONT);
    }

    @Override
    public void glCullBackFace() {
        GLES20.glCullFace(GLES20.GL_BACK);
    }

    @Override
    public void glBindFramebuffer(int id) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id);
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
    }

    @Override
    public void glActiveTexture(int slot) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + slot);
    }

    @Override
    public void glBindTexture2D(int id) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
    }

    @Override
    public void glSetClearColor(float r, float g, float b, float a) {
        GLES20.glClearColor(r, g, b ,a);
    }

    @Override
    public void glClear() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }


}
