// Vertex shader to generate the Depth Map
// Used for shadow mapping - generates depth map from the light's viewpoint
precision highp float;

// model-view projection matrix
uniform mat4 u_MVP_Matrix;

// position of the vertices
attribute vec3 a_Position;

varying vec4 vPosition;

void main() {
	vPosition = u_MVP_Matrix * vec4(a_Position, 1.0);
	gl_Position = vPosition;
}