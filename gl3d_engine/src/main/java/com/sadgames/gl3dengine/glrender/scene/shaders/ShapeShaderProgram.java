package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.GLRenderConsts;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.FBO_TEXTURE_SLOT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_COLOUR_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_POSITIONF_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MAIN_RENDERER_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MAIN_RENDERER_VERTEX_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.RND_SEED__PARAM_NAME;

public class ShapeShaderProgram extends VBOShaderProgram {

    public ShapeShaderProgram(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(sysUtilsWrapper);
    }

    @Override
    protected String getVertexShaderResId() {
        return MAIN_RENDERER_VERTEX_SHADER;
    }
    @Override
    protected String getFragmentShaderResId() {
        return MAIN_RENDERER_FRAGMENT_SHADER;
    }

    @Override
    public void bindAdditionalParams(GLScene scene, AbstractGL3DObject object) {
        bindLightSourceMVP(object, scene.getLightSource().getViewMatrix(), scene.getLightSource().getProjectionMatrix(), scene.hasDepthTextureExtension());
    }

    @Override
    public void bindGlobalParams(GLScene scene) {
        GLLightSource lightSource = scene.getLightSource();
        GLRenderConsts.GraphicsQuality graphicsQualityLevel =
                sysUtilsWrapper.iGetSettingsManager().getGraphicsQualityLevel();

        scene.getShadowMapFBO().getFboTexture().bind(FBO_TEXTURE_SLOT);
        paramByName(ACTIVE_SHADOWMAP_SLOT_PARAM_NAME).setParamValue(FBO_TEXTURE_SLOT);

        synchronized (GLScene.lockObject) {
            Vector3f pos = scene.getCamera().getCameraPosition();
            paramByName(CAMERA_POSITION_PARAM_NAME).setParamValue(new float[] {pos.x, pos.y, pos.z});
        }

        paramByName(LIGHT_POSITION_PARAM_NAME).setParamValue(lightSource.getLightPosInEyeSpace());
        paramByName(LIGHT_POSITIONF_PARAM_NAME).setParamValue(lightSource.getLightPosInEyeSpace());

        Vector3f colour = lightSource.getLightColour();
        paramByName(LIGHT_COLOUR_PARAM_NAME).setParamValue(new float [] {colour.x, colour.y, colour.z});

        paramByName(RND_SEED__PARAM_NAME).setParamValue(
                GLRenderConsts.GraphicsQuality.LOW.equals(graphicsQualityLevel) ?
                        -1f
                        :
                        scene.getMoveFactor());

        /** for rgb depth buffers */
        /*paramByName(UX_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / scene.getShadowMapFBO().getWidth()));
        paramByName(UY_PIXEL_OFFSET_PARAM_NAME).setParamValue((float) (1.0 / scene.getShadowMapFBO().getHeight()));*/
    }
}
