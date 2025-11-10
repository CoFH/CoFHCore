#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
flat in float length; //in blocks

out vec4 fragColor;

vec4 permute(vec4 x) {
    return mod(((x*34.0)+1.0)*x, 289.0);
}

vec2 fade(vec2 t) {
    return t*t*t*(t*(t*6.0-15.0)+10.0);
}

float cnoise(vec2 P){
    vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);
    vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);
    Pi = mod(Pi, 289.0); // To avoid truncation effects in permutation
    vec4 ix = Pi.xzxz;
    vec4 iy = Pi.yyww;
    vec4 fx = Pf.xzxz;
    vec4 fy = Pf.yyww;
    vec4 i = permute(permute(ix) + iy);
    vec4 gx = 2.0 * fract(i * 0.0243902439) - 1.0; // 1/41 = 0.024...
    vec4 gy = abs(gx) - 0.5;
    vec4 tx = floor(gx + 0.5);
    gx = gx - tx;
    vec2 g00 = vec2(gx.x,gy.x);
    vec2 g10 = vec2(gx.y,gy.y);
    vec2 g01 = vec2(gx.z,gy.z);
    vec2 g11 = vec2(gx.w,gy.w);
    vec4 norm = 1.79284291400159 - 0.85373472095314 *
    vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11));
    g00 *= norm.x;
    g01 *= norm.y;
    g10 *= norm.z;
    g11 *= norm.w;
    float n00 = dot(g00, vec2(fx.x, fy.x));
    float n10 = dot(g10, vec2(fx.y, fy.y));
    float n01 = dot(g01, vec2(fx.z, fy.z));
    float n11 = dot(g11, vec2(fx.w, fy.w));
    vec2 fade_xy = fade(Pf.xy);
    vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
    float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
    return 2.3 * n_xy;
}

float cnoise(float x, float y) {
    return cnoise(vec2(x, y));
}

float saygex(float v, float gamut) {
    return 1.0 - 0.5 * gamut * (v + 1.0);
}

float lump(float freq) { // lump.
    return min(saygex(cos(3.14159 * freq), 1.0), floor(1.3333333 * fract(freq * 0.125)));
}

void main() {
    float time = GameTime * 1200.0; //in seconds
    float x = (floor(texCoord0.x * length * 16.0) + 0.5) * 0.0625;
    float y = round(texCoord0.y * 10.0) * 0.2 - 1.0;

    float shift = y > 0.0 ? -420.0 : 420.0;

    // main shape
    float width = sin(x * 2.0);
    float inv = 1.0 - abs(width);
    width = 0.8 * saygex((1.0 - inv * inv * inv) * sign(width) * cos(time), 0.3);
    width -= 0.5 * (width - 0.25) * (cos(x * 4.0 + 0.5 * cnoise(x * 2.0 + shift, time * 0.4)) + 1.0);

    float freq = x - time * 2.5;
    width += 0.3 * lump(freq); // periodic wave
    width *= 3.6 * min(min(0.25, x), length - x); // tapered ends
    width = min(1.0, width);

    if (abs(y) >= width) {
        discard;
    }
    vec4 color = texture(Sampler0, vec2(x - time, y / width));
    float a = color.a * vertexColor.a;
    if (a < 0.0039) {
        discard;
    }
    vec3 rgb = (1.0 - color.rgb) * (1.0 - vertexColor.rgb);
    if (abs(y) + 0.2 >= width) {
        rgb *= 1.0 - max(width * 0.7 - 0.14, 0.0); // lighter outlines
    }
    rgb *= 1.0 + lump(freq * 2.5) * 0.2; // darker bands
    fragColor = linear_fog(vec4(max(1.0 - rgb, 0.0), a) * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}
