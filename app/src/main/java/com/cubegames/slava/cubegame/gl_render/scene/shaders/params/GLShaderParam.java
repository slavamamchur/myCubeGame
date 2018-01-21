package com.cubegames.slava.cubegame.gl_render.scene.shaders.params;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;

public class GLShaderParam {

    private int programId;
    private String paramName;

    protected GLParamType paramType;
    protected int paramReference;
    protected int size;
    protected int stride;
    protected int pos;


    //RAM Param
    private FloatBuffer data = null;

    public GLShaderParam(GLParamType paramType, String paramName, int programId) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.programId = programId;

        paramReference = getParamReference();
    }

    public String getParamName() {
        return paramName;
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

    private int getParamReference () {
        if (paramType.equals(FLOAT_ATTRIB_ARRAY_PARAM))
            return glGetAttribLocation(programId, paramName);
        else
            return glGetUniformLocation(programId, paramName);
    }

    public void setParamValue(int size, int stride, int pos, FloatBuffer data) {
        if (!paramType.equals(FLOAT_ATTRIB_ARRAY_PARAM)) throw new IllegalArgumentException();

        this.size = size;
        this.stride = stride;
        this.pos = pos;

        clearParamData();
        this.data = data;
    }

    protected void copyBaseParamData(GLShaderParam newValue) {
        if (
                !newValue.paramType.equals(FLOAT_ATTRIB_ARRAY_PARAM)
                || !paramType.equals(FLOAT_ATTRIB_ARRAY_PARAM)
            ) throw new IllegalArgumentException();

        size = newValue.size;
        stride = newValue.stride;
        pos = newValue.pos;

        clearParamData();
        data = newValue.data;
    }

    public void linkParamValue(GLShaderParam newValue) throws IllegalAccessException {
        copyBaseParamData(newValue);
        internalLinkParamValue();
    }

    protected void internalLinkParamValue() {
        glVertexAttribPointer(paramReference, size, GL_FLOAT, false, stride, data);
        glEnableVertexAttribArray(paramReference);
    }

    public void setParamValue(float[] data) {
        if (paramType.equals(FLOAT_UNIFORM_VECTOR_PARAM) && (data.length == 4 || data.length == 3))
            glUniform3fv(paramReference, 1, data, 0);
        else if (paramType.equals(FLOAT_UNIFORM_MATRIX_PARAM) && (data.length == 16))
            glUniformMatrix4fv(paramReference, 1, false, data, 0);
        else
            throw new IllegalArgumentException();
    }

    public void setParamValue(int data) {
        if (paramType.equals(INTEGER_UNIFORM_PARAM))
            glUniform1i(paramReference, data);
        else
            throw new IllegalArgumentException();
    }

    public void setParamValue(float data) {
        if (paramType.equals(FLOAT_UNIFORM_PARAM))
            glUniform1f(paramReference, data);
        else
            throw new IllegalArgumentException();
    }

    public void clearParamData() {
        if (data != null) {
            data.limit(0);
            data = null;
        }
    }
}
