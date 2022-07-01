#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;
in vec2 texCoord2;
in vec4 normal;

out vec4 fragColor;

void main() {
    float res = 8.0;
    fragColor = texture(Sampler0, floor(texCoord0 * res) / res) * vertexColor * ColorModulator;
}