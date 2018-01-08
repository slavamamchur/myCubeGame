package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_CUBEMAP_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.AMBIENT_RATE_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.DIFFUSE_RATE_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.IS_CUBEMAP_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.IS_NORMALMAP_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MV_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.RND_SEED__PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.SPECULAR_RATE_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXELS_PARAM_NAME;
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

        /** Model-View-Projection matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MVP_MATRIX_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Model-View- matrix*/
        param = new GLShaderParam(FLOAT_UNIFORM_MATRIX_PARAM, MV_MATRIX_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Light position*/
        param = new GLShaderParam(FLOAT_UNIFORM_VECTOR_PARAM, LIGHT_POSITION_PARAM_NAME, getProgramId());
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

        /** is normal map flag*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, IS_NORMALMAP_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Random generator seed */
        param = new GLShaderParam(FLOAT_UNIFORM_PARAM, RND_SEED__PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    public void setMaterialParams(GLSceneObject object) {
        paramByName(IS_CUBEMAP_PARAM_NAME).setParamValue(object.isCubeMap() ? 1 : 0);
        paramByName(IS_NORMALMAP_PARAM_NAME).setParamValue(object.isNormalMap() ? 1 : 0);

        if (object.isCubeMap()) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_CUBE_MAP, object.getGlTextureId());
            paramByName(ACTIVE_CUBEMAP_SLOT_PARAM_NAME).setParamValue(1);

            if (object.isNormalMap()) {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, object.getGlNormalMapId());
                setTextureSlotData(0);
            }
        }
        else {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, object.getGlTextureId());
            setTextureSlotData(0);
        }

        paramByName(AMBIENT_RATE_PARAM_NAME).setParamValue(object.getAmbientRate());
        paramByName(DIFFUSE_RATE_PARAM_NAME).setParamValue(object.getDiffuseRate());
        paramByName(SPECULAR_RATE_PARAM_NAME).setParamValue(object.getSpecularRate());
    }

}
