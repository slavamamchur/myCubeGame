package com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures;

import com.sadgames.gl3d_engine.gl_render.BitmapWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.GLES20APIWrapperInterface;

import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glTexImage2D;

public class DepthTexture extends RGBATexture {

    public DepthTexture(GLES20APIWrapperInterface glES20Wrapper, int width, int height) {
        super(glES20Wrapper, width, height);
    }

    @Override
    protected void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException {
        glTexImage2D(getTextureType(), 0, GL_DEPTH_COMPONENT, getWidth(), getHeight(), 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, null);
    }
}