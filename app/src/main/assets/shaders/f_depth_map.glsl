precision highp float;

varying highp vec4 vPosition;

highp vec4 pack (float depth)
{
	const highp vec4 bitSh = vec4(256.0 * 256.0 * 256.0,
							256.0 * 256.0,
							256.0,
							1.0);
	const highp vec4 bitMsk = vec4(0,
							 1.0 / 256.0,
							 1.0 / 256.0,
							 1.0 / 256.0);

	highp vec4 comp = vec4(fract(bitSh * depth));
	comp -= comp.xxyz * bitMsk;

	return comp;
}

void main() {
	float normalizedDistance  = vPosition.z / vPosition.w;
	normalizedDistance = normalizedDistance * 0.5 + 0.5;// scale -1.0;1.0 to 0.0;1.0
	//normalizedDistance -= 0.5; //TODO: use with rgb buffer

    /*float v1 = normalizedDistance * 255.0;
    float f1 = fract(v1);
    float vn1 = floor(v1) / 255.0;

    float v2 = f1 * 255.0;
    float f2 = fract(v2);
    float vn2 = floor(v2) / 255.0;

    float v3 = f2 * 255.0;
    float f3 = fract(v3);
    float vn3 = floor(v3) / 255.0;

    highp vec4 result = vec4(vn1, vn2, vn3, f3);*/

    highp vec4 result = pack(normalizedDistance);

    gl_FragColor = result;
}