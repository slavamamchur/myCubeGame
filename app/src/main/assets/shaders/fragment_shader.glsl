precision mediump float;

uniform mat4 u_MV_MatrixF;

uniform sampler2D u_TextureUnit;
uniform sampler2D u_RefractionMapUnit;
uniform samplerCube u_SkyboxMapUnit;
uniform sampler2D u_NormalMapUnit;
uniform sampler2D u_DUDVMapUnit;
uniform sampler2D u_BlendingMapUnit;
uniform sampler2D uShadowTexture;

uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform float u_RndSeed;
uniform vec3 u_lightPositionF;
uniform vec3 u_lightColour;
uniform int u_isCubeMapF;
uniform int u_hasReflectMap;
///uniform float uxPixelOffset;
///uniform float uyPixelOffset; //TODO: -> for RGB depth map

varying vec3 v_wPosition;
varying vec2 v_Texture;
varying vec3 lightvector;
varying vec3 lookvector;
varying float visibility;
varying vec4 vShadowCoord;

varying float vdiffuse;
varying float vspecular;

const vec4 skyColour = vec4(0.48, 0.62, 0.68, 1.0);
const vec4 waterColour = vec4(0, 0.3, 0.5, 1.0);
const float shineDumper = 40.0;
const float nmapTiling = 6.0;
const float waveStrength = 0.02;

float calcDynamicBias(float bias, vec3 normal) {
    highp float result;
    highp vec3 l = normalize(u_lightPositionF);
    highp float cosTheta = clamp(dot(normal,l), 0.0, 1.0);
    result = bias * tan(acos(cosTheta));
    result = clamp(result, 0.0, 0.01);

    return result;
}

float calcShadowRate() {
      highp float shadow = 1.0;
      if (vShadowCoord.w > 0.0) {
        highp float bias = 0.0001; //calcDynamicBias(0.0005, n_normal); //TODO: -> use for ultra graphics settings
        vec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;
        highp float distanceFromLight = texture2D(uShadowTexture, shadowMapPosition.st).z;
        shadow = float(distanceFromLight > (shadowMapPosition.z - bias));
        shadow = (shadow * (1.0 - u_AmbientRate)) + u_AmbientRate;
      }

      return shadow;
}

vec4 calcLightColor(vec3 nNormal, vec3 nLightvector, float shadowRate) {
      float lightFactor = u_DiffuseRate;

      if (v_wPosition.y == 0.0 && u_isCubeMapF == 1 &&  u_RndSeed > -1.0) {
            lightFactor = 0.8 - u_AmbientRate;
      }
      else if ((v_wPosition.y > 0.0 && u_isCubeMapF == 1) || (u_isCubeMapF == 0) || (u_RndSeed == -1.0)) {
            lightFactor *= vdiffuse;
      }
      else {
            lightFactor *= max(dot(nNormal, nLightvector), 0.0);
      }

      vec3 lightColour = u_lightColour * (u_AmbientRate + lightFactor);

      return vec4(lightColour * shadowRate, 1.0);
}

vec4 calcSpecularColor(vec3 nNormal, vec3 nLightvector, vec3 n_lookvector, float shadowRate) {
      float specular = u_SpecularRate;

      if ((v_wPosition.y > 0.0 && u_isCubeMapF == 1) || (u_isCubeMapF == 0) || (u_RndSeed == -1.0)) {
            specular *= vspecular;
      }
      else {
            vec3 reflectvector = reflect(-nLightvector, nNormal);
            specular *= pow(max(dot(reflectvector, n_lookvector), 0.0), shineDumper);
      }

      if (shadowRate < 1.0) {
            specular = 0.0;
      }
      else if (u_isCubeMapF == 1 && v_wPosition.y > 0.0) {
            specular *= 0.25;
      }

      return vec4(u_lightColour * specular, 1.0);
}

vec4 calcPhongLightingMolel(vec3 n_normal, vec3 n_lightvector, vec3 n_lookvector, vec4 diffuseColor) {
      highp float shadowRate = calcShadowRate();
      vec4 lightColor = calcLightColor(n_normal, n_lightvector, shadowRate);
      vec4 specularColor = calcSpecularColor(n_normal, n_lightvector, n_lookvector, shadowRate);

      return lightColor * diffuseColor + specularColor;
}

void main()
{
      vec3 n_lightvector = normalize(lightvector);
      vec3 n_lookvector = normalize(lookvector);

      vec3 n_normal;
      vec2 uv = v_Texture;
      vec2 totalDistortion;
      vec4 diffuseColor;
      if (u_RndSeed > -1.0 && v_wPosition.y == 0.0 && u_isCubeMapF == 1) {
          vec2 tc = uv * nmapTiling;
          uv = texture2D(u_DUDVMapUnit, vec2(tc.x + u_RndSeed, tc.y)).rg * 0.1;
          uv = tc + vec2(uv.x, uv.y + u_RndSeed);
          totalDistortion = (texture2D(u_DUDVMapUnit, uv).rg * 2.0 - 1.0) * waveStrength;

          vec4 normalMapColour = texture2D(u_NormalMapUnit, uv);
          n_normal = (u_MV_MatrixF * vec4(normalMapColour.r * 2.0 - 1.0, normalMapColour.b, normalMapColour.g * 2.0 - 1.0, 0.0)).xyz;
          n_normal = normalize(n_normal);
          if (!gl_FrontFacing) {
                n_normal = -n_normal;
          }

          float reflectiveFactor = 1.0 - dot(n_lookvector, vec3(0.0, 1.0, 0.0));
          vec4 refractionColor = texture2D(u_RefractionMapUnit, clamp(v_Texture + totalDistortion, 0.0, 0.9999));

          if (u_hasReflectMap == 1) {
            //TODO: transform reflected vector via skybox transform to apply rotated sky reflection.
            vec4 reflectionColor = mix(textureCube(u_SkyboxMapUnit, reflect(-n_lookvector, n_normal)), waterColour, 0.5);
            diffuseColor = mix(refractionColor, reflectionColor, reflectiveFactor);
          }
          else {
            diffuseColor = mix(refractionColor, waterColour, reflectiveFactor);
          }
      }
      else {
           diffuseColor = texture2D(u_TextureUnit, v_Texture);
      }

      gl_FragColor = calcPhongLightingMolel(n_normal, n_lightvector, n_lookvector, diffuseColor);

      if (u_isCubeMapF == 1) {
        float blendingFactor = texture2D(u_BlendingMapUnit, v_Texture).r;
        gl_FragColor = mix(gl_FragColor, skyColour, blendingFactor);
      }

      gl_FragColor = mix(skyColour, gl_FragColor, visibility);//fog

}
