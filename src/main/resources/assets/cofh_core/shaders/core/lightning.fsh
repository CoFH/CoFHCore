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
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.0039) {
        discard;
    }
//    float threshold = 0.85;
//    float adjust = vertexColor.a * threshold * 0.8;
//    if (color.r + color.g + color.b + adjust * 3 < threshold * 3) {
//        discard;
//    }
//    color.rgb = vertexColor.rgb * min(color.rgb + adjust, 1.0);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
