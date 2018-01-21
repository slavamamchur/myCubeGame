package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;
import android.opengl.Matrix;

import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDetachShader;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;
import static com.cubegames.slava.cubegame.Utils.readTextFromRaw;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_POSITIONF_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MV_MATRIXF_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MV_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXEL_UV_SIZE;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEXES_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEX_SIZE;

//import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.RND_SEED__PARAM_NAME;

public abstract class GLShaderProgram {

    private int programId;
    private int vertexShaderID;
    private int fragmentShaderID;
    protected Map<String, GLShaderParam> params = new HashMap<>();

    public GLShaderProgram(Context context) {

        vertexShaderID = createShader(context, GL_VERTEX_SHADER, getVertexShaderResId());
        fragmentShaderID = createShader(context, GL_FRAGMENT_SHADER, getFragmentShaderResId());
        programId = createProgram(vertexShaderID, fragmentShaderID);

        createParams();
    }

    protected abstract int getVertexShaderResId();
    protected abstract int getFragmentShaderResId();

    public int getProgramId() {
        return programId;
    }
    public Map<String, GLShaderParam> getParams() {
        return params;
    }

    public void useProgram() {
        glUseProgram(programId);
    }

    public void deleteProgram() {
        glDetachShader(programId, vertexShaderID);
        glDetachShader(programId, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programId);
    }

    public GLShaderParam paramByName (String name) {
        return params.get(name);
    }

    public void createParams() {
        GLShaderParam param;

        /** Vertexes array*/
        param = new GLShaderParam(FLOAT_ATTRIB_ARRAY_PARAM, VERTEXES_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Texels array*/
        param = new GLShaderParam(FLOAT_ATTRIB_ARRAY_PARAM, TEXELS_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Normals array*/
        param = new GLShaderParam(FLOAT_ATTRIB_ARRAY_PARAM, NORMALS_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active texture slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_TEXTURE_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Model-View-Projection matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MVP_MATRIX_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Model-View- matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MV_MATRIX_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Light position*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, LIGHT_POSITION_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Camera position*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, CAMERA_POSITION_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    public void setVertexData(FloatBuffer data, int stride, int pos) {
        paramByName(VERTEXES_PARAM_NAME).setParamValue(VERTEX_SIZE, stride, pos, data);
    }

    public void linkVertexData(GLShaderParam param) throws IllegalAccessException {
        paramByName(VERTEXES_PARAM_NAME).linkParamValue(param);
    }

    public void setTexelData(FloatBuffer data, int stride, int pos) {
        paramByName(TEXELS_PARAM_NAME).setParamValue(TEXEL_UV_SIZE, stride, pos, data);
    }

    public void linkTexelData(GLShaderParam param) throws IllegalAccessException {
        paramByName(TEXELS_PARAM_NAME).linkParamValue(param);
    }

    public void setNormalData(FloatBuffer data, int stride, int pos) {
        paramByName(NORMALS_PARAM_NAME).setParamValue(VERTEX_SIZE, stride, pos, data);
    }

    public void linkNormalData(GLShaderParam param) throws IllegalAccessException {
        paramByName(NORMALS_PARAM_NAME).linkParamValue(param);
    }

    public void setTextureSlotData(int data) {
        paramByName(ACTIVE_TEXTURE_SLOT_PARAM_NAME).setParamValue(data);
    }

    public void setCameraData(float[] data) {
        paramByName(CAMERA_POSITION_PARAM_NAME).setParamValue(data);
    }

    public void setLightSourceData(float[] data) {
        paramByName(LIGHT_POSITION_PARAM_NAME).setParamValue(data);
        paramByName(LIGHT_POSITIONF_PARAM_NAME).setParamValue(data);
    }

    public void setMVPMatrixData(float[] data) {
        paramByName(MVP_MATRIX_PARAM_NAME).setParamValue(data);
    }

    public void setMVMatrixData(float[] data) {
        paramByName(MV_MATRIX_PARAM_NAME).setParamValue(data);
        paramByName(MV_MATRIXF_PARAM_NAME).setParamValue(data);
    }

    public void bindMVPMatrix(GLSceneObject object, float[] viewMatrix, float[] projectionMatrix) {
        float[] mMatrix = new float[16];

        Matrix.multiplyMM(mMatrix, 0, viewMatrix, 0, object.getModelMatrix(), 0);
        setMVMatrixData(mMatrix);

        Matrix.multiplyMM(mMatrix, 0, projectionMatrix, 0, mMatrix, 0);
        setMVPMatrixData(mMatrix);
    }

    public void linkVBOData(GLSceneObject object) {
        try {
            linkVertexData(object.getVertexVBO());
            linkTexelData(object.getTexelVBO());
            linkNormalData(object.getNormalVBO());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /** Utils-------------------------------------------------------------------------------------*/
    public static int createProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = glCreateProgram();
        if (programId == 0) {
            return 0;
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            return 0;
        }
        return programId;
    }

    public static int createShader(Context context, int type, int shaderRawId) {
        return createShader(type, readTextFromRaw(context, shaderRawId));
    }

    static int createShader(int type, String shaderText) {
        final int shaderId = glCreateShader(type);
        if (shaderId == 0) {
            return 0;
        }
        glShaderSource(shaderId, shaderText);
        glCompileShader(shaderId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId);
            return 0;
        }
        return shaderId;
    }
}
