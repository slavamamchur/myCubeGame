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



        }
        catch (Exception exc) {
            throw new InvalidParameterException(String.format("Invalid Blender 3D-model file: \"%s\"", modelFileName));
        }

        return null;
    }
}
