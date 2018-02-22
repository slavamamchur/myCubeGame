package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.gl3dengine.glrender.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.nio.Buffer;

import static android.opengl.ETC1.ETC1_RGB8_OES;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
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

public class CubeMapTexture extends AbstractTexture {

    private String[] faces;
    private SysUtilsWrapperInterface sysUtilsWrapper;

    public CubeMapTexture(SysUtilsWrapperInterface sysUtilsWrapper, String[] faces) {
        init(-1, -1);

        this.faces = faces;
        this.sysUtilsWrapper = sysUtilsWrapper;

        createTexture(null);
    }

    @Override
    protected int getTextureType() {
        return GL_TEXTURE_CUBE_MAP;
    }

    @Override
    protected void setTextureParams() {//TODO: add mimpaps support
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        glTexParameteri(getTextureType(), GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(getTextureType(), GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    @Override
    protected void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException {
        textureSize = 0;

        for (int i =0; i < faces.length; i++)
            try {
                bitmap = sysUtilsWrapper.iGetBitmapFromFile(faces[i]);
                loadTextureInternal(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, bitmap);
                textureSize += bitmap.getImageSizeBytes();
                bitmap.release();
            }
            catch (Exception exception) {
                throw new UnsupportedOperationException(String.format("Texture \"%s\" load error.", faces[i]));
            }
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

}
