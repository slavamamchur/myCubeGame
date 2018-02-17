package com.sadgames.gl3dengine.glrender.scene.shaders;

import android.opengl.Matrix;

import com.sadgames.gl3dengine.SysUtilsWrapperInterface;
import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3f;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDetachShader;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_CUBEMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_DUDVMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_NORMALMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.AMBIENT_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DIFFUSE_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.IS_CUBEMAPF_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.IS_CUBEMAP_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_COLOUR_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_MVP_MATRIX_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_POSITIONF_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.LIGHT_POSITION_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MVP_MATRIX_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MV_MATRIXF_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.MV_MATRIX_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SPECULAR_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.UX_PIXEL_OFFSET_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.UY_PIXEL_OFFSET_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEXES_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;

public abstract class GLShaderProgram {

    private static final float BIAS[] = new float [] {0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f};

    private int programId;
    private int vertexShaderID;
    private int fragmentShaderID;
    protected Map<String, GLShaderParam> params = new HashMap<>();

    public GLShaderProgram(SysUtilsWrapperInterface sysUtilsWrapper) {

        vertexShaderID = createShader(sysUtilsWrapper, GL_VERTEX_SHADER, getVertexShaderResId());
        fragmentShaderID = createShader(sysUtilsWrapper, GL_FRAGMENT_SHADER, getFragmentShaderResId());
        programId = createProgram(vertexShaderID, fragmentShaderID);

        createParams();
    }

    protected abstract String getVertexShaderResId();
    protected abstract String getFragmentShaderResId();

    public int getProgramId() {
        return programId;
    }
    public Map<String, GLShaderParam> getParams() {
        return params;
    }

    public void useProgram() {
        //glUseProgram(programId);
        GLES20JniWrapper.glUseProgram(programId);
    }

    public void deleteProgram() {
        glDetachShader(programId, vertexShaderID);
        glDetachShader(programId, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programId);
    }

    public GLShaderParam paramByName (String name) {
        return params.get(name);
    }

    private void createParams() {
        params.clear();

        createAttributes();
        createUniforms();
    }

    protected void createUniforms() {
        GLShaderParam param;

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

    protected void createAttributes() {
        GLShaderParam param;

        /** Vertexes array*/
        param = new GLShaderParam(FLOAT_ATTRIB_ARRAY_PARAM, VERTEXES_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Texels array*/
        param = new GLShaderParam(FLOAT_ATTRIB_ARRAY_PARAM, TEXELS_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Normals array*/
        param = new GLShaderParam(FLOAT_ATTRIB_ARRAY_PARAM, NORMALS_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);
    }

    public void setVertexData(FloatBuffer data, int stride, int pos) {
        paramByName(VERTEXES_PARAM_NAME).setParamValue(VERTEX_SIZE, stride, pos, data);
    }

    public void linkVertexData(GLShaderParam param) throws IllegalAccessException {
        paramByName(VERTEXES_PARAM_NAME).linkParamValue(param);
    }

    public void setTexelData(FloatBuffer data, int stride, int pos) {
        paramByName(TEXELS_PARAM_NAME).setParamValue(TEXEL_UV_SIZE, stride, pos, data);
    }

    public void linkTexelData(GLShaderParam param) throws IllegalAccessException {
        paramByName(TEXELS_PARAM_NAME).linkParamValue(param);
    }

    public void setNormalData(FloatBuffer data, int stride, int pos) {
        paramByName(NORMALS_PARAM_NAME).setParamValue(VERTEX_SIZE, stride, pos, data);
    }

    public void linkNormalData(GLShaderParam param) throws IllegalAccessException {
        paramByName(NORMALS_PARAM_NAME).linkParamValue(param);
    }

    public void setTextureSlotData(int data) {
        GLShaderParam param = paramByName(ACTIVE_TEXTURE_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(data);
    }

    public void setCameraPosition(Vector3f pos) {
        paramByName(CAMERA_POSITION_PARAM_NAME).setParamValue(new float[] {pos.x, pos.y, pos.z});
    }

    public void setLightSourcePosition(float[] pos) {
        paramByName(LIGHT_POSITION_PARAM_NAME).setParamValue(pos);
        paramByName(LIGHT_POSITIONF_PARAM_NAME).setParamValue(pos);
    }

    public void setMVPMatrixData(float[] data) {
        paramByName(MVP_MATRIX_PARAM_NAME).setParamValue(data);
    }

    public void setMVMatrixData(float[] data) {
        paramByName(MV_MATRIX_PARAM_NAME).setParamValue(data);
        paramByName(MV_MATRIXF_PARAM_NAME).setParamValue(data);
    }

    public void bindMVPMatrix(AbstractGL3DObject object, float[] viewMatrix, float[] projectionMatrix) {
        float[] mMatrix = new float[16];

        Matrix.multiplyMM(mMatrix, 0, viewMatrix, 0, object.getModelMatrix(), 0);
        setMVMatrixData(mMatrix);

        Matrix.multiplyMM(mMatrix, 0, projectionMatrix, 0, mMatrix, 0);
        setMVPMatrixData(mMatrix);
    }

    public void linkVBOData(AbstractGL3DObject object) {
        try {
            linkVertexData(object.getVertexVBO());
            linkTexelData(object.getTexelVBO());
            linkNormalData(object.getNormalVBO());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setMaterialParams(AbstractGL3DObject object) {
        int textureSlotIndex = 0;

        if (object.getGlTextureId() > 0) {
            glActiveTexture(GL_TEXTURE0 + textureSlotIndex);
            glBindTexture(GL_TEXTURE_2D, object.getGlTextureId());
            setTextureSlotData(textureSlotIndex);

            textureSlotIndex++;
        }

        GLShaderParam param = paramByName(AMBIENT_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(object.getAmbientRate());
        param = paramByName(DIFFUSE_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(object.getDiffuseRate());
        param = paramByName(SPECULAR_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(object.getSpecularRate());

        int hasNormalMap = object.hasNormalMap() ? 1 : 0;
        param = paramByName(IS_CUBEMAP_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(hasNormalMap);
        param = paramByName(IS_CUBEMAPF_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(hasNormalMap);

        param = paramByName(ACTIVE_CUBEMAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && object.isCubeMap()) {
            glActiveTexture(GL_TEXTURE0 + textureSlotIndex);
            glBindTexture(/*GL_TEXTURE_CUBE_MAP*/GL_TEXTURE_2D, object.getGlCubeMapId());
            param.setParamValue(textureSlotIndex);
            textureSlotIndex++;
        }

        param = paramByName(ACTIVE_NORMALMAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && object.hasNormalMap()) {
            glActiveTexture(GL_TEXTURE0 + textureSlotIndex);
            glBindTexture(GL_TEXTURE_2D, object.getGlNormalMapId());
            param.setParamValue(textureSlotIndex);
            textureSlotIndex++;
        }

        param = paramByName(ACTIVE_DUDVMAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && object.hasDUDVMap()) {
            glActiveTexture(GL_TEXTURE0 + textureSlotIndex);
            glBindTexture(GL_TEXTURE_2D, object.getGlDUDVMapId());
            param.setParamValue(textureSlotIndex);
            textureSlotIndex++;
        }
    }

    public void setLightColourValue(Vector3f colour) {
        paramByName(LIGHT_COLOUR_PARAM_NAME).setParamValue(new float [] {colour.x, colour.y, colour.z});
    }

    public void bindLightSourceMVP (AbstractGL3DObject object, float[] viewMatrix, float[] projectionMatrix, boolean hasDepthTextureExtension) {
        float [] lightMVP = new float[16];

        Matrix.multiplyMM(lightMVP, 0, viewMatrix, 0, object.getModelMatrix(), 0);
        Matrix.multiplyMM(lightMVP, 0, projectionMatrix, 0, viewMatrix, 0);

        if (hasDepthTextureExtension)
            Matrix.multiplyMM(lightMVP, 0, BIAS, 0, lightMVP, 0);

        paramByName(LIGHT_MVP_MATRIX_PARAM_NAME).setParamValue(lightMVP);
    }

    /** AndroidSysUtils-------------------------------------------------------------------------------------*/
    public static int createProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = glCreateProgram();
        if (programId == 0) {
            return 0;
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            return 0;
        }
        return programId;
    }

    public static int createShader(SysUtilsWrapperInterface sysUtilsWrapper, int type, String shaderRawId) {
        return createShader(type, sysUtilsWrapper.iReadTextFromFile(shaderRawId));
    }

    static int createShader(int type, String shaderText) {
        final int shaderId = glCreateShader(type);
        if (shaderId == 0) {
            return 0;
        }
        glShaderSource(shaderId, shaderText);
        glCompileShader(shaderId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId);
            return 0;
        }
        return shaderId;
    }
}
