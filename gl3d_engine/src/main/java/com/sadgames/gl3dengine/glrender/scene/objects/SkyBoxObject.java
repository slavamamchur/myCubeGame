package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.CubeMapTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableBackFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableFrontFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;

public class SkyBoxObject extends CubePrimitiveObject {

    private float rotationAngle = 0;

    public SkyBoxObject(SysUtilsWrapperInterface sysUtilsWrapper, CubeMapTexture cubeTexture, GLShaderProgram program) {
        super(sysUtilsWrapper, null, program, 1f, COLLISION_OBJECT, LAND_SIZE_IN_WORLD_SPACE / 2f);
        setGlTexture(cubeTexture);
        setCastShadow(false);
    }

    public float getRotationAngle() {
        return rotationAngle;
    }
    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    @Override
    public void render() {
        glEnableFrontFacesCulling();
        super.render();
        glEnableBackFacesCulling();
    }
}
