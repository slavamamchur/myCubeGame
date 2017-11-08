precision mediump float;

uniform vec3 u_camera;
uniform vec3 u_lightPosition;
uniform sampler2D u_TextureUnit;
uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;

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

      float ambient = u_AmbientRate; //0.1
      float k_diffuse = u_DiffuseRate;
      float k_specular = u_SpecularRate;

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

     /* if ((textureColor[1] <= textureColor[2]) && (textureColor[0] < textureColor[1]) && (v_PositionWorld[1] <= 0.0)) {
         float deepLightFactor = 1.0 + v_PositionWorld[1] / 0.333;
         vec4 deepLightColor = vec4(deepLightFactor * vec3(1.0, 1.0, 1.0), 1.0);

         textureColor = deepLightColor * vec4(1.0, 1.0, 0.8, 1.0);
      }*/

      textureColor[3] = 1.0;
      gl_FragColor = lightColor * textureColor;// * fog_factor + fogColor * (1.0 - fog_factor);
}
