package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.InputStream;

public class Blender3DObject extends ImportedObject {

    private String objFileName;

    public Blender3DObject(SysUtilsWrapperInterface sysUtilsWrapper, String objFileName, String textureResName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, textureResName, program, mass, tag);

        this.objFileName = objFileName;
    }

    @Override
    protected Raw3DModel getRaw3DModel() {
        return parseObjFile(sysUtilsWrapper.getResourceStream(objFileName));
    }

    private Raw3DModel parseObjFile(InputStream blenderObject) {
        //TODO: parse obj-file
        return null;
    }
}
