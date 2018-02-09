package com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.fbo;

import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.objects.materials.textures.AbstractTexture;
import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.objects.materials.textures.DepthTexture;

import javax.vecmath.Color4f;

import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TEXTURE4;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glFramebufferTexture2D;

public class DepthBufferFBO extends AbstractFBO {
    public DepthBufferFBO(int width, int height, Color4f clearColor) {
        super(width, height, clearColor);
    }

    @Override
    protected AbstractTexture attachFboTexture() {
        AbstractTexture renderTexture = new DepthTexture(width, height);
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, renderTexture.getTextureId());
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, renderTexture.getTextureId(), 0);

        return renderTexture;
    }
}
