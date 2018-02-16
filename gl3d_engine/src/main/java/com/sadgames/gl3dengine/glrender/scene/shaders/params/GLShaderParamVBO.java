package com.sadgames.gl3dengine.glrender.scene.shaders.params;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
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
        glGenBuffers(1, buffers, 0);
        vboPtr = buffers[0];
    }

    @Override
    public void setParamValue(int size, int stride, int pos, FloatBuffer data) {
        super.setParamValue(size, stride, pos, null);

        if (vboPtr == 0)
            initVBO();

        glBindBuffer(GL_ARRAY_BUFFER, vboPtr);
        glBufferData(GL_ARRAY_BUFFER, data.capacity() * Float.SIZE / 8, data, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
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
        glBindBuffer(GL_ARRAY_BUFFER, vboPtr);
        glVertexAttribPointer(paramReference, size, GL_FLOAT, false, stride, pos);
        glEnableVertexAttribArray(paramReference);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void clearParamDataVBO() {
        clearParamData();

        if (vboPtr != 0) {
            glDeleteBuffers(1, new int[]{vboPtr}, 0);
            vboPtr = 0;
        }
    }

}
