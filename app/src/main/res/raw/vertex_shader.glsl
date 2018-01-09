uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform vec3 u_lightPosition;
uniform vec3 u_camera;

attribute vec3 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Texture;

//varying vec3 v_wPosition;
varying vec3 v_Normal;
varying vec2 v_Texture;
varying vec2 v_TiledTexture;
varying vec3 lightvector;
varying vec3 lookvector;
varying float visibility;

const float fog_density = 0.20;
const float fog_gradient = 10.0;
const float tiling = 10.0;

void main()
{
    //gl_ClipDistance[0] = -1;
    //v_wPosition = a_Position;
    vec3 v_Position = vec4(u_MV_Matrix * vec4(a_Position, 1.0)).xyz;

    lightvector = u_lightPosition - v_Position;
    lookvector = u_camera - v_Position;

    v_Normal = (u_MV_Matrix * vec4(a_Normal, 0.0)).xyz;

    v_Texture = a_Texture;
    v_TiledTexture = v_Texture * tiling;

    float fog_distance = length(v_Position);//length(u_camera - a_Position); //clamp(-v_Position.z, 0.0, 999.0);
    visibility = exp(-pow(fog_distance * fog_density, fog_gradient));
    visibility = clamp(visibility, 0.0, 1.0);

    gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
}
