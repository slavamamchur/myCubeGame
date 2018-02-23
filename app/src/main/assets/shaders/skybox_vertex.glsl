attribute vec3 a_Position;

uniform mat4 u_MVP_Matrix;

varying vec4 vPosition;

void main()
{
	vPosition = u_MVP_Matrix * vec4(a_Position, 1.0);

	gl_Position = vPosition;
}