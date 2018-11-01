package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import static com.sadgames.sysutils.common.CommonUtils.readTextFromFile;

/**
 * Created by Slava Mamchur on 01.11.2018.
 */
public class MyShaderProgram extends ShaderProgram {

    private int mprogramId;

    public MyShaderProgram(String vertexShader, String fragmentShader) {
        super(readTextFromFile(vertexShader), readTextFromFile(fragmentShader));
    }

    @Override
    protected int createProgram() {
        mprogramId = super.createProgram();

        return mprogramId;
    }

    public int getProgramId() {
        return mprogramId;
    }

}
