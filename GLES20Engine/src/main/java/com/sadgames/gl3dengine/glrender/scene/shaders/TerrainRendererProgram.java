package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.GLRenderConsts;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.common.MathUtils;

import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_BACKGROUND_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.BACKGROUND_TEXTURE_SLOT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.FBO_TEXTURE_SLOT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.IS_2D_MODE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_COLOUR_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_POSITIONF_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MAIN_RENDERER_FRAGMENT_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MAIN_RENDERER_VERTEX_SHADER;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.RND_SEED__PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SKY_BOX_MV_MATRIXF_PARAM_NAME;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

public class TerrainRendererProgram extends VBOShaderProgram {
    private float skyBoxRotationAngle = 0;

    public TerrainRendererProgram() {
        super();
    }

    @SuppressWarnings("unused")
    public void setSkyBoxRotationAngle(float skyBoxRotationAngle) {
        this.skyBoxRotationAngle = skyBoxRotationAngle;
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
    protected void createUniforms() {
        super.createUniforms();

        /** Random generator seed */
        GLShaderParam param = new GLShaderParam(FLOAT_UNIFORM_PARAM, RND_SEED__PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Inverted Sky-box rotation */
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, SKY_BOX_MV_MATRIXF_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Background texture slot */
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_BACKGROUND_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** 2D-Mode flag */
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, IS_2D_MODE_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    @Override
    public void bindGlobalParams(GLScene scene) {
        GLLightSource lightSource = scene.getLightSource();
        GLRenderConsts.GraphicsQuality graphicsQualityLevel =
                getSettingsManager().getGraphicsQualityLevel();

        AbstractTexture background = scene.getBackgroundTextureName() == null ? null :
                TextureCacheManager.getInstance().getItem(scene.getBackgroundTextureName());
        if (background != null) {
            background.bind(BACKGROUND_TEXTURE_SLOT);
            paramByName(ACTIVE_BACKGROUND_SLOT_PARAM_NAME).setParamValue(BACKGROUND_TEXTURE_SLOT);
        }

        paramByName(IS_2D_MODE_PARAM_NAME).setParamValue(getSettingsManager().isIn_2D_Mode() ? 1 : 0);

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
                                                        GLRenderConsts.GraphicsQuality.LOW.equals(graphicsQualityLevel)
                                                        || getSettingsManager().isIn_2D_Mode()
                                                        ?
                                                        -1f
                                                        :
                                                        scene.getMoveFactor());

        float[] mMatrix = new float[16];
        MathUtils.rotateM(mMatrix, 0f, skyBoxRotationAngle, 0f);
        GLShaderParam param = paramByName(SKY_BOX_MV_MATRIXF_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(mMatrix);


    }

    @Override
    public void bindAdditionalParams(GLScene scene, AbstractGL3DObject object) {
        bindLightSourceMVP(object, scene.getLightSource().getViewMatrix(), scene.getLightSource().getProjectionMatrix(), scene.hasDepthTextureExtension());
    }
}
