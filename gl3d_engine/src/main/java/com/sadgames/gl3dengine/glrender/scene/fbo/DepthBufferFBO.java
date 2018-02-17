package com.sadgames.gl3dengine.glrender.scene.fbo;

import com.sadgames.gl3dengine.glrender.GLES20APIWrapperInterface;
import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.DepthTexture;

import javax.vecmath.Color4f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.FBO_TEXTURE_SLOT;

public class DepthBufferFBO extends AbstractFBO {
    protected GLES20APIWrapperInterface glES20Wrapper;

    public DepthBufferFBO(GLES20APIWrapperInterface glES20Wrapper, int width, int height, Color4f clearColor) {
        ///super(width, height, clearColor);
        this.width = width;
        this.height = height;
        this.clearColor = clearColor;
        this.glES20Wrapper = glES20Wrapper;

        fboID = createFBO();
    }

    @Override
    protected AbstractTexture attachFboTexture() {
        AbstractTexture renderTexture = new DepthTexture(glES20Wrapper, width, height);

        renderTexture.bind(FBO_TEXTURE_SLOT);
        GLES20JniWrapper.glFramebufferAttachDepthTexture(renderTexture.getTextureId());

        return renderTexture;
    }
}
