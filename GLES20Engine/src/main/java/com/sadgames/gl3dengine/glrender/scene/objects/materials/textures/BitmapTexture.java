package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.gl3dengine.gamelogic.client.GameConst;
import com.sadgames.sysutils.common.BitmapWrapper;

import java.nio.Buffer;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_ETC1_RGB8_OES_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_BLEND_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_LINEAR_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_ONE_MINUS_SRC_ALPHA_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_REPEAT_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_RGBA_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_SRC_ALPHA_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_2D_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_MAG_FILTER_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_MIN_FILTER_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_WRAP_S_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TEXTURE_WRAP_T_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_UNSIGNED_BYTE_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glBlendFunc;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glCompressedTexImage2D;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnable;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glTexImage2D;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glTexParameteri;
import static com.sadgames.sysutils.common.CommonUtils.getBitmapFromFile;
import static com.sadgames.sysutils.common.CommonUtils.packToETC1;

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
        return get_GL_TEXTURE_2D_value();
    }

    @Override
    protected void setTextureParams() {
        glBlendFunc(get_GL_SRC_ALPHA_value(), get_GL_ONE_MINUS_SRC_ALPHA_value());
        glEnable(get_GL_BLEND_value());

        glTexParameteri(getTextureType(), get_GL_TEXTURE_MIN_FILTER_value(), get_GL_LINEAR_value()/*GL_LINEAR_MIPMAP_LINEAR*/);
        glTexParameteri(getTextureType(), get_GL_TEXTURE_MAG_FILTER_value(), get_GL_LINEAR_value());
        glTexParameteri(getTextureType(), get_GL_TEXTURE_WRAP_S_value(), get_GL_REPEAT_value());
        glTexParameteri(getTextureType(), get_GL_TEXTURE_WRAP_T_value(), get_GL_REPEAT_value());
    }

    @Override
    protected void loadTexture(BitmapWrapper bitmap) throws UnsupportedOperationException {
        if (!bitmap.isCompressed()
            && !(bitmap.getWidth() < 3 && bitmap.getHeight() < 3)
            && bitmap.getRawData() != null
            && !GameConst.BLENDING_MAP_TEXTURE.equals(bitmap.getTextureName()))

            bitmap = packToETC1(bitmap);

        loadTextureInternal(getTextureType(), bitmap);

        bitmap.release();
    }

    @SuppressWarnings("all")
    protected void loadTextureInternal(int target, BitmapWrapper bitmap) {
        try {
            if (!bitmap.isCompressed())
                glTexImage2D(target,
                        0,
                        get_GL_RGBA_value(),
                        getWidth(),
                        getHeight(),
                        0,
                        get_GL_RGBA_value(),
                        get_GL_UNSIGNED_BYTE_value(),
                        bitmap.getRawData());
            else {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Buffer data;
                //if (isETC1Supported()) {
                    data = bitmap.getRawData();
                    int imageSize = data.remaining();
                    glCompressedTexImage2D(target, 0, get_ETC1_RGB8_OES_value(), width, height, 0, imageSize, data);
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
