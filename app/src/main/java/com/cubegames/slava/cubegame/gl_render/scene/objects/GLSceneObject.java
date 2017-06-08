package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.Matrix;

import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;

import java.nio.ShortBuffer;

import static android.opengl.GLES20.glDeleteBuffers;
import static com.cubegames.slava.cubegame.Utils.loadGLTexture;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEXES_PARAM_NAME;

public abstract class GLSceneObject {

    protected Context context;
    private GLObjectType objectType;
    private int glTextureId = 0;
    private GLShaderParamVBO vertexVBO = null;
    private GLShaderParamVBO texelVBO = null;
    private GLShaderParamVBO normalVBO = null;
    private ShortBuffer indexData;
    protected int facesIBOPtr = 0;
    private float[] modelMatrix = new float[16];
    private GLShaderProgram program;

    public GLSceneObject(Context context, GLObjectType type, GLShaderProgram program) {
        this.context = context;
        objectType = type;
        this.program = program;
        Matrix.setIdentityM(modelMatrix, 0);

        createVBOParams();
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
    public ShortBuffer getIndexData() {
        return indexData;
    }
    public void setIndexData(ShortBuffer indexData) {
        this.indexData = indexData;
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

    public void loadObject() {
        createVertexesVBO();
        createTexelsVBO();
        createNormalsVBO();
        facesIBOPtr = createFacesIBO();

        glTextureId = loadTexture();
    }

    private int loadTexture() {
        Bitmap textureBmp = getTextureBitmap();

        return loadGLTexture(textureBmp);
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
