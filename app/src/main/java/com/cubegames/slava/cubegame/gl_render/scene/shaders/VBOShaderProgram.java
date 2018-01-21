package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;
import android.opengl.Matrix;

import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;

import javax.vecmath.Vector3f;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE2;
import static android.opengl.GLES20.GL_TEXTURE3;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_CUBEMAP_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_DUDVMAP_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_NORMALMAP_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.AMBIENT_RATE_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.DIFFUSE_RATE_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.IS_CUBEMAPF_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.IS_CUBEMAP_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_COLOUR_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_POSITIONF_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MV_MATRIXF_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MV_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.SPECULAR_RATE_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.UX_PIXEL_OFFSET_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.UY_PIXEL_OFFSET_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEXES_PARAM_NAME;

public abstract class VBOShaderProgram extends GLShaderProgram {


    public VBOShaderProgram(Context context) {
        super(context);
    }

    @Override
    public void createParams() {
        GLShaderParam param;

        /** Vertexes array*/
        param = new GLShaderParamVBO(VERTEXES_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Texels array*/
        param = new GLShaderParamVBO(TEXELS_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Normals array*/
        param = new GLShaderParamVBO(NORMALS_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active texture slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_TEXTURE_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active cubemap slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_CUBEMAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active normalmap slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_NORMALMAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active dudvmap slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_DUDVMAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active dudvmap slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_SHADOWMAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Model-View-Projection matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MVP_MATRIX_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Camer POV Model-View- matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MV_MATRIX_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Camer POV Model-View- matrix for fragmrnt shader*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MV_MATRIXF_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Light POV Model-View-Projection matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, LIGHT_MVP_MATRIX_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Light position*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, LIGHT_POSITION_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Light position for fragment shader*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, LIGHT_POSITIONF_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Light colour*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, LIGHT_COLOUR_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Camera position*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, CAMERA_POSITION_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Ambient material rate*/
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, AMBIENT_RATE_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Diffuse material rate*/
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, DIFFUSE_RATE_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Specular material rate*/
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, SPECULAR_RATE_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** is cube map flag*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, IS_CUBEMAP_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** is cube map flag for fragment shader*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, IS_CUBEMAPF_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** This define the x value to move one pixel left or right */
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, UX_PIXEL_OFFSET_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** This define the y value to move one pixel left or right */
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, UY_PIXEL_OFFSET_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    public void setMaterialParams(GLSceneObject object) {
        paramByName(IS_CUBEMAP_PARAM_NAME).setParamValue(object.hasNormalMap() ? 1 : 0);
        paramByName(IS_CUBEMAPF_PARAM_NAME).setParamValue(object.hasNormalMap() ? 1 : 0);

        if (object.isCubeMap()) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(/*GL_TEXTURE_CUBE_MAP*/GL_TEXTURE_2D, object.getGlCubeMapId());
            paramByName(ACTIVE_CUBEMAP_SLOT_PARAM_NAME).setParamValue(1);

            if (object.hasNormalMap()) {
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, object.getGlNormalMapId());
                paramByName(ACTIVE_NORMALMAP_SLOT_PARAM_NAME).setParamValue(2);

                glActiveTexture(GL_TEXTURE3);
                glBindTexture(GL_TEXTURE_2D, object.getGlDUDVMapId());
                paramByName(ACTIVE_DUDVMAP_SLOT_PARAM_NAME).setParamValue(3);
            }
        }

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, object.getGlTextureId());
        setTextureSlotData(0);

        paramByName(AMBIENT_RATE_PARAM_NAME).setParamValue(object.getAmbientRate());
        paramByName(DIFFUSE_RATE_PARAM_NAME).setParamValue(object.getDiffuseRate());
        paramByName(SPECULAR_RATE_PARAM_NAME).setParamValue(object.getSpecularRate());
    }

    public void setLightColourValue(Vector3f colour) {
        paramByName(LIGHT_COLOUR_PARAM_NAME).setParamValue(new float [] {colour.x, colour.y, colour.z});
    }

    public void bindLightSourceMVP (GLSceneObject object, float[] viewMatrix, float[] projectionMatrix, boolean hasDepthTextureExtension) {
        float [] lightMVP = new float[16];
        float[] tempResultMatrix = new float[16];

        Matrix.multiplyMM(lightMVP, 0, viewMatrix, 0, object.getModelMatrix(), 0);
        Matrix.multiplyMM(tempResultMatrix, 0, projectionMatrix, 0, lightMVP, 0);
        System.arraycopy(tempResultMatrix, 0, lightMVP, 0, 16);

        float bias[] = new float [] {0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f};
        float[] depthBiasMVP = new float[16];

        if (hasDepthTextureExtension){
            Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, lightMVP, 0);
            System.arraycopy(depthBiasMVP, 0, lightMVP, 0, 16);
        }

        paramByName(LIGHT_MVP_MATRIX_PARAM_NAME).setParamValue(lightMVP);
    }

}
