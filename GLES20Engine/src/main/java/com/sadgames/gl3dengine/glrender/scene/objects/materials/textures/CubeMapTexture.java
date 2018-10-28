package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.sysutils.common.BitmapWrapper;

import java.nio.Buffer;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_CLAMP_TO_EDGE;
import static com.badlogic.gdx.graphics.GL20.GL_LINEAR;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_RGBA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_CUBE_MAP;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
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

public class CubeMapTexture extends AbstractTexture {

    private String textureName = null;
    private String[] faces;

    public CubeMapTexture(String[] faces, String textureName) {
        init(-1, -1);

        this.faces = faces;
        this.textureName = textureName;

        createTexture(null);
    }

    public String getTextureName() {
        return textureName;
    }

    @Override
    protected int getTextureType() {
        return GL_TEXTURE_CUBE_MAP;
    }

    @Override
    protected void setTextureParams() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        glTexParameteri(getTextureType(), GL_TEXTURE_MIN_FILTER, GL_LINEAR/*GL_LINEAR_MIPMAP_LINEAR*/);
        glTexParameteri(getTextureType(), GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(getTextureType(), GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    @Override
    protected void loadTexture(BitmapWrapper bitmap) throws UnsupportedOperationException {
        textureSize = 0;

        for (int i =0; i < faces.length; i++)
            try {
                bitmap = getBitmapFromFile(faces[i], false);
                loadTextureInternal(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, bitmap);
                textureSize += bitmap.getImageSizeBytes();
                bitmap.release();
            }
            catch (Exception exception) {
                throw new UnsupportedOperationException(String.format("Texture \"%s\" load error.", faces[i]));
            }
    }

    @SuppressWarnings("all")
    protected void loadTextureInternal(int target, BitmapWrapper bitmap) {
        try {
            if (!bitmap.isCompressed()) {
                glTexImage2D(target,
                        0,
                        GL_RGBA,
                        getWidth(),
                        getHeight(),
                        0,
                        GL_RGBA,
                        GL_UNSIGNED_BYTE,
                        bitmap.getRawData());
            }
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

            //glGenerateMipmap(target);
        }
        catch (Exception exception) {
            throw new UnsupportedOperationException();
        }
    }

}
