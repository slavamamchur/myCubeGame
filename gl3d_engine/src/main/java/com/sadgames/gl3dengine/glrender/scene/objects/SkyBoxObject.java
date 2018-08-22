package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.SKY_BOX_OBJECT;

public class SkyBoxObject extends AbstractSkyObject {


    public SkyBoxObject(SysUtilsWrapperInterface sysUtilsWrapper, AbstractTexture cubeTexture, GLScene glScene) {
        super(sysUtilsWrapper, cubeTexture, glScene.getCachedShader(SKY_BOX_OBJECT));
    }

    @Override
    protected GameItemObject createSkyPrimitive(SysUtilsWrapperInterface sysUtilsWrapper, float halfSize) {
        return new CubePrimitiveObject(sysUtilsWrapper, null, getProgram(), 1f, COLLISION_OBJECT, halfSize);
    }
}
