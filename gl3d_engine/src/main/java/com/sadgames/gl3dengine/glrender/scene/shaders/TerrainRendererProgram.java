package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MAIN_RENDERER_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MAIN_RENDERER_VERTEX_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.RND_SEED__PARAM_NAME;

public class TerrainRendererProgram extends VBOShaderProgram {


    public TerrainRendererProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return MAIN_RENDERER_VERTEX_SHADER;
    }
    @Override
    protected String getFragmentShaderResId() {
        return MAIN_RENDERER_FRAGMENT_SHADER;
    }

    @Override
    protected void createUniforms() {
        super.createUniforms();

        /** Random generator seed */
        GLShaderParam param = new GLShaderParam(FLOAT_UNIFORM_PARAM, RND_SEED__PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

}
