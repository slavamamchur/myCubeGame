package com.cubegames.slava.cubegame.mapgl;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

public class GLShaderParam {

    enum GLParamType {
        FLOAT_ATTRIB_ARRAY_PARAM,
        FLOAT_UNIFORM_VECTOR_PARAM,
        FLOAT_UNIFORM_MATRIX_PARAM,
        INTEGER_UNIFORM_PARAM
    }

    private int programId;
    private GLParamType paramType;
    private String paramName;
    protected int paramReference;

    public GLShaderParam(GLParamType paramType, String paramName, int programId) {
        this.paramType = paramType;
        this.paramName = paramName;
        this.programId = programId;

        paramReference = getParamReference();
    }

    public String getParamName() {
        return paramName;
    }


    private int getParamReference () {
        if (paramType.equals(GLParamType.FLOAT_ATTRIB_ARRAY_PARAM))
            return glGetAttribLocation(programId, paramName);
        else
            return glGetUniformLocation(programId, paramName);
    }

    public void setParamValue(int size, int stride, int pos, FloatBuffer data) {
        if (paramType.equals(GLParamType.FLOAT_ATTRIB_ARRAY_PARAM)) {
            glVertexAttribPointer(paramReference, size, GL_FLOAT, false, stride, data);
            glEnableVertexAttribArray(paramReference);
        }
        else
            throw new IllegalArgumentException();
    }

    public void setParamValue(float[] data) {
        if (paramType.equals(GLParamType.FLOAT_UNIFORM_VECTOR_PARAM) && (data.length == 4))
            glUniform3fv(paramReference, 1, data, 0);
        else if (paramType.equals(GLParamType.FLOAT_UNIFORM_MATRIX_PARAM) && (data.length == 16))
            glUniformMatrix4fv(paramReference, 1, false, data, 0);
        else
            throw new IllegalArgumentException();
    }

    public void setParamValue(int data) {
        if (paramType.equals(GLParamType.INTEGER_UNIFORM_PARAM))
            glUniform1i(paramReference, 0);
        else
            throw new IllegalArgumentException();
    }
}
