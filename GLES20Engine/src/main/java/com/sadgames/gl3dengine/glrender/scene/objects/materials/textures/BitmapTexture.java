package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.sysutils.common.BitmapWrapper;

import java.nio.Buffer;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_LINEAR;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_REPEAT;
import static com.badlogic.gdx.graphics.GL20.GL_RGBA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_MAG_FILTER;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_MIN_FILTER;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_WRAP_S;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_WRAP_T;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_BYTE;
import static com.badlogic.gdx.graphics.glutils.ETC1.ETC1_RGB8_OES;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glBlendFunc;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glCompressedTexImage2D;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnable;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glTexImage2D;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glTexParameteri;
import static com.sadgames.sysutils.common.CommonUtils.getBitmapFromFile;

public class BitmapTexture extends AbstractTexture {

    private String textureName = null;

    private BitmapTexture(BitmapWrapper bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight(), bitmap);
    }

    private BitmapTexture(String textureName) {
        this(getBitmapFromFile(textureName, false));
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

        glTexParameteri(getTextureType(), GL_TEXTURE_MIN_FILTER, GL_LINEAR/*GL_LINEAR_MIPMAP_LINEAR*/);
        glTexParameteri(getTextureType(), GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_T, GL_REPEAT);
    }

    @Override
    protected void loadTexture(BitmapWrapper bitmap) throws UnsupportedOperationException {
        /*if (!bitmap.isCompressed()
            && !(bitmap.getWidth() < 3 && bitmap.getHeight() < 3)
            && bitmap.getRawData() != null
            && !GameConst.BLENDING_MAP_TEXTURE.equals(bitmap.getTextureName()))

            bitmap = packToETC1(bitmap);*///TODO: remove stub after fix error -> must be available!!!

        loadTextureInternal(getTextureType(), bitmap);

        bitmap.release();
    }

    @SuppressWarnings("all")
    protected void loadTextureInternal(int target, BitmapWrapper bitmap) {
        try {
            if (!bitmap.isCompressed())
                glTexImage2D(target,
                        0,
                        GL_RGBA,
                        getWidth(),
                        getHeight(),
                        0,
                        GL_RGBA,
                        GL_UNSIGNED_BYTE,
                        bitmap.getRawData());
            else {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Buffer data;
                //if (isETC1Supported()) {
                    data = bitmap.getRawData();
                    int imageSize = data.remaining();
                    glCompressedTexImage2D(target, 0, ETC1_RGB8_OES, width, height, 0, imageSize, data);
                //} else {
                    //glTexImage2D(target, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getDecodedRawData());
                //}
            }

            //glGenerateMipmap(target);//TODO: manually
        }
        catch (Exception exception) {
            throw new UnsupportedOperationException();
        }
    }

    public static AbstractTexture createInstance(BitmapWrapper bitmap) {
        return new BitmapTexture(bitmap);
    }

    public static AbstractTexture createInstance(String file) {
        return new BitmapTexture(file);
    }

}
