package com.sadgames.gl3dengine.glrender.scene.shaders;

import android.opengl.Matrix;

import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.SkyBoxObject;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SKYBOX_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SKYBOX_VERTEX_SHADER;

public class SkyBoxProgram extends ShadowMapProgram {

    private GLCamera camera = null;

    public SkyBoxProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    public void setCamera(GLCamera camera) {
        this.camera = camera;
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
        Matrix.rotateM(mMatrix, 0, ((SkyBoxObject)object).getRotationAngle(), 0, 1, 0);

        /** remove camera translation -> skybox should stay on the fixed position */
        /*if (camera != null) {
            MathUtils.translateM(mMatrix, 0, camera.getCameraPosition().x, camera.getCameraPosition().y, camera.getCameraPosition().z);
        }*/

        Matrix.multiplyMM(mMatrix, 0, mMatrix, 0, object.getModelMatrix(), 0);
        Matrix.multiplyMM(mMatrix, 0, projectionMatrix, 0, mMatrix, 0);
        setMVPMatrixData(mMatrix);
    }
}
