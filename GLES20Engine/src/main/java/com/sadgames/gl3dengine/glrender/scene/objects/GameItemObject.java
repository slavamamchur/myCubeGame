package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_ELEMENT_ARRAY_BUFFER_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_STATIC_DRAW_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glBindBuffer;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glBufferData;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glGenBuffers;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.GAME_ITEM_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VBO_STRIDE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;

public abstract class GameItemObject extends PNodeObject {

    public GameItemObject(SysUtilsWrapperInterface sysUtilsWrapper, String textureResName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, GAME_ITEM_OBJECT, textureResName, program, mass, tag);
    }

    public GameItemObject(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, int color, float mass, int tag) {
        super(sysUtilsWrapper, GAME_ITEM_OBJECT, program, color, mass, tag);
    }

    protected abstract float[] getVertexesArray();
    protected abstract float[] getNormalsArray();
    protected abstract short[] getFacesArray();

    @Override
    protected int getDimension(BitmapWrapperInterface bmp) {
        return 0;
    }

    @Override
    protected void createVertexesVBO() {
        float[] vertexes = getVertexesArray();

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertexes);
        vertexData.position(0);

        getVertexVBO().setParamValue(VERTEX_SIZE, VBO_STRIDE, 0, vertexData);
        getTexelVBO().setParamValue(TEXEL_UV_SIZE, VBO_STRIDE, VERTEX_SIZE * 4, vertexData);

        vertexData.limit(0);

        createCollisionShape(vertexes);
    }

    @Override
    protected void createTexelsVBO() {}

    @Override
    protected void createNormalsVBO() {
        float[] normal = getNormalsArray();

        FloatBuffer normalData = ByteBuffer
                .allocateDirect(normal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalData.put(normal);
        normalData.position(0);

        getNormalVBO().setParamValue(VERTEX_SIZE, 0, 0, normalData);

        normalData.limit(0);
    }

    @Override
    protected int createFacesIBO() {
        short[] index = getFacesArray();

        if (index == null)
            return 0;
        else {
            ShortBuffer indexData = ByteBuffer
                    .allocateDirect(index.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
            indexData.put(index);

            final int buffers[] = new int[1];
            glGenBuffers(1, buffers);
            glBindBuffer(get_GL_ELEMENT_ARRAY_BUFFER_value(), buffers[0]);
            indexData.position(0);
            glBufferData(get_GL_ELEMENT_ARRAY_BUFFER_value(), indexData.capacity() * 2, indexData, get_GL_STATIC_DRAW_value());
            glBindBuffer(get_GL_ELEMENT_ARRAY_BUFFER_value(), 0);

            indexData.limit(0);
            indexData = null;
            setIndexData(indexData);

            return buffers[0];
        }
    }

}
