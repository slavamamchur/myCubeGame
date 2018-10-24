package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.sysutils.common.BitmapWrapperInterface;

public abstract class AbstractTexture {

    private     int width;
    private     int height;
    private     int textureId;
    protected  long textureSize;

    public AbstractTexture(int width, int height, BitmapWrapperInterface bitmap) {
        init(width, height);
        this.textureSize = bitmap != null ? bitmap.getImageSizeBytes() : 0;

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
    public long getTextureSize() {
        return textureSize;
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
        textureId = 0;
    }

    /*public static boolean isETC1Supported() {
        int[] results = new int[20];
        glGetIntegerv(GL_NUM_COMPRESSED_TEXTURE_FORMATS, results, 0);
        int numFormats = results[0];
        if (numFormats > results.length) {
            results = new int[numFormats];
        }
        glGetIntegerv(GL_COMPRESSED_TEXTURE_FORMATS, results, 0);
        for (int i = 0; i < numFormats; i++) {
            if (results[i] == ETC1_RGB8_OES) {
                return true;
            }
        }
        return false;
    }*/

    protected abstract int getTextureType();
    protected abstract void setTextureParams();
    protected abstract void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException;
}
