package com.cubegames.slava.cubegame.gl_render.scene.fbo;

import javax.vecmath.Color4f;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glViewport;

public abstract class AbstractFBO {
    protected int width;
    protected int height;
    private int fboID;
    private int fboTexture;
    private Color4f clearColor;

    public AbstractFBO(int width, int height, Color4f clearColor) {
        this.width = width;
        this.height = height;
        this.clearColor = clearColor;

        fboID = createFBO();
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public int getFboTexture() {
        return fboTexture;
    }

    public void bindFBO() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        glViewport(0, 0, width, height);
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    public void unbindFBO() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    protected int createFBO() {
        // create a framebuffer object
        int[] fbos = new int[1];
        glGenFramebuffers(1, fbos, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, fbos[0]);

        // attach the texture to FBO
        fboTexture = attachFboTexture();

        // check FBO status
        try {
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
                throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
        finally {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        return fbos[0];
    }

    protected abstract int attachFboTexture();
}
