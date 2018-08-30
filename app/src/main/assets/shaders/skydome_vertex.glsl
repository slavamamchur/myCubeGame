attribute vec3 a_Position;

uniform mat4 u_MVP_Matrix;

varying vec3 v_Normal;

void main()
{
	v_Normal.xyz = a_Position * -1;
	gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);

	/*
	u = atan2(y, z)
    v = acos(x/sqrt(x*x + y*y + z*z))
    u /= 2*Pi and v /= Pi

    vec2((atan(texCoords.y, texCoords.x) / 3.1415926 + 1.0) * 0.5,
    (asin(texCoords.z) / 3.1415926 + 0.5));

    vec2(vNormal2.x, vNormal2.y))
    */
}