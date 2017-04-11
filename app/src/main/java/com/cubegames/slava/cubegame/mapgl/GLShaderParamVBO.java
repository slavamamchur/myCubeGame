package com.cubegames.slava.cubegame.mapgl;

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

public class GLShaderParamVBO extends GLShaderParam {

    private int vboPtr;
    private int size;
    private int stride;
    private int pos;

    public GLShaderParamVBO(String paramName, int programId) {
        super(GLParamType.FLOAT_ATTRIB_ARRAY_PARAM, paramName, programId);

        final int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0);
        vboPtr = buffers[0];
    }

    public int getVboPtr() {
        return vboPtr;
    }
    public int getSize() {
        return size;
    }
    public int getStride() {
        return stride;
    }
    public int getPos() {
        return pos;
    }

    @Override
    public void setParamValue(int size, int stride, int pos, FloatBuffer data) {
        this.size = size;
        this.stride = stride;
        this.pos = pos;

        glBindBuffer(GL_ARRAY_BUFFER, vboPtr);
        glBufferData(GL_ARRAY_BUFFER, data.capacity() * 4, data, GL_STATIC_DRAW);

        //glVertexAttribPointer(paramReference, size, GL_FLOAT, false, stride, pos);
        //glEnableVertexAttribArray(paramReference);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void linkParamValue() {
        glBindBuffer(GL_ARRAY_BUFFER, vboPtr);
        glVertexAttribPointer(paramReference, size, GL_FLOAT, false, stride, pos);
        glEnableVertexAttribArray(paramReference);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void linkParamValue(GLShaderParamVBO newValue) {
        vboPtr = newValue.getVboPtr();
        paramReference = newValue.paramReference;
        size = newValue.getSize();
        stride = newValue.getStride();
        pos = newValue.getPos();

        linkParamValue();
    }

    public void clearParamBuffer() {
        glDeleteBuffers(1, new int[] {vboPtr}, 0); ///???
    }

}
