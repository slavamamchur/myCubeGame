package com.sadgames.gl3dengine.glrender.scene.objects;

import com.badlogic.gdx.graphics.Pixmap;
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;

public abstract class BitmapTexturedObject extends AbstractGL3DObject {

    //public static final int DEFAULT_TEXTURE_SIZE = 500;

    public BitmapTexturedObject(GLObjectType type, String textureResName, GLShaderProgram program) {
        super(type, program);

        this.textureResName = textureResName;
    }

    public BitmapTexturedObject(GLObjectType type, GLShaderProgram program, int textureColor) {
        super(type, program);

        this.textureResName = String.valueOf(textureColor);
    }

    protected abstract int getDimension(Pixmap bmp);
}
