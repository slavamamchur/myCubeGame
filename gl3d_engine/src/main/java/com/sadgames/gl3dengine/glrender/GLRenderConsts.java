package com.sadgames.gl3dengine.glrender;

import javax.vecmath.Vector3f;

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
        SKY_BOX_OBJECT,
        LIGHT_OBJECT,
        GAME_ITEM_OBJECT,
        SHADOW_MAP_OBJECT,
        GUI_OBJECT,
        UNKNOWN_OBJECT
    }

    public enum GLAnimationType {
        TRANSLATE_ANIMATION,
        ROTATE_ANIMATION,
        ZOOM_ANIMATION
    }

    public enum GraphicsQuality {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA
    }

    public static final float[] SHADOW_MAP_RESOLUTION_SCALE = new float[] {0.5f, 1.0f, 1.5f, 2.0f};
    public static final int[] TEXTURE_RESOLUTION_SCALE = new int[] {4, 2, 1, 1};

    public static final int VERTEX_SIZE = 3;
    public static final int TEXEL_UV_SIZE = 2;
    public static final int VBO_ITEM_SIZE = (VERTEX_SIZE + TEXEL_UV_SIZE);
    public static final int VBO_STRIDE = VBO_ITEM_SIZE * 4;

    public static final float LAND_SIZE_IN_WORLD_SPACE = 7.0f;
    public static final float LAND_SIZE_IN_KM = 242.0f; //242Km
    public static final float SEA_SIZE_IN_WORLD_SPACE = 7.0f;

    public final static float    DEFAULT_LIGHT_X        = -2.20F;
    public final static float    DEFAULT_LIGHT_Y        =  1.70F;
    public final static float    DEFAULT_LIGHT_Z        = -3.20F;
    public final static Vector3f DEFAULT_LIGHT_COLOUR   = new Vector3f(1.0f, 1.0f, 0.8f);
    
    public static final Vector3f DEFAULT_GRAVITY_VECTOR = new Vector3f(0f, -9.8f, 0f);
    public  final static float    SIMULATION_FRAMES_IN_SEC = 60f; /** FPS */

    public final static float    DEFAULT_CAMERA_X       = 0f;
    public final static float    DEFAULT_CAMERA_Y       = 3f;
    public final static float    DEFAULT_CAMERA_Z       = 3f;
    public final static float    DEFAULT_CAMERA_PITCH   = 45.0f;
    public final static float    DEFAULT_CAMERA_YAW     = 0.0f;
    public final static float    DEFAULT_CAMERA_ROLL    = 0.0f;
    public static final float    DEFAULT_CAMERA_VERTICAL_FOV = 35.0f;

    public final static float    WAVE_SPEED             = 0.04f;

    public static final String VERTEXES_PARAM_NAME = "a_Position";
    public static final String TEXELS_PARAM_NAME = "a_Texture";
    public static final String NORMALS_PARAM_NAME = "a_Normal";
    public static final String ACTIVE_TEXTURE_SLOT_PARAM_NAME = "u_TextureUnit";
    public static final String ACTIVE_REFRACTION_MAP_SLOT_PARAM_NAME = "u_RefractionMapUnit";
    public static final String ACTIVE_SKYBOX_MAP_SLOT_PARAM_NAME = "u_SkyboxMapUnit";
    public static final String ACTIVE_NORMALMAP_SLOT_PARAM_NAME = "u_NormalMapUnit";
    public static final String ACTIVE_DUDVMAP_SLOT_PARAM_NAME = "u_DUDVMapUnit";
    public static final String ACTIVE_BLENDING_MAP_SLOT_PARAM_NAME = "u_BlendingMapUnit";
    public static final String ACTIVE_SHADOWMAP_SLOT_PARAM_NAME = "uShadowTexture";
    public static final String IS_CUBEMAP_PARAM_NAME = "u_isCubeMap";
    public static final String IS_CUBEMAPF_PARAM_NAME = "u_isCubeMapF";
    public static final String HAS_REFLECT_MAP_PARAM_NAME = "u_hasReflectMap";
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

    public static final String OES_DEPTH_TEXTURE_EXTENSION = "OES_depth_texture";

    public static final String MAIN_RENDERER_VERTEX_SHADER = "shaders/vertex_shader.glsl";
    public static final String MAIN_RENDERER_FRAGMENT_SHADER = "shaders/fragment_shader.glsl";
    public static final String SHADOWMAP_VERTEX_SHADER = "shaders/v_depth_map.glsl";
    public static final String SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT = "shaders/depth_tex_v_depth_map.glsl";
    public static final String SHADOWMAP_FRAGMENT_SHADER = "shaders/f_depth_map.glsl";
    public static final String SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT = "shaders/depth_tex_f_depth_map.glsl";
    public static final String GUI_VERTEX_SHADER = "shaders/gui_vertex.glsl";
    public static final String GUI_FRAGMENT_SHADER = "shaders/gui_fragment.glsl";
    public static final String SKYBOX_VERTEX_SHADER = "shaders/skybox_vertex.glsl";
    public static final String SKYBOX_FRAGMENT_SHADER = "shaders/skybox_fragment.glsl";

    public static final int FBO_TEXTURE_SLOT = 6;

}
