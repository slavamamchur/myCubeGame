package com.sadgames.gl3d_engine.gl_render.scene.objects;

import com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.BitmapWrapperInterface;
import com.sadgames.sysutils.SysUtilsWrapperInterface;

public abstract class BitmapTexturedObject extends AbstractGL3DObject {

    public BitmapTexturedObject(SysUtilsWrapperInterface sysUtilsWrapper, GLObjectType type, String textureResName, GLShaderProgram program) {
        super(sysUtilsWrapper, type, program);

        this.textureResName = textureResName;
    }

    public BitmapTexturedObject(SysUtilsWrapperInterface sysUtilsWrapper, GLObjectType type, GLShaderProgram program, int textureColor) {
        super(sysUtilsWrapper, type, program);

        this.textureColor = textureColor;
    }

    protected abstract int getDimension(BitmapWrapperInterface bmp);
}
