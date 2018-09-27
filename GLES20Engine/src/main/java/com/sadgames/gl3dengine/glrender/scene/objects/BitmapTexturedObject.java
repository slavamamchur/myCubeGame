package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

public abstract class BitmapTexturedObject extends AbstractGL3DObject {

    //public static final int DEFAULT_TEXTURE_SIZE = 500;

    public BitmapTexturedObject(SysUtilsWrapperInterface sysUtilsWrapper, GLObjectType type, String textureResName, GLShaderProgram program) {
        super(sysUtilsWrapper, type, program);

        this.textureResName = textureResName;
    }

    public BitmapTexturedObject(SysUtilsWrapperInterface sysUtilsWrapper, GLObjectType type, GLShaderProgram program, int textureColor) {
        super(sysUtilsWrapper, type, program);

        this.textureResName = String.valueOf(textureColor);
    }

    protected abstract int getDimension(BitmapWrapperInterface bmp);
}
