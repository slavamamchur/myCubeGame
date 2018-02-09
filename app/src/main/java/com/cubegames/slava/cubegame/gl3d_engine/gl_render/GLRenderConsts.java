package com.cubegames.slava.cubegame.gl3d_engine.gl_render;

public  class GLRenderConsts {

    public enum GLParamType {
        FLOAT_ATTRIB_ARRAY_PARAM,
        FLOAT_UNIFORM_VECTOR_PARAM,
        FLOAT_UNIFORM_MATRIX_PARAM,
        FLOAT_UNIFORM_PARAM,
        INTEGER_UNIFORM_PARAM
    }

    public enum GLObjectType {
        WATER_OBJECT,
        TERRAIN_OBJECT,
        SKY_OBJECT,
        LIGHT_OBJECT,
        GAME_ITEM_OBJECT,
        SHADOWMAP_OBJECT,
        GUI_OBJECT,
        UNKNOWN_OBJECT
    }

    public enum GLAnimationType {
        TRANSLATE_ANIMATION,
        ROTATE_ANIMATION,
        ZOOM_ANIMATION
    }

    public enum ShadowMapQuality {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA
    }

    public static final float[] SHADOW_MAP_RESOLUTION = new float[] {0.5f, 1.0f, 1.5f, 2.0f};

    public static final int VERTEX_SIZE = 3;
    public static final int TEXEL_UV_SIZE = 2;
    public static final int VBO_ITEM_SIZE = (VERTEX_SIZE + TEXEL_UV_SIZE);
    public static final int VBO_STRIDE = VBO_ITEM_SIZE * 4;

    public static final float LAND_SIZE_IN_WORLD_SPACE = 7.0f;
    public static final float LAND_SIZE_IN_KM = 242.0f; //242Km
    public static final float SEA_SIZE_IN_WORLD_SPACE = 7.0f;

    public static final String VERTEXES_PARAM_NAME = "a_Position";
    public static final String TEXELS_PARAM_NAME = "a_Texture";
    public static final String NORMALS_PARAM_NAME = "a_Normal";
    public static final String ACTIVE_TEXTURE_SLOT_PARAM_NAME = "u_TextureUnit";
    public static final String ACTIVE_CUBEMAP_SLOT_PARAM_NAME = "u_CubeMapUnit";
    public static final String ACTIVE_NORMALMAP_SLOT_PARAM_NAME = "u_NormalMapUnit";
    public static final String ACTIVE_DUDVMAP_SLOT_PARAM_NAME = "u_DUDVMapUnit";
    public static final String ACTIVE_SHADOWMAP_SLOT_PARAM_NAME = "uShadowTexture";
    public static final String IS_CUBEMAP_PARAM_NAME = "u_isCubeMap";
    public static final String IS_CUBEMAPF_PARAM_NAME = "u_isCubeMapF";
    public static final String IS_NORMALMAP_PARAM_NAME = "u_isNormalMap";
    public static final String MVP_MATRIX_PARAM_NAME = "u_MVP_Matrix";
    public static final String LIGHT_MVP_MATRIX_PARAM_NAME = "uShadowProjMatrix";
    public static final String MV_MATRIX_PARAM_NAME = "u_MV_Matrix";
    public static final String MV_MATRIXF_PARAM_NAME = "u_MV_MatrixF";
    public static final String LIGHT_POSITION_PARAM_NAME = "u_lightPosition";
    public static final String LIGHT_POSITIONF_PARAM_NAME = "u_lightPositionF";
    public static final String LIGHT_COLOUR_PARAM_NAME = "u_lightColour";
    public static final String CAMERA_POSITION_PARAM_NAME = "u_camera";
    public static final String RND_SEED__PARAM_NAME = "u_RndSeed";
    public static final String AMBIENT_RATE_PARAM_NAME = "u_AmbientRate";
    public static final String DIFFUSE_RATE_PARAM_NAME = "u_DiffuseRate";
    public static final String SPECULAR_RATE_PARAM_NAME = "u_SpecularRate";
    public static final String UX_PIXEL_OFFSET_PARAM_NAME = "uxPixelOffset";
    public static final String UY_PIXEL_OFFSET_PARAM_NAME = "uyPixelOffset";

    public static final String TERRAIN_MESH_OBJECT = "TERRAIN_MESH_OBJECT";
    public static final String WATER_MESH_OBJECT = "WATER_MESH_OBJECT";
    public static final String CHIP_MESH_OBJECT = "CHIP_MESH_OBJECT";
    public static final String DICE_MESH_OBJECT_1 = "DICE_MESH_OBJECT_1";

    public static final String OES_DEPTH_TEXTURE_EXTENSION = "OES_depth_texture";

    public static final String TERRAIN_VERTEX_SHADER = "shaders/vertex_shader.glsl";
    public static final String TERRAIN_FRAGMENT_SHADER = "shaders/fragment_shader.glsl";
    public static final String SHADOWMAP_VERTEX_SHADER = "shaders/v_depth_map.glsl";
    public static final String SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT = "shaders/depth_tex_v_depth_map.glsl";
    public static final String SHADOWMAP_FRAGMENT_SHADER = "shaders/f_depth_map.glsl";
    public static final String SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT = "shaders/depth_tex_f_depth_map.glsl";
    public static final String GUI_VERTEX_SHADER = "shaders/gui_vertex.glsl";
    public static final String GUI_FRAGMENT_SHADER = "shaders/gui_fragment.glsl";

}
