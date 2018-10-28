package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.sysutils.common.BitmapWrapper;

import static com.badlogic.gdx.graphics.GL20.GL_CLAMP_TO_EDGE;
import static com.badlogic.gdx.graphics.GL20.GL_NEAREST;
import static com.badlogic.gdx.graphics.GL20.GL_RGBA;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_MAG_FILTER;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_MIN_FILTER;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_WRAP_S;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_WRAP_T;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_BYTE;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glTexImage2D;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glTexParameteri;

public class RGBATexture extends AbstractTexture {

    public RGBATexture(int width, int height) {
        super(width, height, null);
    }

    @Override
    protected int getTextureType() {
        return GL_TEXTURE_2D;
    }

    @Override
    protected void setTextureParams() {
        glTexParameteri(getTextureType(), GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(getTextureType(), GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    @Override
    protected void loadTexture(BitmapWrapper bitmap) throws UnsupportedOperationException {
        glTexImage2D(getTextureType(),
                    0,
                     GL_RGBA,
                     getWidth(),
                     getHeight(),
                    0,
                     GL_RGBA,
                     GL_UNSIGNED_BYTE,
                    null);
    }
}
