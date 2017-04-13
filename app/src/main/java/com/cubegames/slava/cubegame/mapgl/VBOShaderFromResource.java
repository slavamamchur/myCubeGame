package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.MV_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.TEXEL_UV_SIZE;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.VERTEXES_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.VERTEX_SIZE;

public class VBOShaderFromResource extends GLShaderProgram {

    public VBOShaderFromResource(Context context, int vShaderId, int fShaderId) {
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

    public GLShaderParamVBO setVertexData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(VERTEXES_PARAM_NAME);
        paramVBO.setParamValue(VERTEX_SIZE, stride, pos, data);

        return paramVBO;
    }

    public void linkVertexData(GLShaderParamVBO paramVBO) throws IllegalAccessException {
        ((GLShaderParamVBO)paramByName(VERTEXES_PARAM_NAME)).linkParamValue(paramVBO);
    }

    public GLShaderParamVBO setTexelData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(TEXELS_PARAM_NAME);
        paramVBO.setParamValue(TEXEL_UV_SIZE, stride, pos, data);

        return paramVBO;
    }

    public void linkTexelData(GLShaderParamVBO paramVBO) throws IllegalAccessException {
        ((GLShaderParamVBO)paramByName(TEXELS_PARAM_NAME)).linkParamValue(paramVBO);
    }

    public GLShaderParamVBO setNormalData(FloatBuffer data, int stride, int pos) {
        GLShaderParamVBO paramVBO = (GLShaderParamVBO) paramByName(NORMALS_PARAM_NAME);
        paramVBO.setParamValue(VERTEX_SIZE, stride, pos, data);

        return paramVBO;
    }

    public void linkNormalData(GLShaderParamVBO paramVBO) throws IllegalAccessException {
        ((GLShaderParamVBO)paramByName(NORMALS_PARAM_NAME)).linkParamValue(paramVBO);
    }

    public void linkVBOData(GLSceneObject object) throws IllegalAccessException {
        linkVertexData(object.getVertexVBO());
        linkTexelData(object.getTexelVBO());
        linkNormalData(object.getNormalVBO());
    }

    public void setTextureSlotData(int data) {
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

    @Override
    public void bindMatrix(GLSceneObject object, GLCamera camera) {
        float[] mMatrix = new float[16];

        Matrix.multiplyMM(mMatrix, 0, camera.getmViewMatrix(), 0, object.getModelMatrix(), 0);
        setMVMatrixData(mMatrix);

        Matrix.multiplyMM(mMatrix, 0, camera.getProjectionMatrix(), 0, mMatrix, 0);
        setMVPMatrixData(mMatrix);
    }
}
