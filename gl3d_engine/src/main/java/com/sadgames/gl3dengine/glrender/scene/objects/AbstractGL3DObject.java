package com.sadgames.gl3dengine.glrender.scene.objects;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.sadgames.gl3dengine.glrender.scene.GLAnimation;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParamVBO;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.nio.ShortBuffer;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDrawElements;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEXES_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.scene.GLAnimation.ROTATE_BY_X;
import static com.sadgames.gl3dengine.glrender.scene.GLAnimation.ROTATE_BY_Y;
import static com.sadgames.gl3dengine.glrender.scene.GLAnimation.ROTATE_BY_Z;

public abstract class AbstractGL3DObject implements GLAnimation.IAnimatedObject {

    protected SysUtilsWrapperInterface sysUtilsWrapper;
    private GLObjectType objectType;
    protected AbstractTexture glTexture = null;
    private GLShaderParamVBO vertexVBO = null;
    private GLShaderParamVBO texelVBO = null;
    private GLShaderParamVBO normalVBO = null;
    private ShortBuffer indexData;
    protected int facesIBOPtr = 0;
    private float[] modelMatrix = new float[16];
    private GLShaderProgram program;
    private GLAnimation animation = null;
    private Vector2f place = new Vector2f(0, 0);
    private float rotationX = 0;
    private float rotationY = 0;
    private float rotationZ = 0;
    private float scaleFactor = 1;

    private float ambientRate = 0.4f;
    private float diffuseRate = 1.0f;
    private float specularRate = 0.9f;
    private boolean isCubeMap = false;
    private int glNormalMapId = 0;
    private int glCubeMapId = 0;
    private int glDUDVMapId = 0;

    protected String textureResName = "";
    protected int textureColor = 0;

    protected boolean isCastShadow = true;

    public AbstractGL3DObject(SysUtilsWrapperInterface sysUtilsWrapper, GLObjectType type, GLShaderProgram program) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        objectType = type;
        this.program = program;

        Matrix.setIdentityM(modelMatrix, 0);

        createVBOParams();
    }

    public SysUtilsWrapperInterface getSysUtilsWrapper() {
        return sysUtilsWrapper;
    }
    public GLObjectType getObjectType() {
        return objectType;
    }
    public int getGlTextureId() {
        return glTexture.getTextureId();
    }
    public AbstractTexture getGlTexture() {
        return glTexture;
    }

    public void setGlTexture(AbstractTexture glTexture) {
        this.glTexture = glTexture;
    }

    public boolean hasNormalMap() {
        return glNormalMapId > 0;
    }
    public boolean hasDUDVMap() {
        return glDUDVMapId > 0;
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
    public Vector2f getPlace() {
        return place;
    }
    public void setPlace(Vector2f place) {
        this.place = place;
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

    public boolean isCastShadow() {
        return isCastShadow;
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
        glTexture = loadTexture();
    }

    public void loadFromObject(AbstractGL3DObject src) {
        vertexVBO.clearParamDataVBO();
        vertexVBO = src.getVertexVBO();

        texelVBO.clearParamDataVBO();
        texelVBO = src.getTexelVBO();

        normalVBO.clearParamDataVBO();
        normalVBO = src.getNormalVBO();

        clearVBOPtr(facesIBOPtr);
        facesIBOPtr =  src.getFacesIBOPtr();

        glTexture = checkTextureBitmap(src) ? src.glTexture : loadTexture();
    }

    private boolean checkTextureBitmap(AbstractGL3DObject src) {
        return textureResName.equals(src.textureResName) && textureColor == src.textureColor;
    }


    public void prepare() {
        prepare(program);
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
            /*object.getIndexData().place(0);
            glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, object.getIndexData());*/
    }

    public AbstractTexture loadTexture() { //TODO: get from cache
        if (textureResName != null && !textureResName.isEmpty()) {
            return BitmapTexture.createInstance(sysUtilsWrapper, textureResName);
        }
        else if (textureColor != 0) {
            return BitmapTexture.createInstance(sysUtilsWrapper, String.valueOf(textureColor));
        }
        else
            return null;
    }

    private void clearVBOPtr(int vboPtr) {
        if (vboPtr != 0) {
            glDeleteBuffers(1, new int[]{vboPtr}, 0);
        }
    }

    private void clearVBOPtr(GLShaderParamVBO param) {
        if (param != null) {
            param.clearParamDataVBO();
        }
    }

    public void clearData() {
        clearVBOPtr(vertexVBO);
        clearVBOPtr(texelVBO);
        clearVBOPtr(normalVBO);
        clearVBOPtr(facesIBOPtr);

        facesIBOPtr = 0;
        glTexture = null;
    }

    /*public void setModelMatrix() {
        *//** В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.*//*
        float angle = -(float)(SystemClock.uptimeMillis() % LAND_ANIMATION_DELAY_MS) / LAND_ANIMATION_DELAY_MS * 360;

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angle, 0, 1, 0);
    }*/

    public void setInWorldPosition(Vector2f newPlace) {
        setPlace(newPlace);
        updateTransform();
    }

    public float getRotationX() {
        return rotationX;
    }
    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
        updateTransform();
    }

    public float getRotationY() {
        return rotationY;
    }
    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
        updateTransform();
    }

    public float getRotationZ() {
        return rotationZ;
    }
    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
        updateTransform();
    }

    public float getScaleFactor() {
        return scaleFactor;
    }
    public void setScaleFactor(float scaleFactor) {
        //float deltaScale = 1 / this.scaleFactor * scaleFactor;
        this.scaleFactor = scaleFactor;
        updateTransform();
    }

    private void updateTransform() {
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, place.x, 0, place.y);

        Matrix.rotateM(modelMatrix, 0, rotationX, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, rotationY, 0, 1, 0);
        Matrix.rotateM(modelMatrix, 0, rotationZ, 0, 0, 1);

        Matrix.scaleM(modelMatrix, 0, scaleFactor, scaleFactor, scaleFactor);
    }

    public abstract int getFacesCount();
    protected abstract void createVertexesVBO();
    protected abstract void createTexelsVBO();
    protected abstract void createNormalsVBO();
    protected abstract int createFacesIBO();

    @Override
    public float[] getTransformationMatrix() {
        return getModelMatrix();
    }

    @Override
    public void setPosition(Vector3f position) {
        setInWorldPosition(new Vector2f(position.x, position.z));
    }

    @Override
    public void setRotation(float angle, short rotationAxesMask) {
        if ((rotationAxesMask & ROTATE_BY_X) != 0) rotationX += angle;
        if ((rotationAxesMask & ROTATE_BY_Y) != 0) rotationY += angle;
        if ((rotationAxesMask & ROTATE_BY_Z) != 0) rotationZ += angle;

        updateTransform();
    }

    @Override
    public void setZoomLevel(float zoomLevel) {
        setScaleFactor(zoomLevel);
    }

    @Override
    public void onAnimationEnd() {

    }
}
