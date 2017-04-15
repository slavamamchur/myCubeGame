package com.cubegames.slava.cubegame.gl_render.scene.shaders.params;

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
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM;

public class GLShaderParamVBO extends GLShaderParam {

    private int vboPtr;
    private int size;
    private int stride;
    private int pos;

    public GLShaderParamVBO(String paramName, int programId) {
        super(FLOAT_ATTRIB_ARRAY_PARAM, paramName, programId);

        initVBO();
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

    private void initVBO() {
        final int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0);
        vboPtr = buffers[0];
    }

    @Override
    public void setParamValue(int size, int stride, int pos, FloatBuffer data) {
        this.size = size;
        this.stride = stride;
        this.pos = pos;

        if (vboPtr == 0)
            initVBO();

        glBindBuffer(GL_ARRAY_BUFFER, vboPtr);
        glBufferData(GL_ARRAY_BUFFER, data.capacity() * Float.SIZE / 8, data, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void linkParamValue() throws IllegalAccessException {
        if (vboPtr != 0) {
            glBindBuffer(GL_ARRAY_BUFFER, vboPtr);
            glVertexAttribPointer(paramReference, size, GL_FLOAT, false, stride, pos);
            glEnableVertexAttribArray(paramReference);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        else
            throw new IllegalAccessException();
    }

    public void linkParamValue(GLShaderParamVBO newValue) throws IllegalAccessException {
        vboPtr = newValue.getVboPtr();
        paramReference = newValue.paramReference;
        size = newValue.getSize();
        stride = newValue.getStride();
        pos = newValue.getPos();

        linkParamValue();
    }

    public void clearVBOPtr() {
        if (vboPtr != 0) {
            glDeleteBuffers(1, new int[]{vboPtr}, 0);
            vboPtr = 0;
        }
    }

}
