#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D Pixel2Sampler;
uniform sampler2D Pixel2DepthSampler;
uniform sampler2D Pixel4Sampler;
uniform sampler2D Pixel4DepthSampler;
uniform sampler2D Pixel8Sampler;
uniform sampler2D Pixel8DepthSampler;
uniform sampler2D Pixel16Sampler;
uniform sampler2D Pixel16DepthSampler;
uniform sampler2D MainDepthSampler;

out vec4 fragColor;

ivec2 pos;
float closest;

void test(sampler2D color, sampler2D depth, int level) {

    ivec2 p = pos >> level;
    float d = texelFetch(depth, p, 0).r;
    if (d < closest) {
        vec4 col = texelFetch(color, p, 0);
        if (col.a > 0.0) {
            closest = d;
            fragColor = vec4(col.rgb, 1.0);
        }
    }
}

void main() {

    pos = ivec2(gl_FragCoord.xy);
    closest = texelFetch(MainDepthSampler, pos, 0).r;
    fragColor = vec4(0.0);
    test(DiffuseSampler, DiffuseDepthSampler, 0);
    test(Pixel2Sampler, Pixel2DepthSampler, 1);
    test(Pixel4Sampler, Pixel4DepthSampler, 2);
    test(Pixel8Sampler, Pixel8DepthSampler, 3);
    test(Pixel16Sampler, Pixel16DepthSampler, 4);
}
