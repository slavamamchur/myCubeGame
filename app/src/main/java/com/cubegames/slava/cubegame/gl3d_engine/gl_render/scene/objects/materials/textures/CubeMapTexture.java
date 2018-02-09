package com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.objects.materials.textures;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.cubegames.slava.cubegame.gl3d_engine.utils.ISysUtilsWrapper;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glTexParameteri;

public class CubeMapTexture extends AbstractTexture {

    private int[] faces;
    private ISysUtilsWrapper sysUtilsWrapper;

    public CubeMapTexture(ISysUtilsWrapper sysUtilsWrapper, int[] faces) {
        init( -1, -1);

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
    protected void loadTexture(Bitmap bitmap) throws UnsupportedOperationException {
        try {
            for (int i =0; i < faces.length; i++) {
                bitmap = sysUtilsWrapper.getBitmapFromResource(faces[i]);
                GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, bitmap, 0);
                bitmap.recycle();
            }
        }
        catch (Exception exception) {
            throw new UnsupportedOperationException();
        }
    }

}
