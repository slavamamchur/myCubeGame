package com.cubegames.slava.cubegame.mapgl;

public  class GLRenderConsts {

    public static final int VERTEX_SIZE = 3;
    public static final int TEXEL_UV_SIZE = 2;
    public static final int VBO_ITEM_SIZE = (VERTEX_SIZE + TEXEL_UV_SIZE);
    public static final int VBO_STRIDE = VBO_ITEM_SIZE * 4;
    public static final int LAND_INTERPOLATOR_DIM = 120;

    public static final String VERTEXES_PARAM_NAME = "a_Position";
    public static final String TEXELS_PARAM_NAME = "a_Texture";
    public static final String NORMALS_PARAM_NAME = "a_Normal";
    public static final String ACTIVE_TEXTURE_SLOT_PARAM_NAME = "u_TextureUnit";
    public static final String MVP_MATRIX_PARAM_NAME = "u_MVP_Matrix";
    public static final String MV_MATRIX_PARAM_NAME = "u_MV_Matrix";
    public static final String LIGHT_POSITION_PARAM_NAME = "u_lightPosition";
    public static final String CAMERA_POSITION_PARAM_NAME = "u_camera";
    public static final String TERRAIN_MESH_OBJECT = "TERRAIN_MESH_OBJECT";

    public enum GLParamType {
        FLOAT_ATTRIB_ARRAY_PARAM,
        FLOAT_UNIFORM_VECTOR_PARAM,
        FLOAT_UNIFORM_MATRIX_PARAM,
        INTEGER_UNIFORM_PARAM
    }

    public enum GLObjectType {
        WATER_OBJECT,
        TERRAIN_OBJECT,
        SKY_OBJECT,
        LIGHT_OBJECT
    }

}
