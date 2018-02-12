package com.sadgames.sysutils.platforms.android;

import com.sadgames.sysutils.GLES20WrapperInterface;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glEnable;

public class AndroidGLES20Wrapper implements GLES20WrapperInterface {

    private static final Object lockObject = new Object();
    private static AndroidGLES20Wrapper instance = null;

    public static AndroidGLES20Wrapper getInstance(){
        synchronized (lockObject) {
            return instance != null ? instance : new AndroidGLES20Wrapper();
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
