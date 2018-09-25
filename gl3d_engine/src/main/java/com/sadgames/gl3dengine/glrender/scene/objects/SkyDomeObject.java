package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TRIANGLES_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_UNSIGNED_SHORT_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDrawElements;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableBackFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnableFrontFacesCulling;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.SKY_DOME_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;

public class SkyDomeObject extends AbstractSkyObject {

    public SkyDomeObject(SysUtilsWrapperInterface sysUtilsWrapper, AbstractTexture cubeTexture, GLScene glScene) {
        super(sysUtilsWrapper, cubeTexture, glScene.getCachedShader(SKY_DOME_OBJECT));

        textureResName = cubeTexture != null && cubeTexture instanceof BitmapTexture ?
                ((BitmapTexture)cubeTexture).getTextureName() : null;
    }

    @Override
    protected GameItemObject createSkyPrimitive(SysUtilsWrapperInterface sysUtilsWrapper, float halfSize) {
        return new SpherePrimitiveObject(sysUtilsWrapper, null, getProgram(), 1f, COLLISION_OBJECT, halfSize);
    }

    @Override
    protected void createVertexesVBO() {
        float[] vertexes = getVertexesArray();

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertexes);
        vertexData.position(0);

        getVertexVBO().setParamValue(VERTEX_SIZE, 0, 0, vertexData);

        vertexData.limit(0);
    }

    @Override
    public void render() {
        glEnableFrontFacesCulling();
        glDrawElements(get_GL_TRIANGLES_value(), getFacesCount(), get_GL_UNSIGNED_SHORT_value());
        glEnableBackFacesCulling();
    }
}
