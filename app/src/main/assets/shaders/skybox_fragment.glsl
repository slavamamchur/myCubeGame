precision mediump float;

uniform samplerCube u_TextureUnit;

varying vec3 v_Texture;

void main()
{
      gl_FragColor = textureCube(u_TextureUnit, v_Texture);
}
