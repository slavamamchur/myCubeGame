package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.InputStream;
import java.security.InvalidParameterException;

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
        try {
            InputStream modelStream = sysUtilsWrapper.getResourceStream(modelFileName);

            //TODO: parse obj-file
        }
        catch (Exception exc) {
            throw new InvalidParameterException(String.format("Invalid Blender 3D-model file: \"%s\"", modelFileName));
        }

        return null;
    }
}
