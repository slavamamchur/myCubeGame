package com.cubegames.slava.cubegame.gl_render.scene.fbo;

import javax.vecmath.Color4f;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glRenderbufferStorage;
import static com.cubegames.slava.cubegame.Utils.createColorTexture;

public class ColorBufferFBO extends AbstractFBO {

    public ColorBufferFBO(int width, int height, Color4f clearColor) {
        super(width, height, clearColor);
    }

    @Override
    protected int attachFboTexture() {
        int renderTextureId = createColorTexture(width, height);
        glBindTexture(GL_TEXTURE_2D, renderTextureId);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, renderTextureId, 0);

        // create render buffer and bind 16-bit depth buffer
        int[] depthTextures = new int[1];
        glGenRenderbuffers(1, depthTextures, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, depthTextures[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);

        // attach the texture to FBO depth attachment point(not supported with gl_texture_2d)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthTextures[0]);

        return renderTextureId;
    }
}
