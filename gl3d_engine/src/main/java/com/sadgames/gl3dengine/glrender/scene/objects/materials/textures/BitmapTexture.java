package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.gl3dengine.glrender.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.nio.Buffer;

import static android.opengl.ETC1.ETC1_RGB8_OES;
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
import static android.opengl.GLES20.glCompressedTexImage2D;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;

public class BitmapTexture extends AbstractTexture {

    private String textureName = null;

    private BitmapTexture(BitmapWrapperInterface bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight(), bitmap);
    }

    private BitmapTexture(SysUtilsWrapperInterface sysUtilsWrapper, String textureName) {
        this(sysUtilsWrapper.iGetBitmapFromFile(textureName));
        this.textureName = textureName;
    }

    public String getTextureName() {
        return textureName;
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
    protected void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException {
        loadTextureInternal(getTextureType(), bitmap);
        bitmap.release();
    }

    @SuppressWarnings("all")
    protected void loadTextureInternal(int target, BitmapWrapperInterface bitmap) {
        try {
            if (!bitmap.isCompressed())
                glTexImage2D(target, 0, GL_RGBA, getWidth(), getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getRawData());
            else {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Buffer data;
                if (isETC1Supported()) {
                    data = bitmap.getRawData();
                    int imageSize = data.remaining();
                    glCompressedTexImage2D(target, 0, ETC1_RGB8_OES, width, height, 0, imageSize, data);
                } else {
                    glTexImage2D(target, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getDecodedRawData());
                }
            }
        }
        catch (Exception exception) {
            throw new UnsupportedOperationException();
        }
    }

    public static AbstractTexture createInstance(BitmapWrapperInterface bitmap) {
        return new BitmapTexture(bitmap);
    }

    public static AbstractTexture createInstance(SysUtilsWrapperInterface sysUtilsWrapper, String file) {
        return new BitmapTexture(sysUtilsWrapper, file);
    }

}
