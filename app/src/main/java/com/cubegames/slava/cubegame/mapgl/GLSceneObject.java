package com.cubegames.slava.cubegame.mapgl;

import android.graphics.Bitmap;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDeleteBuffers;
import static com.cubegames.slava.cubegame.Utils.loadGLTexture;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLObjectType;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.VERTEXES_PARAM_NAME;

public abstract class GLSceneObject {

    private GLObjectType objectType;
    private int glTextureId = 0;
    private GLShaderParamVBO vertexVBO = null;
    private GLShaderParamVBO texelVBO = null;
    private GLShaderParamVBO normalVBO = null;
    protected int facesIBOPtr = 0;
    private float[] modelMatrix = null;
    private GLShaderProgram program;

    public GLSceneObject(GLObjectType type, GLShaderProgram program) {
        objectType = type;
        this.program = program;

        createVBOParams();
        loadObject();
    }

    public GLObjectType getObjectType() {
        return objectType;
    }
    public int getGlTextureId() {
        return glTextureId;
    }
    public GLShaderParamVBO getVertexVBO() {
        return vertexVBO;
    }
    public GLShaderParamVBO getTexelVBO() {
        return texelVBO;
    }
    public GLShaderParamVBO getNormalVBO() {
        return normalVBO;
    }
    public int getFacesIBOPtr() {
        return facesIBOPtr;
    }
    public float[] getModelMatrix() {
        return modelMatrix;
    }
    public GLShaderProgram getProgram() {
        return program;
    }

    private void createVBOParams() {
        vertexVBO = new GLShaderParamVBO(VERTEXES_PARAM_NAME, program.getProgramId());
        texelVBO = new GLShaderParamVBO(TEXELS_PARAM_NAME, program.getProgramId());
        normalVBO = new GLShaderParamVBO(NORMALS_PARAM_NAME, program.getProgramId());
    }

    public void setVBOParamData(GLShaderParamVBO param, int size, int stride, int pos, FloatBuffer data) {
        param.setParamValue(size, stride, pos, data);
    }

    private void loadObject() {
        glTextureId = loadTexture();
        createVertexesVBO();
        createTexelsVBO();
        createNormalsVBO();
        facesIBOPtr = createFacesIBO();
    }

    private int loadTexture() {
        Bitmap textureBitmap = getTextureBitmap();
        int result = 0;

        if (textureBitmap != null && !textureBitmap.isRecycled()) {
            result = loadGLTexture(textureBitmap);
        }

        return result;
    }

    private void clearVBOPtr(int vboPtr) {
        if (vboPtr != 0) {
            glDeleteBuffers(1, new int[]{vboPtr}, 0);
            vboPtr = 0;
        }
    }

    private void clearVBOPtr(GLShaderParamVBO param) {
        if (param != null) {
            clearVBOPtr(param.getVboPtr());
            param = null;
        }
    }

    public void clearVBOData() {
        clearVBOPtr(vertexVBO);
        clearVBOPtr(texelVBO);
        clearVBOPtr(normalVBO);
        clearVBOPtr(facesIBOPtr);
        facesIBOPtr = 0;
    }

    protected abstract Bitmap getTextureBitmap();
    public abstract int getFacesCount();
    protected abstract void createVertexesVBO();
    protected abstract void createTexelsVBO();
    protected abstract void createNormalsVBO();
    protected abstract int createFacesIBO();
}
