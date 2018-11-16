package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOWMAP_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOWMAP_VERTEX_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.UNI_DEPTH_TEXTURE_EXTENSION;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEXES_PARAM_NAME;

public class ShadowMapProgram extends VBOShaderProgram {

    public ShadowMapProgram() {
        super();
    }

    @Override
    protected String getVertexShaderResId() {
        return !checkDepthTextureExtension() ? SHADOWMAP_VERTEX_SHADER : SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT;
    }

    @Override
    protected String getFragmentShaderResId() {
        return !checkDepthTextureExtension() ? SHADOWMAP_FRAGMENT_SHADER : SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT;
    }

    private boolean checkDepthTextureExtension() {
        return GLES20JniWrapper.glExtensions().contains(UNI_DEPTH_TEXTURE_EXTENSION);
    }

    @Override
    public void linkVertexData(GLShaderParam param) throws IllegalAccessException {
        paramByName(VERTEXES_PARAM_NAME).linkParamValue(param);
    }

    @Override
    public void linkVBOData(AbstractGL3DObject object) {
        try {
            linkVertexData(object.getVertexVBO());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bindGlobalParams(GLScene scene) {}

    @Override
    public void bindAdditionalParams(GLScene scene, AbstractGL3DObject object) {}

    @Override
    public void setMVMatrixData(float[] data) {}

}
