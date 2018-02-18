package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.gl3dengine.glrender.BitmapWrapperInterface;
import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;

public abstract class AbstractTexture {

    private     int width;
    private     int height;
    private     int textureId;

    public AbstractTexture(int width, int height, BitmapWrapperInterface bitmap) {
        init(width, height);
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

    protected void init(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected void createTexture(BitmapWrapperInterface bitmap) {
        final int[] textureIds = new int[1];
        GLES20JniWrapper.glGenTextures(textureIds);

        if (textureIds[0] != 0) {
            if (!(this instanceof CubeMapTexture))
                GLES20JniWrapper.glBindTexture2D(textureIds[0]);
            else
                GLES20JniWrapper.glBindTextureCube(textureIds[0]);

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
        GLES20JniWrapper.glActiveTexture(glTextureSlot);
        if (!(this instanceof CubeMapTexture))
            GLES20JniWrapper.glBindTexture2D(textureId);
        else
            GLES20JniWrapper.glBindTextureCube(textureId);
    }

    public void deleteTexture() {
        GLES20JniWrapper.glDeleteTextures(new int[]{textureId});
    }

    protected abstract int getTextureType();
    protected abstract void setTextureParams();
    protected abstract void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException;
}
