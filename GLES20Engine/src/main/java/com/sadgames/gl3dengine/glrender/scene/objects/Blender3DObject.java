package com.sadgames.gl3dengine.glrender.scene.objects;

import com.bulletphysics.collision.shapes.BoxShape;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.BufferedReader;
import java.io.StringReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.GLEngineConsts.MODELS_RESOURCE_FOLDER_NAME;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_CULL_FACE_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TRIANGLES_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_UNSIGNED_SHORT_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDisable;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDrawElements;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glEnable;

public class Blender3DObject extends ImportedObject {
    private static final String BLENDER_FILE_EXT = ".mdl";

    private class FacePointData {
        public short vertexIndex;
        public short textureCoordIndex;
        public short normalIndex;

        public FacePointData(short vertexIndex, short textureCoordIndex, short normalIndex) {
            this.vertexIndex = vertexIndex;
            this.textureCoordIndex = textureCoordIndex;
            this.normalIndex = normalIndex;
        }
    }

    private String objFileName;
    private boolean hasInvertedNormals = false;
    private boolean twoSidedSurface = false;

    protected float initialScale;
    protected Vector3f initialTranslation;

    public Blender3DObject(SysUtilsWrapperInterface sysUtilsWrapper, String objFileName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, objFileName /*+ COMPRESSED_TEXTURE_FILE_EXT*/, program, mass, tag);
        init(objFileName);
    }

    public Blender3DObject(SysUtilsWrapperInterface sysUtilsWrapper, String objFileName, GLShaderProgram program, int color, float mass, int tag) {
        super(sysUtilsWrapper, program, color, mass, tag);
        init(objFileName);
    }

    private void init(String objFileName) {
        this.objFileName = MODELS_RESOURCE_FOLDER_NAME + objFileName + BLENDER_FILE_EXT;
        this.initialScale = 1.0f;
        this.initialTranslation = new Vector3f(0f, 0f, 0f);

    }

    public void setHasInvertedNormals(boolean hasInvertedNormals) {
        this.hasInvertedNormals = hasInvertedNormals;
    }

    public void setTwoSidedSurface(boolean twoSidedSurface) {
        this.twoSidedSurface = twoSidedSurface;
    }

    public float getInitialScale() {
        return initialScale;
    }
    public void setInitialScale(float initialScale) {
        this.initialScale = initialScale;
    }

    public Vector3f getInitialTranslation() {
        return initialTranslation;
    }
    public void setInitialTranslation(float dx, float dy, float dz) {
        this.initialTranslation = new Vector3f(dx, dy, dz);
    }

    @Override
    protected void createCollisionShape(float[] vertexes) {
        switch (collisionShapeType) {
            case BOX:
                _shape = new BoxShape(new Vector3f(initialScale, initialScale, initialScale));
                break;
            default:
                super.createCollisionShape(vertexes);
        }
    }

    @Override public Raw3DModel getRaw3DModel() {
        return parseObjFile(objFileName);
    }

    private Raw3DModel parseObjFile(String modelFileName) {
        String readedLine;

        List<Vector3f> verticesList = new ArrayList<>();
        List<Vector2f> textureCoordsList = new ArrayList<>();
        List<Vector3f> normalsList = new ArrayList<>();
        List<Short> indicesList = new ArrayList<>();

        float[] verticesArray = null;
        float[] textureCoordsArray = null;
        float[] normalsArray = null;
        short[] indicesArray = null;

        try {
            BufferedReader model = new BufferedReader(new StringReader(sysUtilsWrapper.iReadTextFromFile(modelFileName)));

            while (!((readedLine = model.readLine()) == null) && !readedLine.startsWith("f ")) {
                String[] parsedValues = readedLine.split(" ");

                if (readedLine.startsWith("v ")) {
                    verticesList.add(new Vector3f(Float.parseFloat(parsedValues[1]), Float.parseFloat(parsedValues[2]), Float.parseFloat(parsedValues[3])));
                }
                else if (readedLine.startsWith("vt ")) {
                    textureCoordsList.add(new Vector2f(Float.parseFloat(parsedValues[1]), Float.parseFloat(parsedValues[2])));
                }
                else if (readedLine.startsWith("vn ")) {
                    normalsList.add(new Vector3f(Float.parseFloat(parsedValues[1]), Float.parseFloat(parsedValues[2]), Float.parseFloat(parsedValues[3])));
                }
            }

            textureCoordsArray = new float[verticesList.size() * 2];
            normalsArray = new float[verticesList.size() * 3];

            while (readedLine != null) {
                if (readedLine.startsWith("f ")) {
                    String[] parsedValues = readedLine.split(" ");
                    for (short i = 1; i < parsedValues.length; i++) {
                        if (i == 4) {
                            parseFacePoint(textureCoordsList, normalsList, indicesList, textureCoordsArray, normalsArray, parsedValues[1], true);
                            parseFacePoint(textureCoordsList, normalsList, indicesList, textureCoordsArray, normalsArray, parsedValues[3], true);
                        }

                        parseFacePoint(textureCoordsList, normalsList, indicesList, textureCoordsArray, normalsArray, parsedValues[i], false);
                    }
                }

                readedLine = model.readLine();
            }

            model.close();
        }
        catch (Exception exc) {
            throw new InvalidParameterException(String.format("Invalid Blender 3D-model file: \"%s\"", modelFileName));
        }

        verticesArray = new float[verticesList.size() * 3];
        int vertexPointer = 0;
        for (Vector3f vertex : verticesList) {
            verticesArray[vertexPointer++] = vertex.x * initialScale + initialTranslation.x;
            verticesArray[vertexPointer++] = vertex.y * initialScale + initialTranslation.y;
            verticesArray[vertexPointer++] = vertex.z * initialScale + initialTranslation.z;
        }

        setFacesCount(indicesList.size());
        indicesArray = new short[getFacesCount()];
        short indexPointer = 0;
        for (Short index : indicesList)
            indicesArray[indexPointer++] = index;

        if (getFacesCount() == 0) {
            int pointer = 0;
            for (Vector2f currentTextureCoords : textureCoordsList) {
                if (pointer >= verticesList.size()) break;

                textureCoordsArray[pointer * 2] = currentTextureCoords.x;
                textureCoordsArray[pointer * 2 + 1] = currentTextureCoords.y;

                pointer++;
            }

            pointer = 0;
            for (Vector3f currentNormal : normalsList) {
                if (pointer >= verticesList.size()) break;

                normalsArray[pointer * 3] = hasInvertedNormals ? -currentNormal.x : currentNormal.x;
                normalsArray[pointer * 3 + 1] = hasInvertedNormals ? -currentNormal.y : currentNormal.y;
                normalsArray[pointer * 3 + 2] = hasInvertedNormals ? -currentNormal.z : currentNormal.z;

                pointer++;
            }

            indicesArray = new short[verticesList.size()];
            for (int vertexCount = 0; vertexCount < verticesList.size(); vertexCount++)
                indicesArray[vertexCount] = (short) vertexCount;

            setFacesCount(verticesList.size());
        }

        return new Raw3DModel(verticesArray, textureCoordsArray, normalsArray, indicesArray);
    }

    private void parseFacePoint(List<Vector2f> textureCoordsList,
                                List<Vector3f> normalsList,
                                List<Short> indicesList,
                                float[] textureCoordsArray,
                                float[] normalsArray,
                                String parsedValue,
                                boolean addIndexOnly) {
        FacePointData facePointData = getFacePointData(parsedValue.split("/"));

        short currentVertexIndex = facePointData.vertexIndex;
        indicesList.add(currentVertexIndex);

        if (!addIndexOnly) {
            Vector2f currentTextureCoords = textureCoordsList.get(facePointData.textureCoordIndex);
            textureCoordsArray[currentVertexIndex * 2] = currentTextureCoords.x;
            textureCoordsArray[currentVertexIndex * 2 + 1] = 1f - currentTextureCoords.y;

            Vector3f currentNormal = normalsList.get(facePointData.normalIndex);
            normalsArray[currentVertexIndex * 3] = hasInvertedNormals ? -currentNormal.x : currentNormal.x;
            normalsArray[currentVertexIndex * 3 + 1] = hasInvertedNormals ? -currentNormal.y : currentNormal.y;
            normalsArray[currentVertexIndex * 3 + 2] = hasInvertedNormals ? -currentNormal.z : currentNormal.z;
        }
    }

    private FacePointData getFacePointData(String[] facePointData) {
        return new FacePointData((short)(Short.parseShort(facePointData[0]) - 1),
                                 (short)(Short.parseShort(facePointData[1]) - 1),
                                 (short)(Short.parseShort(facePointData[2]) - 1));
    }

    @Override
    public void render() {
        if (twoSidedSurface) glDisable(get_GL_CULL_FACE_value());

        glDrawElements(get_GL_TRIANGLES_value(), getFacesCount(), get_GL_UNSIGNED_SHORT_value());

        if (twoSidedSurface) glEnable(get_GL_CULL_FACE_value());
    }

}
