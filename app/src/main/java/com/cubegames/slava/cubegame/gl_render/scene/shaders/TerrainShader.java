package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.RND_SEED__PARAM_NAME;

public class TerrainShader extends VBOShaderProgram {

    public TerrainShader(Context context) {
        super(context);
    }

    @Override
    protected int getVertexShaderResId() {
        return R.raw.vertex_shader;
    }
    @Override
    protected int getFragmentShaderResId() {
        return R.raw.fragment_shader;
    }

    @Override
    public void createParams() {
        super.createParams();

        /** Random generator seed */
        GLShaderParam param = new GLShaderParam(FLOAT_UNIFORM_PARAM, RND_SEED__PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    public void setWaveMovingFactor(float moveFactor) {
        paramByName(RND_SEED__PARAM_NAME).setParamValue(moveFactor);
    }

}
