package com.sadgames.gl3d_engine.gl_render.scene.objects;

import android.graphics.Bitmap;

import com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.ISysUtilsWrapper;

public abstract class BitmapTexturedObject extends GLSceneObject {

    public BitmapTexturedObject(ISysUtilsWrapper sysUtilsWrapper, GLObjectType type, String textureResName, GLShaderProgram program) {
        super(sysUtilsWrapper, type, program);

        this.textureResName = textureResName;
    }

    public BitmapTexturedObject(ISysUtilsWrapper sysUtilsWrapper, GLObjectType type, GLShaderProgram program, int textureColor) {
        super(sysUtilsWrapper, type, program);

        this.textureColor = textureColor;
    }

    protected abstract int getDimension(Bitmap bmp);
}
