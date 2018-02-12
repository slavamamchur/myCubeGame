package com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures;

import com.sadgames.sysutils.IBitmapWrapper;
import com.sadgames.sysutils.ISysUtilsWrapper;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;

public class BitmapTexture extends AbstractTexture {

    public BitmapTexture(IBitmapWrapper bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight(), bitmap);
    }

    public BitmapTexture(ISysUtilsWrapper sysUtilsWrapper, String file) {
        this(sysUtilsWrapper.iGetBitmapFromFile(file));
    }

    public BitmapTexture(ISysUtilsWrapper sysUtilsWrapper, int color) {
        this(sysUtilsWrapper.iCreateColorBitmap(color));
    }

    @Override
    protected int getTextureType() {
        return GL_TEXTURE_2D;
    }

    @Override
    protected void setTextureParams() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glTexParameteri(getTextureType(), GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(getTextureType(), GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_T, GL_REPEAT);
    }

    @Override
    protected void loadTexture(IBitmapWrapper bitmap) throws UnsupportedOperationException {
        try {
            glTexImage2D(getTextureType(), 0, GL_RGBA, getWidth(), getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getRawData());
            bitmap.release();
        }
        catch (Exception exception) {
            throw new UnsupportedOperationException();
        }
    }

    public static AbstractTexture createInstance(IBitmapWrapper bitmap) {
        return new BitmapTexture(bitmap);
    }

    public static AbstractTexture createInstance(ISysUtilsWrapper sysUtilsWrapper, String file) {
        return new BitmapTexture(sysUtilsWrapper, file);
    }

    public static AbstractTexture createInstance(ISysUtilsWrapper sysUtilsWrapper, int color) {
        return new BitmapTexture(sysUtilsWrapper, color);
    }
}
