package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MAIN_RENDERER_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MAIN_RENDERER_VERTEX_SHADER;

//import static com.sadgames.gameengine.gl_render.GLRenderConsts.RND_SEED__PARAM_NAME;

public class WaterShaderProgram extends VBOShaderProgram {

    public WaterShaderProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return MAIN_RENDERER_VERTEX_SHADER;
    }

    @Override
    protected String getFragmentShaderResId() {
        return MAIN_RENDERER_FRAGMENT_SHADER;
    }

//    @Override
//    public void createParams() {
//        super.createParams();
//
//        /*GLShaderParam param;
//        *//** Random generator seed *//*
//        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, RND_SEED__PARAM_NAME, getProgramId());
//        params.put(param.getParamName(), param);*/
//    }

//    public void setRndSeedData(float data) { //1.93984269E9 //1.34289536E9
//        paramByName(RND_SEED__PARAM_NAME).setParamValue(data);
//    }
}
