uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform vec3 u_lightPosition;
uniform vec3 u_camera;

attribute vec3 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Texture;

varying vec3 v_wPosition;
varying vec3 v_Normal;
varying vec2 v_Texture;
varying vec3 lightvector;
varying vec3 lookvector;

void main()
{
    v_wPosition = a_Position;

    vec4 v_Position = u_MV_Matrix * vec4(a_Position, 1.0);
    lightvector = u_lightPosition - v_Position.xyz;
    lookvector = u_camera - v_Position.xyz;

    v_Normal = (u_MV_Matrix * vec4(a_Normal, 0.0)).xyz;

    v_Texture = a_Texture;

    gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
}
