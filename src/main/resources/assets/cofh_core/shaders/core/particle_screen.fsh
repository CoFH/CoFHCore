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
    vec4 texColor = texture(Sampler0, texCoord0);
    float a = texColor.a * vertexColor.a * ColorModulator.a;
    if (a < 0.0039) {
        discard;
    }
    vec4 color = vec4((1.0 - (1.0 - texColor.rgb) * (1.0 - vertexColor.rgb)) * ColorModulator.rgb, a);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
