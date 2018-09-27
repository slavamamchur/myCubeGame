package com.sadgames.gl3dengine.glrender.scene.shaders;


import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SKYDOME_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SKYDOME_VERTEX_SHADER;

public class SkyDomeProgram extends SkyBoxProgram {

    public SkyDomeProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return SKYDOME_VERTEX_SHADER;
    }

    @Override
    protected String getFragmentShaderResId() {
        return SKYDOME_FRAGMENT_SHADER;
    }
}
