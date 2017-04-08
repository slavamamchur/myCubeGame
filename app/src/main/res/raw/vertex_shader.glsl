uniform mat4 u_Matrix;

attribute vec3 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Texture;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_Texture;

void main()
{
    v_Position = a_Position;
    v_Normal = normalize(a_Normal);
    v_Texture = a_Texture;

    gl_Position = u_Matrix * vec4(a_Position, 1.0);
}
