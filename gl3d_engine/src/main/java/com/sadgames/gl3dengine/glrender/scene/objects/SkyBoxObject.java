package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.CubeMapTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;

public class SkyBoxObject extends CubePrimitiveObject {

    public SkyBoxObject(SysUtilsWrapperInterface sysUtilsWrapper, CubeMapTexture cubeTexture, GLShaderProgram program) {
        super(sysUtilsWrapper, null, program, 1f, COLLISION_OBJECT, LAND_SIZE_IN_WORLD_SPACE / 2.0f);
        setGlTexture(cubeTexture);
        setCastShadow(false);
    }

    @Override
    public void render() {
        GLES20JniWrapper.glEnableFrontFacesCulling();
        super.render();
        GLES20JniWrapper.glEnableBackFacesCulling();
    }
}
