precision mediump float;

uniform mat4 u_MV_MatrixF;

uniform sampler2D u_TextureUnit;
uniform /*samplerCube*/ sampler2D u_CubeMapUnit;
uniform sampler2D u_NormalMapUnit;
uniform sampler2D u_DUDVMapUnit;
uniform sampler2D uShadowTexture;

uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform float u_RndSeed;
uniform vec3 u_lightPositionF;
uniform vec3 u_lightColour;
uniform int u_isCubeMapF;
///uniform float uxPixelOffset;
///uniform float uyPixelOffset;

varying vec3 v_wPosition;
varying vec3 v_Normal;
varying vec2 v_Texture;
varying vec3 lightvector;
varying vec3 lookvector;
///varying float visibility;
varying vec4 vShadowCoord;

const vec4 skyColour = vec4(0.0, 0.7, 1.0, 1.0);
const float shineDumper = 40.0;
const float nmapTiling = 6.0;
const float waveStrength = 0.02;

void main()
{
      vec2 uv = v_Texture;
      vec2 totalDistortion;
      if (u_RndSeed > -1.0 && v_wPosition.y == 0.0) {
          vec2 tc = uv * nmapTiling;
          uv = texture2D(u_DUDVMapUnit, vec2(tc.x + u_RndSeed, tc.y)).rg * 0.1;
          uv = tc + vec2(uv.x, uv.y + u_RndSeed);
          totalDistortion = (texture2D(u_DUDVMapUnit, uv).rg * 2.0 - 1.0) * waveStrength;
      }

      vec3 n_normal;
      if (v_wPosition.y == 0.0 && u_isCubeMapF == 1) {
        vec4 normalMapColour = texture2D(u_NormalMapUnit, uv);
        n_normal = (u_MV_MatrixF * vec4(normalMapColour.r * 2.0 - 1.0, normalMapColour.b, normalMapColour.g * 2.0 - 1.0, 0.0)).xyz;
        n_normal = normalize(n_normal);
      }
      else {
        n_normal = normalize(v_Normal);
      }

      vec3 n_lightvector = normalize(lightvector);
      vec3 n_lookvector = normalize(lookvector);

      //Simple shadow mapping
      float shadow = 1.0;
      //if the fragment is not behind light view frustum
      if (vShadowCoord.w > 0.0) {
        //Calculate variable bias
        float bias; // = 0.0005;
        vec3 l = normalize(u_lightPositionF);
        float cosTheta = clamp(dot(n_normal,l), 0.0, 1.0);
        bias = 0.0001 * tan(acos(cosTheta));
        bias = clamp(bias, 0.0, 0.01);

        vec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;
        float distanceFromLight = texture2D(uShadowTexture, shadowMapPosition.st).z;
        //1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map) 0.0 = in shadow
        shadow = float(distanceFromLight > shadowMapPosition.z - bias);
        //scale 0.0-1.0 to 0.2-1.0 otherways everything in shadow would be black
        shadow = (shadow * (1.0 - u_AmbientRate)) + u_AmbientRate;
      }

      float ambient = u_AmbientRate;
      float k_diffuse = u_DiffuseRate;
      float k_specular = u_SpecularRate;
      if (u_isCubeMapF == 1 && v_wPosition.y > 0.0) {
        k_specular *= 0.5;
      }

      float diffuse = k_diffuse;
      if (gl_FrontFacing) {
              diffuse *= max(dot(n_normal, n_lightvector), 0.0);
          }
      else {
          	diffuse *= max(dot(-n_normal, n_lightvector), 0.0);
          }
      // Add attenuation.
      ///float distance = length(u_lightPosition - v_Position);
      ///diffuse = diffuse * (1.0 / (1.0 + (0.05 * distance)));

      float lightFactor = ambient + diffuse;
      vec3 diffuseColor = lightFactor * u_lightColour * shadow;

      vec3 reflectvector = reflect(-n_lightvector, n_normal);
      float specular = k_specular * pow(max(dot(reflectvector, n_lookvector), 0.0), shineDumper);
      vec3 specularColor = specular * u_lightColour;

      vec4 textureColor;
      if (v_wPosition.y == 0.0 && u_isCubeMapF == 1) {
        diffuseColor = vec3(0.8) * shadow;//(0.4 + lightFactor) * vec3(1.0);//vec3(0.8) * shadow;

        ///textureColor = vec4(0, 0.3, 0.5, 1.0);

        textureColor = texture2D(u_CubeMapUnit, clamp(v_Texture + totalDistortion, 0.0, 0.9999));
        float reflectiveFactor = 0.4;//dot(n_lookvector, vec3(0.0, 1.0, 0.0)); //0.4
        textureColor = mix(textureColor, vec4(0, 0.3, 0.5, 1.0), reflectiveFactor);

        /*vec3 texcoordCube = reflect(-n_lookvector, n_normal);
        float reflectiveFactor = dot(n_lookvector, vec3(0.0, 1.0, 0.0)) *//* * 0.5 *//*;
        textureColor = mix(textureCube(u_CubeMapUnit, texcoordCube), vec4(0, 0.3, 0.5, 1.0), reflectiveFactor);*/
      }
      else {
        textureColor = texture2D(u_TextureUnit/*uShadowTexture*/, v_Texture);
      }


      gl_FragColor = vec4(diffuseColor, 1.0) * textureColor + vec4(specularColor, 1.0);
      ///gl_FragColor.rgb = (gl_FragColor.rgb - 0.5) * 1.2 + 0.5;//contrast effect
      ///gl_FragColor = mix(skyColour, gl_FragColor, visibility);//fog

}
