package com.sadgames.gl3d_engine.gl_render.scene.fbo;

import com.sadgames.gl3d_engine.gl_render.GLES20APIWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.DepthTexture;

import javax.vecmath.Color4f;

import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.FBO_TEXTURE_SLOT;

public class DepthBufferFBO extends AbstractFBO {
    public DepthBufferFBO(GLES20APIWrapperInterface glES20Wrapper, int width, int height, Color4f clearColor) {
        super(glES20Wrapper, width, height, clearColor);
    }

    @Override
    protected AbstractTexture attachFboTexture() {
        AbstractTexture renderTexture = new DepthTexture(glES20Wrapper, width, height);

        glES20Wrapper.glActiveTexture(FBO_TEXTURE_SLOT);
        glES20Wrapper.glBindTexture2D(renderTexture.getTextureId());
        glES20Wrapper.glFramebufferAttachDepthTexture(renderTexture.getTextureId());

        return renderTexture;
    }
}
