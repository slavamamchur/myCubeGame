package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;

import java.nio.FloatBuffer;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MV_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXEL_UV_SIZE;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEXES_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEX_SIZE;

public class VBOShader extends GLShaderProgram {

    public VBOShader(Context context, int vShaderId, int fShaderId) {
        super(context, vShaderId, fShaderId);
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

    @Override
    public void setVertexData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(VERTEXES_PARAM_NAME);
        paramVBO.setParamValue(VERTEX_SIZE, stride, pos, data);
    }

    @Override
    public void linkVertexData(GLShaderParam param) throws IllegalAccessException {
        if (param instanceof GLShaderParamVBO)
            ((GLShaderParamVBO)paramByName(VERTEXES_PARAM_NAME)).linkParamValue((GLShaderParamVBO)param);
        else
            throw new IllegalAccessException();
    }

    public void setTexelData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(TEXELS_PARAM_NAME);
        paramVBO.setParamValue(TEXEL_UV_SIZE, stride, pos, data);
    }

    @Override
    public void linkTexelData(GLShaderParam param) throws IllegalAccessException {
        if (param instanceof GLShaderParamVBO)
            ((GLShaderParamVBO)paramByName(TEXELS_PARAM_NAME)).linkParamValue((GLShaderParamVBO)param);
        else
            throw new IllegalAccessException();
    }

    @Override
    public void setNormalData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(NORMALS_PARAM_NAME);
        paramVBO.setParamValue(VERTEX_SIZE, stride, pos, data);
    }

    @Override
    public void linkNormalData(GLShaderParam param) throws IllegalAccessException {
        if (param instanceof GLShaderParamVBO)
            ((GLShaderParamVBO)paramByName(NORMALS_PARAM_NAME)).linkParamValue((GLShaderParamVBO)param);
        else
            throw new IllegalAccessException();
    }

    @Override
    public void setTextureSlotData(int data) {
        paramByName(ACTIVE_TEXTURE_SLOT_PARAM_NAME).setParamValue(data);
    }

    @Override
    public void setCameraData(float[] data) {
        paramByName(CAMERA_POSITION_PARAM_NAME).setParamValue(data);
    }

    @Override
    public void setLightSourceData(float[] data) {
        paramByName(LIGHT_POSITION_PARAM_NAME).setParamValue(data);
    }

    @Override
    public void setMVPMatrixData(float[] data) {
        paramByName(MVP_MATRIX_PARAM_NAME).setParamValue(data);
    }

    @Override
    public void setMVMatrixData(float[] data) {
        paramByName(MV_MATRIX_PARAM_NAME).setParamValue(data);
    }

}
