package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.MaterialPropertiesObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.CubeMapTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParamVBO;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.common.MathUtils;

import java.nio.ShortBuffer;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_ELEMENT_ARRAY_BUFFER_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TRIANGLES_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TRIANGLE_STRIP_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_UNSIGNED_SHORT_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glActiveTexture;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glBindBuffer;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glBindTexture2D;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDeleteBuffers;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDrawArrays;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDrawElements;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_BLENDING_MAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_DUDVMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_NORMALMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_REFRACTION_MAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.AMBIENT_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DIFFUSE_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.HAS_REFLECT_MAP_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.IS_CUBEMAPF_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.IS_CUBEMAP_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SPECULAR_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEXES_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_X;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Y;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Z;

public abstract class AbstractGL3DObject extends SceneObjectsTreeItem implements GLAnimation.IAnimatedObject {

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
    private boolean isCubeMap = false; //TODO: set material

    protected AbstractTexture glNormalMap = null;
    protected AbstractTexture glCubeMap = null;
    protected AbstractTexture glDUDVMap = null;
    protected AbstractTexture glBlendingMap = null;
    protected CubeMapTexture waterReflectionMap = null;

    protected String textureResName = "";

    protected boolean castShadow = true;

    public AbstractGL3DObject(GLObjectType type, GLShaderProgram program) {
        super();

        this.objectType = type;
        this.program = program;

        MathUtils.setIdentityM(modelMatrix, 0);

        createVBOParams();
    }

    public GLObjectType getObjectType() {
        return objectType;
    }
    public AbstractTexture getGlTexture() {
        return glTexture;
    }

    public void setGlTexture(AbstractTexture glTexture) {
        this.glTexture = glTexture;
    }

    public boolean hasNormalMap() {
        return glNormalMap != null;
    }
    public boolean hasDUDVMap() {
        return glDUDVMap != null;
    }
    public boolean hasCubeMap() {
        return glCubeMap != null;
    }
    public boolean hasBlendingMap() {
        return glBlendingMap != null;
    }
    public boolean hasWaterReflectionMap() {
        return waterReflectionMap != null;
    }

    public AbstractTexture getGlNormalMap() {
        return glNormalMap;
    }
    public void setGlNormalMap(AbstractTexture glNormalMap) {
        this.glNormalMap = glNormalMap;
    }

    public AbstractTexture getGlCubeMap() {
        return glCubeMap;
    }
    public void setGlCubeMap(AbstractTexture glCubeMap) {
        this.glCubeMap = glCubeMap;
    }

    public AbstractTexture getGlDUDVMap() {
        return glDUDVMap;
    }
    public void setGlDUDVMap(AbstractTexture glDUDVMap) {
        this.glDUDVMap = glDUDVMap;
    }

    public AbstractTexture getGlBlendingMap() {
        return glBlendingMap;
    }
    public void setGlBlendingMap(AbstractTexture glBlendingMap) {
        this.glBlendingMap = glBlendingMap;
    }

    public CubeMapTexture getWaterReflectionMap() {
        return waterReflectionMap;
    }
    public void setWaterReflectionMap(CubeMapTexture waterReflectionMap) {
        this.waterReflectionMap = waterReflectionMap;
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

    @SuppressWarnings("unused") public ShortBuffer getIndexData() {
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
    public void animationStart() {
        if (animation != null && !animation.isInProgress())
            animation.startAnimation(this, null);
    }
    public void animationStop() {
        if (animation != null && animation.isInProgress())
            animation.stopAnimation();
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
        return castShadow;
    }
    public void setCastShadow(boolean castShadow) {
        this.castShadow = castShadow;
    }

    public void setMaterialProperties(MaterialPropertiesObject material) {
        if (material == null)
            return;

        diffuseRate = material.getDiffuseRate();
        ambientRate = material.getAmbientRate();
        specularRate = material.getSpecularRate();

        textureResName = material.getDiffuseMapName();
        if (textureResName != null)
            glTexture = TextureCacheManager.getInstance().getItem(textureResName);

        if (material.getNormalMapName() != null)
            glNormalMap = TextureCacheManager.getInstance().getItem(material.getNormalMapName());

        if (material.getDUDVMapName() != null)
            glNormalMap = TextureCacheManager.getInstance().getItem(material.getDUDVMapName());

        if (material.getNormalMapName() != null)
            glNormalMap = TextureCacheManager.getInstance().getItem(material.getNormalMapName());
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

        glTexture = loadTexture();
    }

    @Override
    public boolean equals(Object obj) {
        return !((obj == null) || !(obj instanceof AbstractGL3DObject))
               && ((AbstractGL3DObject) obj).getVertexVBO().getVboPtr() == vertexVBO.getVboPtr();
    }

    public void bindVBO() {
        bindVBO(program);
    }
    public void bindVBO(GLShaderProgram program) {
        program.linkVBOData(this);

        if (facesIBOPtr > 0)
            glBindBuffer(get_GL_ELEMENT_ARRAY_BUFFER_value(), facesIBOPtr);
    }

    public void unbindTexture(int slot) {
        glActiveTexture(slot);
        glBindTexture2D(0);
    }

    public void bindMaterial() {
        bindMaterial(program);
    }
    public void bindMaterial(GLShaderProgram program) {
        int textureSlotIndex = 0;

        if (glTexture != null) {
            if (glTexture.getTextureId() == 0)
                glTexture = loadTexture();

            glTexture.bind(textureSlotIndex);
            program.setTextureSlotData(textureSlotIndex);

            textureSlotIndex++;
        }

        GLShaderParam param = program.paramByName(AMBIENT_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(ambientRate);
        param = program.paramByName(DIFFUSE_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(diffuseRate);
        param = program.paramByName(SPECULAR_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(specularRate);

        int hasNormalMap = hasNormalMap() ? 1 : 0;
        param = program.paramByName(IS_CUBEMAP_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(hasNormalMap);
        param = program.paramByName(IS_CUBEMAPF_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(hasNormalMap);

        param = program.paramByName(ACTIVE_REFRACTION_MAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && isCubeMap()) {
            if (glCubeMap.getTextureId() == 0)
                glCubeMap = TextureCacheManager.getInstance().getItem(((BitmapTexture) glCubeMap).getTextureName());

            glCubeMap.bind(textureSlotIndex);
            param.setParamValue(textureSlotIndex);
            textureSlotIndex++;
        }

        param = program.paramByName(ACTIVE_NORMALMAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && hasNormalMap()) {
            if (glNormalMap.getTextureId() == 0)
                glNormalMap = TextureCacheManager.getInstance().getItem(((BitmapTexture) glNormalMap).getTextureName());

            glNormalMap.bind(textureSlotIndex);
            param.setParamValue(textureSlotIndex);
            textureSlotIndex++;
        }

        param = program.paramByName(ACTIVE_DUDVMAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && hasDUDVMap()) {
            if (glDUDVMap.getTextureId() == 0)
                glDUDVMap = TextureCacheManager.getInstance().getItem(((BitmapTexture) glDUDVMap).getTextureName());

            glDUDVMap.bind(textureSlotIndex);
            param.setParamValue(textureSlotIndex);
            textureSlotIndex++;
        }

        /** blending map */
        param = program.paramByName(ACTIVE_BLENDING_MAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && hasBlendingMap()) {
            if (glBlendingMap.getTextureId() == 0)
                glBlendingMap = TextureCacheManager.getInstance().getItem(((BitmapTexture) glBlendingMap).getTextureName());

            glBlendingMap.bind(textureSlotIndex);
            param.setParamValue(textureSlotIndex);
            textureSlotIndex++;
        }

        /** reflection map */
        /*int hasReflectMap = 0;
        param = program.paramByName(ACTIVE_SKYBOX_MAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0) {
            if (hasWaterReflectionMap()) {
                hasReflectMap = 1;

                if (waterReflectionMap.getTextureId() == 0)
                    waterReflectionMap = (CubeMapTexture) TextureCacheManager.getInstance(sysUtilsWrapper).getItem(waterReflectionMap.getTextureName());

                waterReflectionMap.bind(textureSlotIndex);
                param.setParamValue(textureSlotIndex);
                textureSlotIndex++;
            }
        }*/

        param = program.paramByName(HAS_REFLECT_MAP_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setParamValue(/*hasReflectMap*/1);
    }

    public void render() {
        /** USING VBO BUFFER */
        if (facesIBOPtr == 0)
            glDrawArrays(get_GL_TRIANGLES_value(), 0, getFacesCount());
        else {
            glDrawElements(get_GL_TRIANGLE_STRIP_value(), getFacesCount(), get_GL_UNSIGNED_SHORT_value());
        }

        /** USING RAM BUFFER */
            /*object.getIndexData().place(0);
            glDrawElements(GL_TRIANGLE_STRIP, object.getFacesCount(), GL_UNSIGNED_SHORT, object.getIndexData());*/
    }

    public AbstractTexture loadTexture() {
        if (textureResName != null && !textureResName.isEmpty()) {
            return TextureCacheManager.getInstance().getItem(textureResName);
        }
        else
            return null;
    }

    public synchronized void reloadTexture () {
        if (glTexture != null) {
            unbindTexture(1);
            glTexture = TextureCacheManager.getInstance().reloadItem(((BitmapTexture)getGlTexture()).getTextureName());
        }
    }

    private void clearVBOPtr(int vboPtr) {
        if (vboPtr != 0) {
            glDeleteBuffers(new int[]{vboPtr});
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
        MathUtils.setIdentityM(modelMatrix, 0);

        MathUtils.rotateM(modelMatrix, rotationX, rotationY, rotationZ);

        modelMatrix[12] += place.x;
        modelMatrix[14] += place.y;

        MathUtils.scaleM(modelMatrix, 0, scaleFactor, scaleFactor, scaleFactor);
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
        if ((rotationAxesMask & ROTATE_BY_X) != 0) rotationX = (angle) % 360;
        if ((rotationAxesMask & ROTATE_BY_Y) != 0) rotationY = (angle) % 360;
        if ((rotationAxesMask & ROTATE_BY_Z) != 0) rotationZ = (angle) % 360;

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
