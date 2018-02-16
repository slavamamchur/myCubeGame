package com.sadgames.gl3dengine.glrender.scene.fbo;

import com.sadgames.gl3dengine.glrender.GLES20APIWrapperInterface;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.RGBATexture;

import javax.vecmath.Color4f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.FBO_TEXTURE_SLOT;

public class ColorBufferFBO extends AbstractFBO {

    private int[] depthTextures = new int[1];

    public ColorBufferFBO(GLES20APIWrapperInterface glES20Wrapper, int width, int height, Color4f clearColor) {
        super(glES20Wrapper, width, height, clearColor);
    }

    @Override
    protected AbstractTexture attachFboTexture() {
        AbstractTexture renderTexture = new RGBATexture(glES20Wrapper, width, height);

        glES20Wrapper.glActiveTexture(FBO_TEXTURE_SLOT);
        glES20Wrapper.glBindTexture2D(renderTexture.getTextureId());
        glES20Wrapper.glFramebufferAttachColorTexture(renderTexture.getTextureId());

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
