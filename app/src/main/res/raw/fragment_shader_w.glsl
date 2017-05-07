precision mediump float;

uniform vec3 u_camera;
uniform vec3 u_lightPosition;
uniform sampler2D u_TextureUnit;

varying vec3 v_PositionWorld;
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_Texture;

void main()
{
      vec3 n_normal = normalize(v_Normal);
      vec3 lightvector = normalize(u_lightPosition - v_Position);
      vec3 lookvector = normalize(u_camera - v_Position);
      float distance = length(u_lightPosition - v_Position);

      float ambient = 0.2;//0.1
      float k_diffuse = 1.0;
      float k_specular = 1.0;

      float diffuse = k_diffuse;
      if (gl_FrontFacing) {
              diffuse *= max(dot(n_normal, lightvector), 0.0);
          }
      else {
          	diffuse *= max(dot(-n_normal, lightvector), 0.0);
          }
      // Add attenuation.
      diffuse = diffuse * (1.0 / (1.0 + (0.05 * distance)));

      vec3 reflectvector = reflect(-lightvector, n_normal);
      float specular = k_specular * pow(max(dot(lookvector, reflectvector), 0.0), 40.0);

      float lightFactor = ambient + diffuse + specular;
      vec4 lightColor = vec4(lightFactor * vec3(1.0, 1.0, 1.0), 1.0);

      /*//Add linear fog
      float u_fogMaxDist = 3.5;
      float u_fogMinDist = 1.0;
      float fog_distance = length(u_camera - v_Position);
      float fog_factor;
      // Compute linear fog equation
      fog_factor = (u_fogMaxDist - fog_distance) / (u_fogMaxDist - u_fogMinDist);
      // Clamp in the [0,1] range
      fog_factor = clamp(fog_factor, 0.0, 1.0);
      vec4 fogColor = vec4(fog_factor * vec3(1.0, 1.0, 1.0), 1.0);*/

      vec4 textureColor = texture2D(u_TextureUnit, v_Texture);

      if ((textureColor[1] <= textureColor[2]) && (textureColor[0] < textureColor[1]) && (v_PositionWorld[1] <= 0.0)) {
         textureColor = lightColor * textureColor;
         textureColor[3] = 0.9;
      }
      else {
         textureColor = vec4(0, 0, 0, 0);
      }

      gl_FragColor = textureColor;// * fog_factor + fogColor * (1.0 - fog_factor);
}
