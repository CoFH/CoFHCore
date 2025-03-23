#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(Sampler0, texCoord0);

    if (textureColor.a < 0.0039) {
        discard;
    }

    vec4 combinedColor = textureColor + vertexColor;

    fragColor = linear_fog(combinedColor, vertexDistance, FogStart, FogEnd, FogColor);
}