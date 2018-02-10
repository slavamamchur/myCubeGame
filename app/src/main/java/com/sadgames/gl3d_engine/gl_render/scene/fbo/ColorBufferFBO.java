package com.sadgames.gl3d_engine.gl_render.scene.fbo;

import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.RGBATexture;

import javax.vecmath.Color4f;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_TEXTURE4;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteRenderbuffers;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glRenderbufferStorage;

public class ColorBufferFBO extends AbstractFBO {

    private int[] depthTextures = new int[1];

    public ColorBufferFBO(int width, int height, Color4f clearColor) {
        super(width, height, clearColor);
    }

    @Override
    protected AbstractTexture attachFboTexture() {
        AbstractTexture renderTexture = new RGBATexture(width, height);
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, renderTexture.getTextureId());
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, renderTexture.getTextureId(), 0);

        glGenRenderbuffers(1, depthTextures, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, depthTextures[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthTextures[0]);

        return renderTexture;
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        glDeleteRenderbuffers(1, depthTextures, 0);
    }
}
