package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TERRAIN_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TERRAIN_VERTEX_SHADER;

public class ShapeShaderProgram extends VBOShaderProgram {

    public ShapeShaderProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return  TERRAIN_VERTEX_SHADER;
    }
    @Override
    protected String getFragmentShaderResId() {
        return TERRAIN_FRAGMENT_SHADER;
    }

/*    @Override
    public void createParams() {
        super.createParams();

        GLShaderParam param;

        *//** pX*//*
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, PRIVOT_X_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        *//** pY*//*
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, PRIVOT_Y_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        *//** rAngle*//*
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, ROLL_ANGLE_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        *//** objRadius*//*
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, OBJECT_RADIUS_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        *//** rollStep*//*
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ROLL_STEP_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }*/
}
