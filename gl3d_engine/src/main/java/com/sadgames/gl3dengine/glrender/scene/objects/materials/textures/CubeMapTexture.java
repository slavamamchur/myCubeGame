package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.gl3dengine.SysUtilsWrapperInterface;
import com.sadgames.gl3dengine.glrender.BitmapWrapperInterface;

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
    protected void setTextureParams() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        glTexParameteri(getTextureType(), GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(getTextureType(), GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    @Override
    protected void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException {
        try {
            for (int i =0; i < faces.length; i++) {
                bitmap = sysUtilsWrapper.iGetBitmapFromFile(faces[i]);
                glTexImage2D(
                        GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                        0, GL_RGBA,
                         bitmap.getWidth(),
                         bitmap.getHeight(),
                        0,
                         GL_RGBA,
                         GL_UNSIGNED_BYTE,
                         bitmap.getRawData());

                bitmap.release();
            }
        }
        catch (Exception exception) {
            throw new UnsupportedOperationException();
        }
    }

}
