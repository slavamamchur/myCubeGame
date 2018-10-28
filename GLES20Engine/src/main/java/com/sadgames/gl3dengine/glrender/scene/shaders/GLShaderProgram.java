package com.sadgames.gl3dengine.glrender.scene.shaders;

import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.GL20.GL_COMPILE_STATUS;
import static com.badlogic.gdx.graphics.GL20.GL_FRAGMENT_SHADER;
import static com.badlogic.gdx.graphics.GL20.GL_LINK_STATUS;
import static com.badlogic.gdx.graphics.GL20.GL_VERTEX_SHADER;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glAttachShader;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glCompileShader;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glCreateProgram;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glCreateShader;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDeleteProgram;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDeleteShader;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDetachShader;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glGetProgramiv;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glGetShaderiv;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glLinkProgram;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glShaderSource;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_BLENDING_MAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_DUDVMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_NORMALMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_REFRACTION_MAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_SHADOWMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_SKYBOX_MAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.AMBIENT_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.CAMERA_POSITION_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DIFFUSE_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_MATRIX_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_UNIFORM_VECTOR_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.INTEGER_UNIFORM_PARAM;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.HAS_REFLECT_MAP_PARAM_NAME;
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
import static com.sadgames.sysutils.common.CommonUtils.readTextFromFile;
import static com.sadgames.sysutils.common.MathUtils.mulMat;

public abstract class GLShaderProgram {

    private static final float BIAS[] = new float [] {0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f};

    private int programId;
    private int vertexShaderID;
    private int fragmentShaderID;
    protected Map<String, GLShaderParam> params = new HashMap<>();


    public GLShaderProgram() {
        vertexShaderID = createShaderFromResource(GL_VERTEX_SHADER, getVertexShaderResId());
        fragmentShaderID = createShaderFromResource(GL_FRAGMENT_SHADER, getFragmentShaderResId());
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

        /** Active refraction map slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_REFRACTION_MAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active reflection map slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_SKYBOX_MAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** has reflection map flag*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, HAS_REFLECT_MAP_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active normal map slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_NORMALMAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active dudv map slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_DUDVMAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active blending map slot*/
        param = new GLShaderParam(INTEGER_UNIFORM_PARAM, ACTIVE_BLENDING_MAP_SLOT_PARAM_NAME, getProgramId());
        params.put(param.getParamName(), param);

        /** Active shadow map slot*/
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

    @SuppressWarnings("all")
    public void linkTexelData(GLShaderParam param) throws IllegalAccessException {
        paramByName(TEXELS_PARAM_NAME).linkParamValue(param);
    }

    public void setNormalData(FloatBuffer data, int stride, int pos) {
        paramByName(NORMALS_PARAM_NAME).setParamValue(VERTEX_SIZE, stride, pos, data);
    }

    @SuppressWarnings("all")
    public void linkNormalData(GLShaderParam param) throws IllegalAccessException {
        paramByName(NORMALS_PARAM_NAME).linkParamValue(param);
    }

    public void setTextureSlotData(int data) {
        GLShaderParam param = paramByName(ACTIVE_TEXTURE_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(data);
    }

    @SuppressWarnings("all")
    public void setMVPMatrixData(float[] data) {
        GLShaderParam param = paramByName(MVP_MATRIX_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(data);
    }

    public void setMVMatrixData(float[] data) {
        GLShaderParam param = paramByName(MV_MATRIX_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(data);

        param = paramByName(MV_MATRIXF_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(data);
    }

    public void bindMVPMatrix(AbstractGL3DObject object, float[] viewMatrix, float[] projectionMatrix) {
        float [] mMVMatrix = new float[16];
        float[] mMVPMatrix = new float[16];

        mulMat(mMVMatrix, viewMatrix, object.getModelMatrix());
        setMVMatrixData(mMVMatrix);

        mulMat(mMVPMatrix, projectionMatrix, mMVMatrix);
        setMVPMatrixData(mMVPMatrix);
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

    public abstract void bindGlobalParams(GLScene scene);
    public abstract void bindAdditionalParams(GLScene scene, AbstractGL3DObject object);

    @SuppressWarnings("all")
    protected void bindLightSourceMVP (AbstractGL3DObject object, float[] viewMatrix, float[] projectionMatrix, boolean hasDepthTextureExtension) {
        float [] tmpResult1 = new float[16];
        float [] tmpResult2 = new float[16];
        float [] lightMVP = new float[16];

        mulMat(tmpResult1, viewMatrix, object.getModelMatrix());
        mulMat(tmpResult2, projectionMatrix, tmpResult1);
        mulMat(lightMVP, BIAS, tmpResult2);

        paramByName(LIGHT_MVP_MATRIX_PARAM_NAME).setParamValue(lightMVP);
    }

    @SuppressWarnings("all")
    protected int createProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = glCreateProgram();
        if (programId == 0) {
            return 0;
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            return 0;
        }
        return programId;
    }

    @SuppressWarnings("all")
    protected int createShaderFromResource(int type, String shaderRawId) {
        return createShader(type, readTextFromFile(shaderRawId));
    }

    @SuppressWarnings("all")
    protected int createShader(int type, String shaderText) {
        final int shaderId = glCreateShader(type);
        if (shaderId == 0) {
            return 0;
        }
        glShaderSource(shaderId, shaderText);
        glCompileShader(shaderId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus);
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId);
            return 0;
        }
        return shaderId;
    }
}
