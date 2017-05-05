package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.R;

public class WaterShader extends VBOShaderProgram {

    public WaterShader(Context context) {
        super(context);
    }

    @Override
    protected int getVertexShaderResId() {
        return R.raw.vertex_shader;
    }

    @Override
    protected int getFragmentShaderResId() {
        return R.raw.fragment_shader_w;
    }
}
