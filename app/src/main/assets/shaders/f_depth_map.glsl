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
	//normalizedDistance -= 0.5; //TODO: use with rgb buffer + 0.5 ???

    highp vec4 result = pack(normalizedDistance);
    gl_FragColor = result;
}