package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static android.opengl.GLES20.GL_EXTENSIONS;
import static android.opengl.GLES20.glGetString;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.OES_DEPTH_TEXTURE_EXTENSION;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOWMAP_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOWMAP_VERTEX_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEXES_PARAM_NAME;

public class ShadowMapProgram extends VBOShaderProgram {

    public ShadowMapProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return !checkDepthTextureExtension() ? SHADOWMAP_VERTEX_SHADER : SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT; //TODO: use rgb only ???
    }

    @Override
    protected String getFragmentShaderResId() {
        return !checkDepthTextureExtension() ? SHADOWMAP_FRAGMENT_SHADER : SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT;
    }

    private boolean checkDepthTextureExtension() {
        return glGetString(GL_EXTENSIONS).contains(OES_DEPTH_TEXTURE_EXTENSION);
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
    public void setMVMatrixData(float[] data) {}

    @Override
    public void setMVPMatrixData(float[] data) {
        GLShaderParam param = paramByName(MVP_MATRIX_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(data);
    }

}
