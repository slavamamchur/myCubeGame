package com.sadgames.sysutils.platforms.android;

import com.sadgames.sysutils.GLES2WrapperInterface;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glEnable;

public class AndroidGLES2Wrapper implements GLES2WrapperInterface {

    private static final Object lockObject = new Object();
    private static AndroidGLES2Wrapper instance = null;

    public static AndroidGLES2Wrapper getInstance(){
        synchronized (lockObject) {
            return instance != null ? instance : new AndroidGLES2Wrapper();
        }
    }

    @Override
    public void glEnableFacesCulling() {
        glEnable(GL_CULL_FACE);
    }

    @Override
    public void glEnableDepthTest() {
        glEnable(GL_DEPTH_TEST);
    }
}
