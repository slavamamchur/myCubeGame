package com.sadgames.gl3dengine.glrender.scene.shaders.params;

import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;

import java.nio.FloatBuffer;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_FLOAT_value;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;

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

    public int getParamReference () {
        if (paramType.equals(FLOAT_ATTRIB_ARRAY_PARAM))
            return GLES20JniWrapper.glGetAttribLocation(programId, paramName);
        else
            return GLES20JniWrapper.glGetUniformLocation(programId, paramName);

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
        GLES20JniWrapper.glVertexAttribPointer(paramReference, size, get_GL_FLOAT_value(), false, stride, data);
        GLES20JniWrapper.glEnableVertexAttribArray(paramReference);
    }

    public void setParamValue(float[] data) {
        if (paramType.equals(FLOAT_UNIFORM_VECTOR_PARAM) && (data.length == 4 || data.length == 3))
            GLES20JniWrapper.glUniform3fv(paramReference, 1, data);
        else if (paramType.equals(FLOAT_UNIFORM_MATRIX_PARAM) && (data.length == 16))
            GLES20JniWrapper.glUniformMatrix4fv(paramReference, 1, false, data);
        else
            throw new IllegalArgumentException();
    }

    public void setParamValue(int data) {
        if (paramType.equals(INTEGER_UNIFORM_PARAM))
            GLES20JniWrapper.glUniform1i(paramReference, data);
        else
            throw new IllegalArgumentException();
    }

    public void setParamValue(float data) {
        if (paramType.equals(FLOAT_UNIFORM_PARAM))
            GLES20JniWrapper.glUniform1f(paramReference, data);
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
