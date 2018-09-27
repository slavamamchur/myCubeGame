package com.sadgames.gl3dengine.glrender.scene.objects;

import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.util.ObjectArrayList;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;

public abstract class ImportedObject extends GameItemObject {

    public enum ShapeType {
        UNKNOWN,
        BOX,
        SPHERE,
        PYRAMID
    }

    protected Raw3DModel raw3DModel = null;
    protected int facesCount = 0;
    protected ShapeType collisionShapeType = ShapeType.UNKNOWN;

    public ImportedObject(SysUtilsWrapperInterface sysUtilsWrapper, String textureResName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, textureResName, program, mass, tag);
    }

    public ImportedObject(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, int color, float mass, int tag) {
        super(sysUtilsWrapper, program, color, mass, tag);
    }

    public abstract Raw3DModel getRaw3DModel();

    @SuppressWarnings("unused") public ShapeType getCollisionShapeType() {
        return collisionShapeType;
    }
    @SuppressWarnings("unused") public void setCollisionShapeType(ShapeType collisionShapeType) {
        this.collisionShapeType = collisionShapeType;
    }
    public void setCollisionShapeType(short key) {
        this.collisionShapeType = ShapeType.values()[key];
    }

    @Override
    public void loadFromObject(AbstractGL3DObject src) {
        super.loadFromObject(src);

        facesCount = src.getFacesCount();
        raw3DModel = ((ImportedObject)src).getRaw3DModel();
        _shape = ((ImportedObject)src).get_shape();
    }

    @Override
    public int getFacesCount() {
        return facesCount;
    }
    public void setFacesCount(int facesCount) {
        this.facesCount = facesCount;
    }

    @Override protected float[] getVertexesArray() {
        return raw3DModel.getVertexes();
    }
    @Override protected float[] getNormalsArray() {
        return raw3DModel.getNormals();
    }
    @Override protected short[] getFacesArray() {
        return raw3DModel.getFaces();
    }
    protected float[] getTextureCoordsArray() {
        return raw3DModel.getTextureCoords();
    }

    @Override
    protected void createVertexesVBO() {
        raw3DModel = getRaw3DModel();

        float[] vertexes = getVertexesArray();

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertexes);
        vertexData.position(0);

        getVertexVBO().setParamValue(VERTEX_SIZE, 0, 0, vertexData);
        vertexData.limit(0);

        createCollisionShape(vertexes);
    }

    @Override
    protected void createCollisionShape(float[] vertexes) {
        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();
        _shape = new ConvexHullShape(points);
        for (int i = 0; i < vertexes.length; i+=3)
        {
            Vector3f btv = new Vector3f(vertexes[i], vertexes[i + 1], vertexes[i + 2]);
            ((ConvexHullShape)_shape).addPoint(btv);
        }
    }

    @Override
    protected void createTexelsVBO() {
        float[] textureCoords = getTextureCoordsArray();

        FloatBuffer textureCoordsData = ByteBuffer
                .allocateDirect(textureCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureCoordsData.put(textureCoords);
        textureCoordsData.position(0);

        getTexelVBO().setParamValue(TEXEL_UV_SIZE, 0, 0, textureCoordsData);
        textureCoordsData.limit(0);
    }

}
