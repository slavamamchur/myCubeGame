#version 130
precision mediump float;

uniform sampler2D u_TextureUnit;

varying vec3 v_Normal;

const vec4 skyColour = vec4(0.68, 0.74, 0.78, 1.0);//vec4(0.0, 0.3, 0.5, 1.0);
const float lowerLimit = 0.0;
const float upperLimit = 1.0;
const float PI  = 3.141592653589793;

vec2 RadialCoords(vec3 a_coords)
{
	vec3 a_coords_n = normalize(a_coords);
	float lon = atan(a_coords_n.z, a_coords_n.x);
	float lat = acos(a_coords_n.y);
	vec2 sphereCoords = vec2(lon, lat) * (1.0 / PI);

	return vec2(sphereCoords.x * 0.5 + 0.5, 1.0 - sphereCoords.y);
}

void main()
{
      vec2 v_Texture = RadialCoords(v_Normal);
      vec4 textureColour = texture2D(u_TextureUnit, v_Texture);

      float blendFactor = clamp((v_Texture.y - lowerLimit) / (upperLimit - lowerLimit), 0.0, 1.0);

      gl_FragColor = mix(skyColour, textureColour, blendFactor);
}
