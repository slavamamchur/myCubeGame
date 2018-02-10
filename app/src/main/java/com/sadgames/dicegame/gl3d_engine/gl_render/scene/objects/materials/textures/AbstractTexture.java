package com.sadgames.dicegame.gl3d_engine.gl_render.scene.objects.materials.textures;

import android.graphics.Bitmap;

import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;

public abstract class AbstractTexture {

    private     int width;
    private     int height;
    private     int textureId;

    public AbstractTexture(int width, int height, Bitmap bitmap) {
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

    protected void createTexture(Bitmap bitmap) {
        final int[] textureIds = new int[1];
        glGenTextures(1, textureIds, 0);

        if (textureIds[0] != 0) {
            glBindTexture(getTextureType(), textureIds[0]);

            setTextureParams();

            try {
                loadTexture(bitmap);
            }
            catch (UnsupportedOperationException exception) {
                textureIds[0] = 0;
            }

            glBindTexture(getTextureType(), 0);
        }

        textureId = textureIds[0];
    }

    public void deleteTexture() {
        glDeleteTextures(1, new int[]{textureId}, 0);
    }

    protected abstract int getTextureType();
    protected abstract void setTextureParams();
    protected abstract void loadTexture(Bitmap bitmap) throws UnsupportedOperationException;
}
