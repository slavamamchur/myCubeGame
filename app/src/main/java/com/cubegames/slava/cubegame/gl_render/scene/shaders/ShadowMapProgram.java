package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;

import static android.opengl.GLES20.GL_EXTENSIONS;
import static android.opengl.GLES20.glGetString;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.OES_DEPTH_TEXTURE_EXTENSION;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEXES_PARAM_NAME;

public class ShadowMapProgram extends GLShaderProgram {

    public ShadowMapProgram(Context context) {
        super(context);
    }

    @Override
    protected int getVertexShaderResId() {
        return !checkDepthTextureExtension() ? R.raw.v_depth_map : R.raw.depth_tex_v_depth_map;
    }

    @Override
    protected int getFragmentShaderResId() {
        return !checkDepthTextureExtension() ? R.raw.f_depth_map : R.raw.depth_tex_f_depth_map;
    }

    @Override
    public void createParams() {
        GLShaderParam param;

        /** Vertexes array*/
        param = new GLShaderParamVBO(VERTEXES_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Model-View-Projection matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MVP_MATRIX_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    private boolean checkDepthTextureExtension() {
        return glGetString(GL_EXTENSIONS).contains(OES_DEPTH_TEXTURE_EXTENSION);
    }

    @Override
    public void linkVertexData(GLShaderParam param) throws IllegalAccessException {
        paramByName(VERTEXES_PARAM_NAME).linkParamValue(param);
    }

    @Override
    public void linkVBOData(GLSceneObject object) {
        try {
            linkVertexData(object.getVertexVBO());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMVMatrixData(float[] data) {}

    @Override
    public void setMVPMatrixData(float[] data) {
        paramByName(MVP_MATRIX_PARAM_NAME).setParamValue(data);
    }

}
