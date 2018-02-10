package com.sadgames.dicegame.gl3d_engine.gl_render.scene.objects.materials.textures;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.sadgames.dicegame.gl3d_engine.utils.ISysUtilsWrapper;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glTexParameteri;

public class BitmapTexture extends AbstractTexture {

    public BitmapTexture(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight(), bitmap);
    }

    public BitmapTexture(ISysUtilsWrapper sysUtilsWrapper, int resourceId) {
        this(sysUtilsWrapper.getBitmapFromResource(resourceId));
    }

    public BitmapTexture(ISysUtilsWrapper sysUtilsWrapper, String file) {
        this(sysUtilsWrapper.getBitmapFromFile(file));
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
    protected void loadTexture(Bitmap bitmap) throws UnsupportedOperationException {
        try {
            GLUtils.texImage2D(getTextureType(), 0, bitmap, 0);
            bitmap.recycle();
        }
        catch (Exception exception) {
            throw new UnsupportedOperationException();
        }
    }

    public static AbstractTexture createInstance(Bitmap bitmap) {
        return new BitmapTexture(bitmap);
    }
}
