package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParamVBO;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEXES_PARAM_NAME;

public abstract class VBOShaderProgram extends GLShaderProgram {

    public VBOShaderProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected void createAttributes() {
        GLShaderParam param;

        /** Vertexes array*/
        param = new GLShaderParamVBO(VERTEXES_PARAM_NAME, getProgramId());
        if (param.getParamReference() >= 0)
            params.put(param.getParamName(), param);

        /** Texels array*/
        param = new GLShaderParamVBO(TEXELS_PARAM_NAME, getProgramId());
        if (param.getParamReference() >= 0)
            params.put(param.getParamName(), param);

        /** Normals array*/
        param = new GLShaderParamVBO(NORMALS_PARAM_NAME, getProgramId());
        if (param.getParamReference() >= 0)
            params.put(param.getParamName(), param);
    }

}
