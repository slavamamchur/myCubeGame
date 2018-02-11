package com.sadgames.gl3d_engine.gl_render.scene.objects;

import android.graphics.Bitmap;

import com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.gl3d_engine.utils.ISysUtilsWrapper;

import org.springframework.util.StringUtils;

public abstract class BitmapTexturedObject extends GLSceneObject {
    private ISysUtilsWrapper sysUtilsWrapper;

    public BitmapTexturedObject(ISysUtilsWrapper sysUtilsWrapper, GLObjectType type, String textureResName, GLShaderProgram program) {
        super(type, program);

        this.textureResName = textureResName;
        this.sysUtilsWrapper = sysUtilsWrapper;
    }

    public BitmapTexturedObject(ISysUtilsWrapper sysUtilsWrapper, GLObjectType type, int textureResId, GLShaderProgram program) {
        super(type, program);

        this.textureResId = textureResId;
        this.sysUtilsWrapper = sysUtilsWrapper;
    }

    public BitmapTexturedObject(ISysUtilsWrapper sysUtilsWrapper, GLObjectType type, GLShaderProgram program, int textureColor) {
        super(type, program);

        this.textureColor = textureColor;
        this.sysUtilsWrapper = sysUtilsWrapper;
    }

    public ISysUtilsWrapper getSysUtilsWrapper() {
        return sysUtilsWrapper;
    }

    protected abstract int getDimension(Bitmap bmp);

    @Override
    protected Bitmap getTextureBitmap() {
        if (textureResId >= 0) {
            return sysUtilsWrapper.getBitmapFromResource(textureResId);
        }
        else if (StringUtils.hasText(textureResName)) {
            Bitmap result = sysUtilsWrapper.getBitmapFromFile(textureResName);
            result = result != null ? result : sysUtilsWrapper.loadBitmapFromDB(textureResName);

            return result;
        }
        else if (textureColor != 0) {
            return sysUtilsWrapper.createColorBitmap(textureColor);
        }
        else
            return null;
    }

}
