package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractSkyObject;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.Arrays;

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
        float [] mVMatrix = Arrays.copyOf(viewMatrix, viewMatrix.length);
        float [] mMVMatrix = new float[16];
        float[] mMVPMatrix = new float[16];

        sysUtilsWrapper.rotateM(mVMatrix, ((AbstractSkyObject)object).getRotationAngle(), 0.0f, 1.0f, 0.0f);

        /** remove camera translation -> skybox should stay on the fixed position */
        /*if (camera != null) {
            MathUtils.translateM(mVMatrix, 0, camera.getCameraPosition().x, camera.getCameraPosition().y, camera.getCameraPosition().z);
        }*/

        sysUtilsWrapper.mulMM(mMVMatrix, 0, mVMatrix, 0, object.getModelMatrix(), 0);
        sysUtilsWrapper.mulMM(mMVPMatrix, 0, projectionMatrix, 0, mMVMatrix, 0);
        setMVPMatrixData(mMVPMatrix);
    }
}
