package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.gl3dengine.glrender.BitmapWrapperInterface;
import com.sadgames.gl3dengine.glrender.GLES20APIWrapperInterface;

public abstract class AbstractTexture {

    private     int width;
    private     int height;
    private     int textureId;

    protected GLES20APIWrapperInterface glES20Wrapper;

    public AbstractTexture(GLES20APIWrapperInterface glES20Wrapper, int width, int height, BitmapWrapperInterface bitmap) {
        init(glES20Wrapper, width, height);
        createTexture(bitmap);

    }

    protected AbstractTexture() {}

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getTextureId() {
        return textureId;
    }

    protected void init(GLES20APIWrapperInterface glES20Wrapper, int width, int height) {
        this.width = width;
        this.height = height;
        this.glES20Wrapper = glES20Wrapper;
    }

    protected void createTexture(BitmapWrapperInterface bitmap) {
        final int[] textureIds = new int[1];
        glES20Wrapper.glGenTextures(1, textureIds, 0);

        if (textureIds[0] != 0) {
            if (!(this instanceof CubeMapTexture))
                glES20Wrapper.glBindTexture2D(textureIds[0]);
            else
                glES20Wrapper.glBindTextureCube(textureIds[0]);

            setTextureParams();

            try {
                loadTexture(bitmap);
            }
            catch (UnsupportedOperationException exception) {
                textureIds[0] = 0;
            }
        }

        textureId = textureIds[0];
    }

    public void bind() {
        bind(0);
    }
    public void bind(int glTextureSlot) {
        glES20Wrapper.glActiveTexture(glTextureSlot);
        glES20Wrapper.glBindTexture2D(textureId);
    }

    public void deleteTexture() {
        glES20Wrapper.glDeleteTextures(1, new int[]{textureId}, 0);
    }

    protected abstract int getTextureType();
    protected abstract void setTextureParams();
    protected abstract void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException;
}
