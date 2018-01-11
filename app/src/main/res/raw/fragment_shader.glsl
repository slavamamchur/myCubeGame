precision mediump float;

//uniform vec3 u_camera;
//uniform vec3 u_lightPosition;
uniform mat4 u_MV_Matrix;
uniform sampler2D u_TextureUnit;
uniform /*samplerCube*/ sampler2D u_CubeMapUnit;
uniform sampler2D u_NormalMapUnit;
uniform sampler2D u_DUDVMapUnit;
uniform int u_isCubeMap;
uniform int u_isNormalMap;
uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform float u_RndSeed;
uniform vec3 u_lightColour;

varying vec3 v_wPosition;
varying vec3 v_Normal;
varying vec2 v_Texture;
varying vec3 lightvector;
varying vec3 lookvector;
varying float visibility;

const vec4 skyColour = vec4(0.0, 0.7, 1.0, 1.0);
const float shineDumper = 40.0;
const float nmapTiling = 6.0;
const float waveStrength = 0.02;

void main()
{
      vec2 uv = v_Texture;
      vec2 totalDistortion;
      if (u_RndSeed > -1 && v_wPosition.y == 0) {
              vec2 tc = uv * nmapTiling;
              uv = texture2D(u_DUDVMapUnit, vec2(tc.x + u_RndSeed, tc.y)).rg * 0.1;
              uv = tc + vec2(uv.x, uv.y + u_RndSeed);
              totalDistortion = (texture2D(u_DUDVMapUnit, uv).rg * 2.0 - 1.0) * waveStrength;
      }

      vec3 n_normal;
      if ((u_isNormalMap == 1)  && v_wPosition.y == 0) {
        vec4 normalMapColour = texture2D(u_NormalMapUnit, uv);
        n_normal = (u_MV_Matrix * vec4(normalMapColour.r * 2.0 - 1.0, normalMapColour.b, normalMapColour.g * 2.0 - 1.0, 0.0)).xyz;
        n_normal = normalize(n_normal);
      }
      else {
        n_normal = normalize(v_Normal);
      }

      vec3 n_lightvector = normalize(lightvector);
      vec3 n_lookvector = normalize(lookvector);

      //float distance = length(lightvector);

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
      vec3 diffuseColor = lightFactor * u_lightColour;

      vec3 reflectvector = reflect(-n_lightvector, n_normal);
      float specular = k_specular * pow(max(dot(reflectvector, n_lookvector), 0.0), shineDumper);
      vec3 specularColor = specular * u_lightColour;

      vec4 textureColor;
      if (u_isCubeMap == 1 && v_wPosition.y == 0) {
        textureColor = texture2D(u_CubeMapUnit, clamp(v_Texture + totalDistortion, 0.0, 0.9999));
        float reflectiveFactor = 0.4;//dot(n_lookvector, vec3(0.0, 1.0, 0.0)); //0.4
        textureColor = mix(textureColor, vec4(0, 0.3, 0.5, 1.0), reflectiveFactor);

        /*vec3 texcoordCube = reflect(-n_lookvector, n_normal);
        float reflectiveFactor = dot(n_lookvector, vec3(0.0, 1.0, 0.0)) *//* * 0.5 *//*;
        textureColor = mix(textureCube(u_CubeMapUnit, texcoordCube), vec4(0, 0.3, 0.5, 1.0), reflectiveFactor);*/
      }
      else {
        textureColor = texture2D(u_TextureUnit, v_Texture);
      }

      if (u_isNormalMap == 0 || v_wPosition.y > 0) {
        gl_FragColor = vec4(diffuseColor, 1.0) * textureColor + vec4(specularColor, 1.0);
      }
      else {
        gl_FragColor = vec4(0.8, 0.8, 0.8, 1.0) * textureColor + vec4(specularColor, 1.0);
      }

      gl_FragColor = mix(skyColour, gl_FragColor, visibility);
}
