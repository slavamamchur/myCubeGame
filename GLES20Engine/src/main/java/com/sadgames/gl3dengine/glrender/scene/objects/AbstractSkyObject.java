package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TRIANGLE_STRIP_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDrawArrays;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableBackFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableFrontFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

public abstract class AbstractSkyObject extends GameItemObject {

    private float rotationAngle = 0;
    protected GameItemObject skyPrimitive;

    public AbstractSkyObject(AbstractTexture cubeTexture, GLShaderProgram program) {
        super(null, program, 1f, COLLISION_OBJECT);

        skyPrimitive = createSkyPrimitive(LAND_SIZE_IN_WORLD_SPACE / 2f /*+ 0.25f*/);

        setGlTexture(cubeTexture);
        setCastShadow(false);
    }

    public float getRotationAngle() {
        return rotationAngle;
    }
    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    @SuppressWarnings("unused")
    public void calcRotationAngle(long frametime) {
        float angle = getSettingsManager().isIn_2D_Mode() ? 0 : getRotationAngle() + 0.5f * frametime / 250f;
        setRotationAngle(angle > 360f ? 360f - angle : angle);
    }

    protected abstract GameItemObject createSkyPrimitive(float halfSize);

    @Override
    public void render() {
        glEnableFrontFacesCulling();
        glDrawArrays(get_GL_TRIANGLE_STRIP_value(), 0, getFacesCount());
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
