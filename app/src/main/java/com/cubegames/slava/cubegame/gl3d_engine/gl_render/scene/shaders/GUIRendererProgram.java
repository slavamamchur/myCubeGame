package com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.shaders;

import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl3d_engine.utils.ISysUtilsWrapper;

import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.GUI_FRAGMENT_SHADER;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.GUI_VERTEX_SHADER;

public class GUIRendererProgram extends ShadowMapProgram {

    public GUIRendererProgram(ISysUtilsWrapper sysUtilsWrapper) {
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
    public void linkVBOData(GLSceneObject object) {
        try {
            linkVertexData(object.getVertexVBO());
            linkTexelData(object.getTexelVBO());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
