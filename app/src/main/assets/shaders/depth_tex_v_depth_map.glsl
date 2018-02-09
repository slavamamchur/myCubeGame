precision highp float;

// model-view projection matrix
uniform mat4 u_MVP_Matrix;

// position of the vertices
attribute vec3 a_Position;

void main() {
	gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
	//gl_Position.z *= gl_Position.w;
}