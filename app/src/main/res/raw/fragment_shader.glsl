precision mediump float;

//uniform vec3 u_camera;
//uniform vec3 u_lightPosition;
uniform mat4 u_MV_Matrix;
uniform sampler2D u_TextureUnit;
uniform samplerCube u_CubeMapUnit;
uniform int u_isCubeMap;
uniform int u_isNormalMap;
uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform float u_RndSeed;

varying vec3 v_wPosition;
varying vec3 v_Normal;
varying vec2 v_Texture;
varying vec2 v_TiledTexture;
varying vec3 lightvector;
varying vec3 lookvector;

void main()
{
      vec2 uv = v_Texture.xy;
      if (u_RndSeed > -1) {
              vec2 tc = v_TiledTexture.xy;
              vec2 p = -1.0 + 2.0 * tc;
              float len = length (p);

              uv = tc + (p / len ) * cos(len * 12.0 - u_RndSeed * 4.0) * 0.03;
      }

      vec3 n_normal;
      if (u_isNormalMap == 0) {
        n_normal = normalize(v_Normal);
      }
      else {
        vec4 normalMapColour = texture2D(u_TextureUnit, uv);
        n_normal = (u_MV_Matrix * vec4(normalMapColour.r * 2.0 - 1.0, normalMapColour.b, normalMapColour.g * 2.0 - 1.0, 0.0)).xyz;
        n_normal = normalize(n_normal);
      }

      vec3 n_lightvector = normalize(lightvector);
      vec3 n_lookvector = normalize(lookvector);

      //float distance = length(u_lightPosition - v_Position);

      float ambient = u_AmbientRate;
      float k_diffuse = u_DiffuseRate;
      float k_specular = u_SpecularRate;

      float diffuse = k_diffuse;
      if (gl_FrontFacing) {
              diffuse *= max(dot(n_normal, n_lightvector), 0.0);
          }
      else {
          	diffuse *= max(dot(-n_normal, n_lightvector), 0.0);
          }
      // Add attenuation.
      //diffuse = diffuse * (1.0 / (1.0 + (0.05 * distance)));

      float lightFactor = ambient + diffuse;
      vec3 diffuseColor = lightFactor * vec3(1.0, 1.0, 0.667);

      vec3 reflectvector = reflect(-n_lightvector, n_normal);
      float specular = k_specular * pow(max(dot(reflectvector, n_lookvector), 0.0), 40.0);
      vec3 specularColor = specular * vec3(1.0, 1.0, 0.667);

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

      vec4 textureColor;
      if (u_isCubeMap == 1) {
        //float ratio = 1.00 / 1.52;
        vec3 texcoordCube = reflect(-n_lookvector, n_normal);
        textureColor = mix(textureCube(u_CubeMapUnit, texcoordCube), vec4(0, 0.4, 1.0, 1.0), 0.5);
      }
      else {
        textureColor = texture2D(u_TextureUnit, uv);
      }

      if (u_isNormalMap == 0) {
        gl_FragColor = vec4(diffuseColor, 1.0) * textureColor + vec4(specularColor, 1.0);// * fog_factor + fogColor * (1.0 - fog_factor);
      }
      else {
        gl_FragColor = vec4(0.8, 0.8, 0.8, 1.0) * textureColor + vec4(specularColor, 1.0);// * fog_factor + fogColor * (1.0 - fog_factor);
      }
}
