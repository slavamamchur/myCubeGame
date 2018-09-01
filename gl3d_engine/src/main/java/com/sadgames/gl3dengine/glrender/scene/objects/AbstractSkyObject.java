package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableBackFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableFrontFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;

public abstract class AbstractSkyObject extends GameItemObject {

    private float rotationAngle = 0;
    protected GameItemObject skyPrimitive;

    public AbstractSkyObject(SysUtilsWrapperInterface sysUtilsWrapper, AbstractTexture cubeTexture, GLShaderProgram program) {
        super(sysUtilsWrapper, null, program, 1f, COLLISION_OBJECT);

        skyPrimitive = createSkyPrimitive(sysUtilsWrapper, LAND_SIZE_IN_WORLD_SPACE / 2f + 0.25f);

        setGlTexture(cubeTexture);
        setCastShadow(false);
    }

    public float getRotationAngle() {
        return rotationAngle;
    }
    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public void calcRotationAngle(long frametime) {
        float angle = sysUtilsWrapper.iGetSettingsManager().isIn_2D_Mode() ? 0 : getRotationAngle() + 0.5f * frametime / 250f;
        setRotationAngle(angle > 360f ? 360f - angle : angle);
    }

    protected abstract GameItemObject createSkyPrimitive(SysUtilsWrapperInterface sysUtilsWrapper, float halfSize);

    @Override
    public void render() {
        glEnableFrontFacesCulling();
        glDrawArrays(GL_TRIANGLE_STRIP, 0, getFacesCount());
        glEnableBackFacesCulling();
    }

    @Override
    public int getFacesCount() {
        return skyPrimitive.getFacesCount();
    }

    @Override
    protected float[] getVertexesArray() {
        return skyPrimitive.getVertexesArray();
    }

    @Override
    protected float[] getNormalsArray() {
        return skyPrimitive.getNormalsArray();
    }

    @Override
    protected short[] getFacesArray() {
        return skyPrimitive.getFacesArray();
    }
}
