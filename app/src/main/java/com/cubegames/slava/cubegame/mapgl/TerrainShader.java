package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;

import com.cubegames.slava.cubegame.R;

import java.nio.FloatBuffer;

import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.TEXEL_UV_SIZE;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.VERTEX_SIZE;
import static com.cubegames.slava.cubegame.mapgl.GLShaderParam.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.mapgl.GLShaderParam.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.cubegames.slava.cubegame.mapgl.GLShaderParam.GLParamType.INTEGER_UNIFORM_PARAM;

public class TerrainShader extends GLShaderProgram {

    private static final String VERTEXES_PARAM_NAME = "a_Position";
    private static final String TEXELS_PARAM_NAME = "a_Texture";
    private static final String NORMALS_PARAM_NAME = "a_Normal";
    private static final String ACTIVE_TEXTURE_SLOT_PARAM_NAME = "u_TextureUnit";
    private static final String MVP_MATRIX_PARAM_NAME = "u_MVP_Matrix";
    private static final String MV_MATRIX_PARAM_NAME = "u_MV_Matrix";
    private static final String LIGHT_POSITION_PARAM_NAME = "u_lightPosition";
    private static final String CAMERA_POSITION_PARAM_NAME = "u_camera";

    public TerrainShader(Context context) {
        super(context, R.raw.vertex_shader, R.raw.fragment_shader);
    }

    @Override
    public void createParams() {
        int programId = getProgramId();
        GLShaderParam param;

        /** Vertexes array*/
        param = new GLShaderParamVBO(VERTEXES_PARAM_NAME, programId);
        params.put(param.getParamName(), param);

        /** Texels array*/
        param = new GLShaderParamVBO(TEXELS_PARAM_NAME, programId);
        params.put(param.getParamName(), param);

        /** Normals array*/
        param = new GLShaderParamVBO(NORMALS_PARAM_NAME, programId);
        params.put(param.getParamName(), param);

        /** Active texture slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_TEXTURE_SLOT_PARAM_NAME, programId);
        params.put(param.getParamName(), param);

        /** Model-View-Projection matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MVP_MATRIX_PARAM_NAME, programId);
        params.put(param.getParamName(), param);

        /** Model-View- matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MV_MATRIX_PARAM_NAME, programId);
        params.put(param.getParamName(), param);

        /** Light position*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, LIGHT_POSITION_PARAM_NAME, programId);
        params.put(param.getParamName(), param);

        /** Camera position*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, CAMERA_POSITION_PARAM_NAME, programId);
        params.put(param.getParamName(), param);
    }

    public GLShaderParamVBO setVertexData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(VERTEXES_PARAM_NAME);
        paramVBO.setParamValue(VERTEX_SIZE, stride, pos, data);

        return paramVBO;
    }

    public void linkVertexData(GLShaderParamVBO paramVBO) {
        ((GLShaderParamVBO)paramByName(VERTEXES_PARAM_NAME)).linkParamValue(paramVBO);
    }

    public GLShaderParamVBO setTexelData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(TEXELS_PARAM_NAME);
        paramVBO.setParamValue(TEXEL_UV_SIZE, stride, pos, data);

        return paramVBO;
    }

    public void linkTexelData(GLShaderParamVBO paramVBO) {
        ((GLShaderParamVBO)paramByName(TEXELS_PARAM_NAME)).linkParamValue(paramVBO);
    }

    public GLShaderParamVBO setNormalData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(NORMALS_PARAM_NAME);
        paramVBO.setParamValue(VERTEX_SIZE, stride, pos, data);

        return paramVBO;
    }

    public void linkNormalData(GLShaderParamVBO paramVBO) {
        ((GLShaderParamVBO)paramByName(NORMALS_PARAM_NAME)).linkParamValue(paramVBO);
    }

    public void setTextureSlotlData(int data) {
        paramByName(ACTIVE_TEXTURE_SLOT_PARAM_NAME).setParamValue(data);
    }

    public void setCameraData(float[] data) {
        paramByName(CAMERA_POSITION_PARAM_NAME).setParamValue(data);
    }

    public void setLightSourceData(float[] data) {
        paramByName(LIGHT_POSITION_PARAM_NAME).setParamValue(data);
    }

    public void setMVPMatrixData(float[] data) {
        paramByName(MVP_MATRIX_PARAM_NAME).setParamValue(data);
    }

    public void setMVMatrixData(float[] data) {
        paramByName(MV_MATRIX_PARAM_NAME).setParamValue(data);
    }

}
