package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.sysutils.common.BitmapWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_CLAMP_TO_EDGE_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_NEAREST_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_RGBA_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_2D_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_MAG_FILTER_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_MIN_FILTER_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_WRAP_S_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_WRAP_T_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_UNSIGNED_BYTE_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glTexImage2D;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glTexParameteri;

public class RGBATexture extends AbstractTexture {

    public RGBATexture(int width, int height) {
        super(width, height, null);
    }

    @Override
    protected int getTextureType() {
        return get_GL_TEXTURE_2D_value();
    }

    @Override
    protected void setTextureParams() {
        glTexParameteri(getTextureType(), get_GL_TEXTURE_MIN_FILTER_value(), get_GL_NEAREST_value());
        glTexParameteri(getTextureType(), get_GL_TEXTURE_MAG_FILTER_value(), get_GL_NEAREST_value());
        glTexParameteri(getTextureType(), get_GL_TEXTURE_WRAP_S_value(), get_GL_CLAMP_TO_EDGE_value());
        glTexParameteri(getTextureType(), get_GL_TEXTURE_WRAP_T_value(), get_GL_CLAMP_TO_EDGE_value());
    }

    @Override
    protected void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException {
        glTexImage2D(getTextureType(),
                    0,
                     get_GL_RGBA_value(),
                     getWidth(),
                     getHeight(),
                    0,
                     get_GL_RGBA_value(),
                     get_GL_UNSIGNED_BYTE_value(),
                    null);
    }
}
