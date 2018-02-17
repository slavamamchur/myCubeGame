package com.sadgames.gl3dengine.glrender.scene.fbo;

import com.sadgames.gl3dengine.glrender.GLES20APIWrapperInterface;
import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.RGBATexture;

import javax.vecmath.Color4f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.FBO_TEXTURE_SLOT;

public class ColorBufferFBO extends AbstractFBO {

    protected GLES20APIWrapperInterface glES20Wrapper;
    private int[] depthTextures = new int[1];

    public ColorBufferFBO(GLES20APIWrapperInterface glES20Wrapper, int width, int height, Color4f clearColor) {
        ///super(width, height, clearColor);
        this.width = width;
        this.height = height;
        this.clearColor = clearColor;
        this.glES20Wrapper = glES20Wrapper;

        fboID = createFBO();
    }

    @Override
    protected AbstractTexture attachFboTexture() {
        AbstractTexture renderTexture = new RGBATexture(glES20Wrapper, width, height);
        renderTexture.bind(FBO_TEXTURE_SLOT);
        GLES20JniWrapper.glFramebufferAttachColorTexture(renderTexture.getTextureId());

        glES20Wrapper.glGenRenderBuffers(1, depthTextures, 0);
        glES20Wrapper.glBindRenderBuffer(depthTextures[0]);
        glES20Wrapper.glRenderBufferStorage(width, height);
        glES20Wrapper.glFramebufferAttachDepthBuffer(depthTextures[0]);

        return renderTexture;
    }

    @Override
    public void cleanUp() {
        super.cleanUp();

        glES20Wrapper.glDeleteRenderBuffers(1, depthTextures, 0);
    }
}
