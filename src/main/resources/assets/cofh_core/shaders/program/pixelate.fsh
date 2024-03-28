#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

out vec4 fragColor;

void main() {

    vec2 pixel = InSize.y * oneTexel * 0.0078125;
    vec4 col = texture(DiffuseSampler, round(texCoord / pixel) * pixel);
    if (col.r > 0.0) {
        fragColor = col;
    }
}
