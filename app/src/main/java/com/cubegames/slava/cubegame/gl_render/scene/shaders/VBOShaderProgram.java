package com.cubegames.slava.cubegame.gl_render.scene.shaders;

import android.content.Context;

import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParam;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindTexture;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.AMBIENT_RATE_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.DIFFUSE_RATE_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.MV_MATRIX_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.NORMALS_PARAM_NAME;
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
    }

    //TODO: override protected set material params -> from object
    public void setMaterialParams(GLSceneObject object) {
        glBindTexture(GL_TEXTURE_2D, object.getGlTextureId());
        setTextureSlotData(0);

        paramByName(AMBIENT_RATE_PARAM_NAME).setParamValue(object.getAmbientRate());
        paramByName(DIFFUSE_RATE_PARAM_NAME).setParamValue(object.getDiffuseRate());
        paramByName(SPECULAR_RATE_PARAM_NAME).setParamValue(object.getSpecularRate());
    }

}
