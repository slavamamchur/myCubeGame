#version 130
attribute vec3 a_Position;

uniform mat4 u_MVP_Matrix;

varying vec3 v_Texture;

void main()
{
	v_Texture = a_Position;
	gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
}