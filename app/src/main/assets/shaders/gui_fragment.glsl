//#version 130
precision mediump float;

uniform sampler2D u_TextureUnit;

varying vec2 v_Texture;

const float contrast = 0.3;

void main()
{
      vec4 colour = vec4(texture2D(u_TextureUnit, v_Texture).rgb, 1.0); //base colour
      ///float brightness = (colour.r * 0.2126) + (colour.g * 0.7152) + (colour.b * 0.0722);
      ///colour.rgb *= brightness; // brightness map
      ///colour.rgb = vec3(brightness); //grayscale effect
      ///colour.rgb = (colour.rgb - 0.5) * (1.0 + contrast) + 0.5; //contrast effect

      gl_FragColor = colour;
}
