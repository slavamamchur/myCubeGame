uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;

attribute vec3 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Texture;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_Texture;

void main()
{
    v_Position = vec3(u_MV_Matrix * vec4(a_Position, 1.0));
    v_Normal = vec3(u_MV_Matrix * vec4(a_Normal, 0.0));
    v_Texture = a_Texture;

    gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
}
