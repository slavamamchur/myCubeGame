package com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.shaders;

import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl3d_engine.utils.ISysUtilsWrapper;

import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.RND_SEED__PARAM_NAME;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.TERRAIN_FRAGMENT_SHADER;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.TERRAIN_VERTEX_SHADER;

public class TerrainRendererProgram extends VBOShaderProgram {


    public TerrainRendererProgram(ISysUtilsWrapper sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return TERRAIN_VERTEX_SHADER;
    }
    @Override
    protected String getFragmentShaderResId() {
        return TERRAIN_FRAGMENT_SHADER;
    }

    @Override
    protected void createUniforms() {
        super.createUniforms();

        /** Random generator seed */
        GLShaderParam param = new GLShaderParam(FLOAT_UNIFORM_PARAM, RND_SEED__PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    public void setWaveMovingFactor(float moveFactor) {
        paramByName(RND_SEED__PARAM_NAME).setParamValue(moveFactor);
    }

}
