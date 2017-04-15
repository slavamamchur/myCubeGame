package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.R;

public class TerrainShader extends VBOShader {

    public TerrainShader(Context context) {
        super(context, R.raw.vertex_shader, R.raw.fragment_shader);
    }
}
