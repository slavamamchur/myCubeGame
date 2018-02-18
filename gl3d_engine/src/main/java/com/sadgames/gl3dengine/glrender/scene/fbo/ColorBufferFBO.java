package com.sadgames.gl3dengine.glrender.scene.fbo;

import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.RGBATexture;

import javax.vecmath.Color4f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.FBO_TEXTURE_SLOT;

public class ColorBufferFBO extends AbstractFBO {

    private int[] depthTextures = new int[1];

    public ColorBufferFBO(int width, int height, Color4f clearColor) {
        super(width, height, clearColor);
    }

    @Override
    protected AbstractTexture attachFboTexture() {
        AbstractTexture renderTexture = new RGBATexture(width, height);
        renderTexture.bind(FBO_TEXTURE_SLOT);
        GLES20JniWrapper.glFramebufferAttachColorTexture(renderTexture.getTextureId());

        GLES20JniWrapper.glGenRenderBuffers(depthTextures);
        GLES20JniWrapper.glBindRenderBuffer(depthTextures[0]);
        GLES20JniWrapper.glRenderBufferStorage(width, height);
        GLES20JniWrapper.glFramebufferAttachDepthBuffer(depthTextures[0]);

        return renderTexture;
    }

    @Override
    public void cleanUp() {
        super.cleanUp();

        GLES20JniWrapper.glDeleteRenderBuffers(depthTextures);
    }
}
