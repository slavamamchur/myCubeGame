uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform mat4 uShadowProjMatrix;
uniform vec3 u_lightPosition;
uniform vec3 u_camera;
uniform int u_isCubeMap;

attribute vec3 a_Normal;
attribute vec3 a_Position;
attribute vec2 a_Texture;

varying vec3 v_wPosition;
varying vec3 v_Normal;
varying vec2 v_Texture;
varying vec3 lightvector;
varying vec3 lookvector;
///varying float visibility;
varying vec4 vShadowCoord;

const float fog_density = 0.20;
const float fog_gradient = 10.0;

void main()
{
    v_Normal = (u_MV_Matrix * vec4(a_Normal, 0.0)).xyz;

    vec3 tmp_pos = a_Position;
    if(tmp_pos.y <= 0.0 && u_isCubeMap == 1) {
        tmp_pos.y = 0.0;
        v_Normal = (u_MV_Matrix * vec4(0.0, 1.0, 0.0, 0.0)).xyz;
    }
    v_wPosition = tmp_pos;

    vec3 v_Position = vec4(u_MV_Matrix * vec4(tmp_pos, 1.0)).xyz;

    lightvector = u_lightPosition - v_Position;
    lookvector = u_camera - v_Position;

    v_Texture = a_Texture;

    ///float fog_distance = length(v_Position);
    ///visibility = exp(-pow(fog_distance * fog_density, fog_gradient));
    ///visibility = clamp(visibility, 0.0, 1.0);

    vShadowCoord = uShadowProjMatrix * vec4(tmp_pos, 1.0);

    gl_Position = u_MVP_Matrix * vec4(tmp_pos, 1.0);
}
