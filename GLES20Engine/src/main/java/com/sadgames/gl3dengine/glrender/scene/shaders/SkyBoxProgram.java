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
        float[] mMatrix = Arrays.copyOf(viewMatrix, viewMatrix.length);

        //todo: rotate sky aground Z ...
        sysUtilsWrapper.rotateM(mMatrix, 0, ((AbstractSkyObject)object).getRotationAngle(), 0, 1, 0);

        /** remove camera translation -> skybox should stay on the fixed position */
        /*if (camera != null) {
            MathUtils.translateM(mMatrix, 0, camera.getCameraPosition().x, camera.getCameraPosition().y, camera.getCameraPosition().z);
        }*/

        sysUtilsWrapper.mulMM(mMatrix, 0, mMatrix, 0, object.getModelMatrix(), 0);
        sysUtilsWrapper.mulMM(mMatrix, 0, projectionMatrix, 0, mMatrix, 0);
        setMVPMatrixData(mMatrix);
    }
}
