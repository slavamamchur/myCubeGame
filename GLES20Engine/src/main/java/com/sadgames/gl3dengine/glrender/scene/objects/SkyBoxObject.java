package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.SKY_BOX_OBJECT;

public class SkyBoxObject extends AbstractSkyObject {

    public SkyBoxObject(AbstractTexture cubeTexture, GLScene glScene) {
        super(cubeTexture, glScene.getCachedShader(SKY_BOX_OBJECT));
    }

    @Override
    protected GameItemObject createSkyPrimitive(float halfSize) {
        return new CubePrimitiveObject(null, getProgram(), 1f, COLLISION_OBJECT, halfSize);
    }
}
