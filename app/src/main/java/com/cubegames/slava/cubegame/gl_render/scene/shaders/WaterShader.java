package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.RND_SEED__PARAM_NAME;

public class WaterShader extends VBOShaderProgram {

    public WaterShader(Context context) {
        super(context);
    }

    @Override
    protected int getVertexShaderResId() {
        return R.raw.vertex_shader_w;
    }

    @Override
    protected int getFragmentShaderResId() {
        return R.raw.fragment_shader_w;
    }

    @Override
    public void createParams() {
        super.createParams();

        GLShaderParam param;
        /** Random generator seed */
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, RND_SEED__PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    public void setRndSeedData(float data) { //1.93984269E9 //1.34289536E9
        paramByName(RND_SEED__PARAM_NAME).setParamValue(data);
    }
}
