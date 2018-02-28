package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

public class ImportedObject extends GameItemObject {

    private Raw3DModel raw3DModel = null;

    public ImportedObject(SysUtilsWrapperInterface sysUtilsWrapper, String textureResName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, textureResName, program, mass, tag);
    }

    public ImportedObject(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, int color, float mass, int tag) {
        super(sysUtilsWrapper, program, color, mass, tag);
    }

    @Override
    public int getFacesCount() {
        return raw3DModel == null ? 0 : raw3DModel.getFaces().length;
    }

    @Override protected float[] getVertexesArray() {
        return raw3DModel.getVertexes();
    }
    @Override protected float[] getNormalsArray() {
        return raw3DModel.getNormals();
    }
    @Override protected short[] getFacesArray() {
        return raw3DModel.getFaces();
    }

    @Override
    protected void createVertexesVBO() {

    }

    @Override
    protected void createTexelsVBO() {

    }

}
