precision highp float;

varying vec4 vPosition;

void main() {
	float normalizedDistance  = vPosition.z / vPosition.w;
	// scale -1.0;1.0 to 0.0;1.0
	//normalizedDistance = (normalizedDistance + 1.0) / 2.0;

    float value = 15.0 - normalizedDistance;
    float whole_part = floor(value);
    float frac_part = value - whole_part;
    whole_part = whole_part * 0.1;

    gl_FragColor = vec4(whole_part, frac_part, 0.0, 1.0);
}