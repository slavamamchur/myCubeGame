package com.sadgames.dicegame.gl3d_engine.gl_render.scene.shaders;

import com.sadgames.dicegame.gl3d_engine.gl_render.scene.objects.GLSceneObject;
import com.sadgames.dicegame.gl3d_engine.gl_render.scene.shaders.params.GLShaderParam;
import com.sadgames.dicegame.gl3d_engine.utils.ISysUtilsWrapper;

import static android.opengl.GLES20.GL_EXTENSIONS;
import static android.opengl.GLES20.glGetString;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.OES_DEPTH_TEXTURE_EXTENSION;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.SHADOWMAP_FRAGMENT_SHADER;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.SHADOWMAP_VERTEX_SHADER;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.VERTEXES_PARAM_NAME;

public class ShadowMapProgram extends VBOShaderProgram {

    public ShadowMapProgram(ISysUtilsWrapper sysUtilsWrapper) {
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
    public void linkVBOData(GLSceneObject object) {
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
