package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cubegames.slava.cubegame.gl_render.GLAnimation;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.params.GLShaderParamVBO;

import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glDrawElements;
import static com.cubegames.slava.cubegame.Utils.loadGLTexture;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEXES_PARAM_NAME;

public abstract class GLSceneObject {

    //private final static long LAND_ANIMATION_DELAY_MS = 10000L;

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
    private GLAnimation animation = null;
    private PointF position = new PointF(0, 0);

    private float ambientRate = 0.4f;
    private float diffuseRate = 1.0f;
    private float specularRate = 0.9f;
    private boolean isCubeMap = false;
    private int glNormalMapId = 0;
    private int glCubeMapId = 0;
    private int glDUDVMapId = 0;

    protected int textureResId = -1;
    protected String mapID = "";
    protected int textureColor = 0;

    public GLSceneObject(Context context, GLObjectType type, GLShaderProgram program) { //TODO: material object
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
    public void setGlTextureId(int glTextureId) {
        this.glTextureId = glTextureId;
    }

    public boolean hasNormalMap() {
        return glNormalMapId > 0;
    }

    public int getGlNormalMapId() {
        return glNormalMapId;
    }
    public void setGlNormalMapId(int glNormalMapId) {
        this.glNormalMapId = glNormalMapId;
    }

    public int getGlCubeMapId() {
        return glCubeMapId;
    }
    public void setGlCubeMapId(int glCubeMapId) {
        this.glCubeMapId = glCubeMapId;
    }

    public int getGlDUDVMapId() {
        return glDUDVMapId;
    }
    public void setGlDUDVMapId(int glDUDVMapId) {
        this.glDUDVMapId = glDUDVMapId;
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
    public void setModelMatrix(float[] modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public GLShaderProgram getProgram() {
        return program;
    }
    public PointF getPosition() {
        return position;
    }
    public void setPosition(PointF position) {
        this.position = position;
    }

    public GLAnimation getAnimation() {
        return animation;
    }
    public void setAnimation(GLAnimation animation) {
        this.animation = animation;
    }

    public float getAmbientRate() {
        return ambientRate;
    }
    public void setAmbientRate(float ambientRate) {
        this.ambientRate = ambientRate;
    }

    public float getDiffuseRate() {
        return diffuseRate;
    }
    public void setDiffuseRate(float diffuseRate) {
        this.diffuseRate = diffuseRate;
    }

    public float getSpecularRate() {
        return specularRate;
    }
    public void setSpecularRate(float specularRate) {
        this.specularRate = specularRate;
    }

    public boolean isCubeMap() {
        return isCubeMap;
    }
    public void setCubeMap(boolean cubeMap) {
        isCubeMap = cubeMap;
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

    public void loadFromObject(GLSceneObject src) {
        vertexVBO.clearParamDataVBO();
        vertexVBO = src.getVertexVBO();

        texelVBO.clearParamDataVBO();
        texelVBO = src.getTexelVBO();

        normalVBO.clearParamDataVBO();
        normalVBO = src.getNormalVBO();

        clearVBOPtr(facesIBOPtr);
        facesIBOPtr =  src.getFacesIBOPtr();

        glTextureId = checkTextureBitmap(src) ? src.getGlTextureId() : loadTexture();
    }

    private boolean checkTextureBitmap(GLSceneObject src) {
        return textureResId == src.textureResId && mapID.equals(src.mapID) && textureColor == src.textureColor;
    }

    public void prepare(GLShaderProgram program) {
        program.linkVBOData(this);

        if (facesIBOPtr > 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, facesIBOPtr);
    }

    public void render() {
        /** USING VBO BUFFER */
        if (facesIBOPtr == 0)
            GLES20.glDrawArrays(GL_TRIANGLES, 0, getFacesCount());
        else {
            glDrawElements(GL_TRIANGLE_STRIP, getFacesCount(), GL_UNSIGNED_SHORT, 0);
        }

        /** USING RAM BUFFER */
            /*object.getIndexData().position(0);
            glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, object.getIndexData());*/
    }

    protected int loadTexture() {
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

    public void clearData() {
        clearVBOPtr(vertexVBO);
        clearVBOPtr(texelVBO);
        clearVBOPtr(normalVBO);
        clearVBOPtr(facesIBOPtr);
        facesIBOPtr = 0;

        glDeleteTextures(4, new int[]{glTextureId, glCubeMapId, glDUDVMapId, glNormalMapId}, 0);
    }

    public void setModelMatrix() {
        /** В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.*/
        /*float angle = -(float)(SystemClock.uptimeMillis() % LAND_ANIMATION_DELAY_MS) / LAND_ANIMATION_DELAY_MS * 360;

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angle, 0, 1, 0);*/
    }

    protected abstract Bitmap getTextureBitmap();
    public abstract int getFacesCount();
    protected abstract void createVertexesVBO();
    protected abstract void createTexelsVBO();
    protected abstract void createNormalsVBO();
    protected abstract int createFacesIBO();
}
