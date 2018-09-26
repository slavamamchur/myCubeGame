package com.sadgames.gl3dengine.glrender.scene.shaders.params;

import java.nio.FloatBuffer;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_ARRAY_BUFFER_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_FLOAT_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_STATIC_DRAW_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glBindBuffer;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glBufferData;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDeleteBuffers;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableVertexAttribArray;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glGenBuffers;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glVertexAttribPointer;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM;

public class GLShaderParamVBO extends GLShaderParam {

    private int vboPtr;

    public GLShaderParamVBO(String paramName, int programId) {
        super(FLOAT_ATTRIB_ARRAY_PARAM, paramName, programId);

        initVBO();
    }

    public int getVboPtr() {
        return vboPtr;
    }

    private void initVBO() {
        final int buffers[] = new int[1];
        glGenBuffers(1, buffers);
        vboPtr = buffers[0];
    }

    @Override
    public void setParamValue(int size, int stride, int pos, FloatBuffer data) {
        super.setParamValue(size, stride, pos, null);

        if (vboPtr == 0)
            initVBO();

        glBindBuffer(get_GL_ARRAY_BUFFER_value(), vboPtr);
        glBufferData(get_GL_ARRAY_BUFFER_value(), data.capacity() * Float.SIZE / 8, data, get_GL_STATIC_DRAW_value());
        glBindBuffer(get_GL_ARRAY_BUFFER_value(), 0);
    }

    @Override
    protected void copyBaseParamData(GLShaderParam newValue) {
        super.copyBaseParamData(newValue);

        vboPtr = ((GLShaderParamVBO)newValue).getVboPtr();
    }

    @Override
    public void linkParamValue(GLShaderParam newValue) throws IllegalAccessException {
        if (
                !(newValue instanceof GLShaderParamVBO)
                || (((GLShaderParamVBO)newValue).getVboPtr() == 0)
            ) throw new IllegalAccessException();

        copyBaseParamData(newValue);
        internalLinkParamValue();
    }

    @Override
    protected void internalLinkParamValue() {
        glBindBuffer(get_GL_ARRAY_BUFFER_value(), vboPtr);
        glVertexAttribPointer(paramReference, size, get_GL_FLOAT_value(), false, stride, pos);
        glEnableVertexAttribArray(paramReference);
        glBindBuffer(get_GL_ARRAY_BUFFER_value(), 0);
    }

    public void clearParamDataVBO() {
        clearParamData();

        if (vboPtr != 0) {
            glDeleteBuffers(new int[]{vboPtr});
            vboPtr = 0;
        }
    }

}
