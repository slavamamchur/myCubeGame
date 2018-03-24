precision highp float;

varying vec4 vPosition;

void main() {
	float normalizedDistance  = vPosition.z / vPosition.w;
	// scale -1.0;1.0 to 0.0;1.0
	normalizedDistance = (normalizedDistance + 1.0) / 2.0;

	const vec4 bitSh = vec4(256.0 * 256.0 * 256.0,
    							256.0 * 256.0,
    							256.0,
    							1.0);
    const vec4 bitMsk = vec4(0.0,
    							 1.0 / 256.0,
    							 1.0 / 256.0,
    							 1.0 / 256.0);
    vec4 comp = fract(normalizedDistance * bitSh);
    comp -= comp.xxyz * bitMsk;

    gl_FragColor = vec4(1.0, 0.0, 0.0, 0.5);//comp;

}