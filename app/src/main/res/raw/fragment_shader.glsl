precision mediump float;

uniform vec3 u_camera;
uniform vec3 u_lightPosition;
uniform sampler2D u_TextureUnit;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_Texture;

void main()
{
      vec3 n_normal = normalize(v_Normal);
      vec3 lightvector = normalize(u_lightPosition - v_Position);
      vec3 lookvector = normalize(u_camera - v_Position);
      float distance = length(u_lightPosition - v_Position);

      float ambient = 0.5;
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

      vec4 lightColor = vec4((ambient + diffuse + specular) * vec3(0.6, 0.8, 1.0), 1.0);
      gl_FragColor = lightColor * texture2D(u_TextureUnit, v_Texture);
}
