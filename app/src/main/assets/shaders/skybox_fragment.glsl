precision mediump float;

uniform samplerCube u_TextureUnit;

varying vec4 vPosition;

void main()
{
      vec4 colour = vec4(texture2D(u_TextureUnit, v_Texture).rgb, 1.0);

      gl_FragColor = colour;
}
