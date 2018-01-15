package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEXES_PARAM_NAME;

public class ShadowMapProgram extends GLShaderProgram {

    private boolean hasDepthTextureExtension;

    public ShadowMapProgram(Context context, boolean hasDepthTextureExtension) {
        super(context);

        this.hasDepthTextureExtension = hasDepthTextureExtension;
    }

    @Override
    protected int getVertexShaderResId() {
        return !hasDepthTextureExtension ? R.raw.v_depth_map : R.raw.depth_tex_v_depth_map;
    }

    @Override
    protected int getFragmentShaderResId() {
        return !hasDepthTextureExtension ? R.raw.f_depth_map : R.raw.depth_tex_f_depth_map;
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

}
