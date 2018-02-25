package com.sadgames.gl3dengine.glrender.scene.shaders;

import android.opengl.Matrix;

import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import javax.vecmath.Matrix4f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SKYBOX_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SKYBOX_VERTEX_SHADER;

public class SkyBoxProgram extends ShadowMapProgram {

    public SkyBoxProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return SKYBOX_VERTEX_SHADER;
    }

    @Override
    protected String getFragmentShaderResId() {
        return SKYBOX_FRAGMENT_SHADER;
    }

    @Override
    public void bindMVPMatrix(AbstractGL3DObject object, float[] viewMatrix, float[] projectionMatrix) {
        float[] mMatrix = new float[16];

        Matrix.setIdentityM(mMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, viewMatrix, 0, mMatrix, 0);
        //TODO: remove translation from viewMatrix
        Matrix4f view4f = new Matrix4f(mMatrix);

        //mMatrix[12] = 0;
        //mMatrix[13] = 0;
        //mMatrix[14] = 0;

        Matrix.multiplyMM(mMatrix, 0, mMatrix, 0, object.getModelMatrix(), 0);
        Matrix.multiplyMM(mMatrix, 0, projectionMatrix, 0, mMatrix, 0);
        setMVPMatrixData(mMatrix);
    }
}
