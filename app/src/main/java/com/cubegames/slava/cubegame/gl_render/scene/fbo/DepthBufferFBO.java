package com.cubegames.slava.cubegame.gl_render.scene.fbo;

import javax.vecmath.Color4f;

import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static com.cubegames.slava.cubegame.Utils.createDepthTexture;

public class DepthBufferFBO extends AbstractFBO {
    public DepthBufferFBO(int width, int height, Color4f clearColor) {
        super(width, height, clearColor);
    }

    @Override
    protected int attachFboTexture() {
        // Use a depth texture
        int renderTextureId = createDepthTexture(width, height);
        glBindTexture(GL_TEXTURE_2D, renderTextureId);

        // Attach the depth texture to FBO depth attachment point
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, renderTextureId, 0);

        return renderTextureId;
    }
}
