package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;

public abstract class ImportedObject extends GameItemObject {

    private Raw3DModel raw3DModel = null;

    public ImportedObject(SysUtilsWrapperInterface sysUtilsWrapper, String textureResName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, textureResName, program, mass, tag);
    }

    public ImportedObject(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, int color, float mass, int tag) {
        super(sysUtilsWrapper, program, color, mass, tag);
    }

    protected abstract Raw3DModel getRaw3DModel();

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
    protected float[] getTextureCoordsArray() {
        return raw3DModel.getTextureCoords();
    }

    @Override
    protected void createVertexesVBO() {
        raw3DModel = getRaw3DModel();

        float[] vertexes = getVertexesArray();

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertexes);
        vertexData.position(0);

        getVertexVBO().setParamValue(VERTEX_SIZE, 0, 0, vertexData);
        vertexData.limit(0);

        createCollisionShape(vertexes);
    }

    @Override
    protected void createTexelsVBO() {
        float[] textureCoords = getTextureCoordsArray();

        FloatBuffer textureCoordsData = ByteBuffer
                .allocateDirect(textureCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureCoordsData.put(textureCoords);
        textureCoordsData.position(0);

        getTexelVBO().setParamValue(TEXEL_UV_SIZE, 0, 0, textureCoordsData);
        textureCoordsData.limit(0);
    }

}
