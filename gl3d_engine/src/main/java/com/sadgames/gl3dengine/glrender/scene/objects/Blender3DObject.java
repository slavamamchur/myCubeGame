package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.BufferedReader;
import java.io.StringReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.GLEngineConsts.COMPRESSED_TEXTURE_FILE_EXT;
import static com.sadgames.gl3dengine.GLEngineConsts.MODELS_RESOURCE_FOLDER_NAME;

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

    public Blender3DObject(SysUtilsWrapperInterface sysUtilsWrapper, String objFileName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, objFileName + COMPRESSED_TEXTURE_FILE_EXT, program, mass, tag);

        this.objFileName = MODELS_RESOURCE_FOLDER_NAME + objFileName + BLENDER_FILE_EXT;
    }

    @Override protected Raw3DModel getRaw3DModel() {
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
            //TODO: parse obj-file
            BufferedReader model = new BufferedReader(new StringReader(sysUtilsWrapper.iReadTextFromFile(modelFileName)));

            while (!(readedLine = model.readLine()).startsWith("f ")) {
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
                    for (short i = 1; i < 4; i++) {
                        FacePointData facePointData = getFacePointData(parsedValues[i].split("/"));

                        short currentVertexIndex = facePointData.vertexIndex;
                        indicesList.add(currentVertexIndex);

                        Vector2f currentTextureCoords = textureCoordsList.get(facePointData.textureCoordIndex);
                        textureCoordsArray[currentVertexIndex * 2] = currentTextureCoords.x;
                        textureCoordsArray[currentVertexIndex * 2 + 1] = 1f - currentTextureCoords.y;

                        Vector3f currentNormal = normalsList.get(facePointData.normalIndex);
                        normalsArray[currentVertexIndex * 3] = currentNormal.x;
                        normalsArray[currentVertexIndex * 3 + 1] = currentNormal.y;
                        normalsArray[currentVertexIndex * 3 + 2] = currentNormal.z;
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
        short vertexPointer = 0;
        for (Vector3f vertex : verticesList) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        indicesArray = new short[indicesList.size()];
        short indexPointer = 0;
        for (Short index : indicesList)
            indicesArray[indexPointer++] = index;

        return new Raw3DModel(verticesArray, textureCoordsArray, normalsArray, indicesArray);
    }

    private FacePointData getFacePointData(String[] facePointData) {
        return new FacePointData((short)(Short.parseShort(facePointData[0]) - 1),
                                 (short)(Short.parseShort(facePointData[1]) - 1),
                                 (short)(Short.parseShort(facePointData[2]) - 1));
    }
}
