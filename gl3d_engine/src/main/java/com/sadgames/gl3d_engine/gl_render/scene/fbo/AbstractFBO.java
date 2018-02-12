package com.sadgames.gl3d_engine.gl_render.scene.fbo;

import com.sadgames.gl3d_engine.gl_render.GLES20APIWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.AbstractTexture;

import javax.vecmath.Color4f;

public abstract class AbstractFBO {
    protected int width;
    protected int height;
    private int fboID;
    private AbstractTexture fboTexture;
    private Color4f clearColor;
    protected GLES20APIWrapperInterface glES20Wrapper;

    public AbstractFBO(GLES20APIWrapperInterface glES20Wrapper, int width, int height, Color4f clearColor) {
        this.width = width;
        this.height = height;
        this.clearColor = clearColor;
        this.glES20Wrapper = glES20Wrapper;

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

    public AbstractTexture getFboTexture() {
        return fboTexture;
    }
    public int getFboID() {
        return fboID;
    }

    public void bind() {
        glES20Wrapper.glBindFramebuffer(fboID);
        glES20Wrapper.glViewport(0, 0, width, height);
        glES20Wrapper.glEnableFacesCulling();
        glES20Wrapper.glSetClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        glES20Wrapper.glClear();
    }

    public void unbind() {
        glES20Wrapper.glBindFramebuffer(0);
    }

    protected int createFBO() {
        int[] fbos = new int[1];
        glES20Wrapper.glGenFrameBuffers(1, fbos, 0);
        glES20Wrapper.glBindFramebuffer(fbos[0]);

        fboTexture = attachFboTexture();

        try {
            if (!glES20Wrapper.glCheckFramebufferStatus())
                throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
        finally {
            glES20Wrapper.glBindFramebuffer(0);
        }

        return fbos[0];
    }

    public void cleanUp() {
        unbind();
        glES20Wrapper.glDeleteFrameBuffers(1, new int[]{fboID}, 0);
        fboTexture.deleteTexture();
    }

    protected abstract AbstractTexture attachFboTexture();
}
