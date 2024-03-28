#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MainDepthSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform float Resolution;

out vec4 fragColor;

void main() {

    float bound = round(InSize.y / Resolution);
    vec2 center = texCoord / oneTexel;
    float closest = 1.0 - texture(MainDepthSampler, texCoord).r;
    for (float i = 0; i < bound; i += 1.0) {
        vec4 col = texture(DiffuseSampler, vec2(center.x, center.y + i) * oneTexel);
        if (col.a > closest) {
            if (i <= ceil(min(col.a * 56.0, 1.0) * bound)) {
                closest = col.a;
                fragColor = vec4(col.rgb, 1.0);
            }
        }
    }
//    vec2 pixel = InSize.y * oneTexel * 0.0078125;
//    vec4 col = texture(DiffuseSampler, round(texCoord / pixel) * pixel);
//    if (col.r > 0.0) {
//        fragColor = col;
//    }
}
