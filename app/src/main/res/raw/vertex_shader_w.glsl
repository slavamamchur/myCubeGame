uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;

attribute vec3 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Texture;

varying vec3 v_PositionWorld;
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_Texture;

uniform int u_RndSeed;

float rand(vec2 co)
{
    float a = 12.9898;
    float b = 78.233;
    float c = 43758.5453;
    float dt= dot(co,vec2(a,b));
    float sn= mod(dt,3.14);

    return fract(sin(sn) * c);
}

void main()
{
    float rnd = (rand(a_Position.xz * u_RndSeed) - 0.5f) * 0.025f;

    v_PositionWorld = vec3(a_Position.x, rnd, a_Position.z);
    v_Position = vec3(u_MV_Matrix * vec4(v_PositionWorld, 1.0));
    v_Normal = vec3(u_MV_Matrix * vec4(a_Normal, 0.0));
    v_Texture = a_Texture;

    gl_Position = u_MVP_Matrix * vec4(v_PositionWorld, 1.0);
}
