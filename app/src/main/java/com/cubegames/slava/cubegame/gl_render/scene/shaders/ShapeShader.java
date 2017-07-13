package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.R;

public class ShapeShader extends VBOShaderProgram {

    public ShapeShader(Context context) {
        super(context);
    }

    @Override
    protected int getVertexShaderResId() {
        return  R.raw.vertex_shader_other;
    }
    @Override
    protected int getFragmentShaderResId() {
        return R.raw.fragment_shader_other;
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
