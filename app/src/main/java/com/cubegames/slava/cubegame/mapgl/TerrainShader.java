package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;

import com.cubegames.slava.cubegame.R;

public class TerrainShader extends VBOShaderFromResource {

    public TerrainShader(Context context) {
        super(context, R.raw.vertex_shader, R.raw.fragment_shader);
    }
}
