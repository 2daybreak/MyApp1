#version 330

in  vec4 exColour;
in  vec2 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform int hasTexture;

void main()
{
	if(hasTexture == 1) {
	    fragColor = texture(texture_sampler, outTexCoord);
	    fragColor.w = 0.25f;
	}
	if(hasTexture == 0) {
	    //fragColor = vec4(exColour);
	}
}