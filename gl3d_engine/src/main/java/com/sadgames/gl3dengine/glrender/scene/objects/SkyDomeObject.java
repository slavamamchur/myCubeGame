package com.sadgames.gl3dengine.glrender.scene.objects;


import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glDrawElements;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableBackFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableFrontFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.SKY_BOX_OBJECT;

public class SkyDomeObject extends AbstractSkyObject {

    public SkyDomeObject(SysUtilsWrapperInterface sysUtilsWrapper, AbstractTexture cubeTexture, GLScene glScene) {
        super(sysUtilsWrapper, cubeTexture, glScene.getCachedShader(SKY_BOX_OBJECT));//TODO: new shader
    }

    @Override
    protected GameItemObject createSkyPrimitive(SysUtilsWrapperInterface sysUtilsWrapper, float halfSize) {
        return new SpherePrimitiveObject(sysUtilsWrapper, null, getProgram(), 1f, COLLISION_OBJECT, halfSize);
    }

    @Override
    public void render() {
        glEnableFrontFacesCulling();
        glDrawElements(GL_TRIANGLES, getFacesCount(), GL_UNSIGNED_INT, 0);
        glEnableBackFacesCulling();
    }
}
