package com.sadgames.gl3dengine.glrender.scene.fbo;

import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;

import javax.vecmath.Color4f;

public abstract class AbstractFBO {
    protected int width;
    protected int height;
    protected int fboID;
    protected AbstractTexture fboTexture;
    protected Color4f clearColor;

    public AbstractFBO(int width, int height, Color4f clearColor) {
        this.width = width;
        this.height = height;
        this.clearColor = clearColor;

        fboID = createFBO();
    }

    protected AbstractFBO() {
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
    public AbstractTexture getFboTexture() {
        return fboTexture;
    }

    public void bind() {
        GLES20JniWrapper.glBindFramebuffer(fboID);
        GLES20JniWrapper.glViewport(width, height);
        GLES20JniWrapper.glEnableFacesCulling();
        GLES20JniWrapper.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        GLES20JniWrapper.glClear();
    }

    public void unbind() {
        GLES20JniWrapper.glBindFramebuffer(0);
    }

    protected int createFBO() {
        int[] fbos = new int[1];
        GLES20JniWrapper.glGenFrameBuffers(fbos);
        GLES20JniWrapper.glBindFramebuffer(fbos[0]);

        fboTexture = attachFboTexture();

        try {
            if (!GLES20JniWrapper.glCheckFramebufferStatus())
                throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
        finally {
            GLES20JniWrapper.glBindFramebuffer(0);
        }

        return fbos[0];
    }

    public void cleanUp() {
        unbind();
        GLES20JniWrapper.glDeleteFrameBuffers(new int[]{fboID});
        fboTexture.deleteTexture();
    }

    protected abstract AbstractTexture attachFboTexture();
}
