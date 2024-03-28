#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform float Resolution;

out vec4 fragColor;

void main() {

    float bound = round(InSize.y / Resolution);
    vec2 center = texCoord / oneTexel;
    float closest = 1024.0;
    for (float i = 0; i < bound; i += 1.0) {
        vec2 pos = vec2(center.x + i, center.y);
        vec2 coord = pos * oneTexel;
        float depth = texture(DiffuseDepthSampler, coord).r;
        if (depth < closest) {
            float size = ceil(min((1.0 - depth) * 56.0, 1.0) * bound);
            if (i <= size) {
                size = ceil(size * 0.75); //max(size - 1.0, 1.0);
                if (mod(pos.x, size) < 1.0 && mod(pos.y, size) < 1.0) {
                    vec4 col = texture(DiffuseSampler, coord);
                    if (col.a > 0.0) {
                        closest = depth;
                        fragColor = vec4(col.rgb, 1.0 - depth);
                    }
                }
            }
        }
    }
}
