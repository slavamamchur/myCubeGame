package com.sadgames.gl3d_engine.gl_render.scene.shaders;

import com.sadgames.gl3d_engine.gl_render.scene.objects.AbstractGL3DObject;
import com.sadgames.sysutils.SysUtilsWrapperInterface;

import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GUI_FRAGMENT_SHADER;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GUI_VERTEX_SHADER;

public class GUIRendererProgram extends ShadowMapProgram {

    public GUIRendererProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return GUI_VERTEX_SHADER;
    }
    @Override
    protected String getFragmentShaderResId() {
        return GUI_FRAGMENT_SHADER;
    }

    @Override
    public void linkVBOData(AbstractGL3DObject object) {
        try {
            linkVertexData(object.getVertexVBO());
            linkTexelData(object.getTexelVBO());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
