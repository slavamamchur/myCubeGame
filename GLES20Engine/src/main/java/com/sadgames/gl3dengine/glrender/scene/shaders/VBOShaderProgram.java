package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParamVBO;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEXES_PARAM_NAME;

public abstract class VBOShaderProgram extends GLShaderProgram {

    VBOShaderProgram() {
        super();
    }

    @Override
    protected void createAttributes() {
        GLShaderParam param;

        param = new GLShaderParamVBO(VERTEXES_PARAM_NAME, getProgramId());
        if (param.getParamReference() >= 0)
            params.put(param.getParamName(), param);

        param = new GLShaderParamVBO(TEXELS_PARAM_NAME, getProgramId());
        if (param.getParamReference() >= 0)
            params.put(param.getParamName(), param);

        param = new GLShaderParamVBO(NORMALS_PARAM_NAME, getProgramId());
        if (param.getParamReference() >= 0)
            params.put(param.getParamName(), param);
    }

}
