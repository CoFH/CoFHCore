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
flat in ivec2 texCoord1;

out vec4 fragColor;

vec4 mod289(vec4 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec4 permute(vec4 x) {
    return mod289(((x*34.0)+10.0)*x);
}

vec4 taylorInvSqrt(vec4 r) {
    return 1.79284291400159 - 0.85373472095314 * r;
}

vec2 fade(vec2 t) {
    return t*t*t*(t*(t*6.0-15.0)+10.0);
}

// Classic Perlin noise
float cnoise(vec2 P) {
    vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);
    vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);
    Pi = mod289(Pi); // To avoid truncation effects in permutation
    vec4 ix = Pi.xzxz;
    vec4 iy = Pi.yyww;
    vec4 fx = Pf.xzxz;
    vec4 fy = Pf.yyww;

    vec4 i = permute(permute(ix) + iy);

    vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;
    vec4 gy = abs(gx) - 0.5 ;
    vec4 tx = floor(gx + 0.5);
    gx = gx - tx;

    vec2 g00 = vec2(gx.x,gy.x);
    vec2 g10 = vec2(gx.y,gy.y);
    vec2 g01 = vec2(gx.z,gy.z);
    vec2 g11 = vec2(gx.w,gy.w);

    vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));

    float n00 = norm.x * dot(g00, vec2(fx.x, fy.x));
    float n10 = norm.y * dot(g10, vec2(fx.y, fy.y));
    float n01 = norm.z * dot(g01, vec2(fx.z, fy.z));
    float n11 = norm.w * dot(g11, vec2(fx.w, fy.w));

    vec2 fade_xy = fade(Pf.xy);
    vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
    float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
    return 2.3 * n_xy;
}

void main() {
    vec4 color = vertexColor * ColorModulator;
    float radius = float(texCoord1.r * 0.03125);
    float girth = float(texCoord1.g * 0.03125);
    vec2 uv = round((texCoord0 * 2.0 - 1.0) * radius);
    float h = sqrt(uv.x * uv.x + uv.y * uv.y);
    float scale = 1.0 / girth;
    float noise = 0.125 * (clamp(radius * scale, 1.0, 4.0) - 1.0) * (cnoise(uv * 0.3 / sqrt(radius) + 0.5 + girth * 1024.0) + 1.0);
    float a = color.a * cos(clamp(abs(h - radius + girth + 2.0) * scale, -1.5708, 1.5708)) - noise;
    if (a < 0.3) {
        discard;
    }
    fragColor = linear_fog(vec4(color.rgb, a), vertexDistance, FogStart, FogEnd, FogColor);
}
