package com.sadgames.gl3dengine;

import static com.sadgames.sysutils.common.SysUtilsConsts.BYTES_IN_MB;

public class GLEngineConsts {

    public static final String MODELS_RESOURCE_FOLDER_NAME = "models/";
    public static final String COMPRESSED_TEXTURE_FILE_EXT = ".pkg";

    public static final String NOT_ENOUGH_SPACE_IN_CACHE_ERROR_MESSAGE = "Not enough space: item size > cache size.";

    public static final   long TEXTURE_CACHE_SIZE                      = 64L * BYTES_IN_MB;

}
