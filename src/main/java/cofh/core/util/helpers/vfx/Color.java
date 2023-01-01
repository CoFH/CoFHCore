package cofh.core.util.helpers.vfx;

import cofh.lib.util.helpers.MathHelper;

public class Color {

    public static final Color WHITE = new Color(0xFFFFFFFF);
    public int r;
    public int g;
    public int b;
    public int a;

    public Color(int rgba) {

        r = (rgba >> 24) & 0xFF;
        g = (rgba >> 16) & 0xFF;
        b = (rgba >> 8) & 0xFF;
        a = rgba & 0xFF;
    }

    public Color(int r, int g, int b, int a) {

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(float r, float g, float b, float a) {

        this.r = (int) (r * 255);
        this.g = (int) (g * 255);
        this.b = (int) (b * 255);
        this.a = (int) (a * 255);
    }

    protected int scale(int channel, float scale) {

        return MathHelper.clamp((int) (channel * scale), 0, 255);
    }

    public Color scaleRGB(float scale) {

        return new Color(scale(r, scale), scale(g, scale), scale(b, scale), a);
    }

    public Color scaleRGB(float sr, float sg, float sb) {

        return new Color(scale(r, sr), scale(g, sg), scale(b, sb), a);
    }

    public Color scaleAlpha(float scale) {

        return new Color(r, g, b, scale(a, scale));
    }

    public Color scaleRGBA(float sr, float sg, float sb, float sa) {

        return new Color(scale(r, sr), scale(g, sg), scale(b, sb), scale(a, sa));
    }

    public int pack() {

        return (r << 24) | (g << 16) | (b << 8) | a;
    }

    public boolean equals(Color other) {

        return other.r == r && other.g == g && other.b == b && other.a == a;
    }

    public boolean equals(int other) {

        return equals(new Color(other));
    }

    public boolean sameRGB(Color other) {

        return other.r == r && other.g == g && other.b == b;
    }

    public boolean sameRGB(int other) {

        return sameRGB(new Color(other));
    }

    //Linearly interpolate between colors
    public Color mix(Color other, float amount) {

        return new Color(MathHelper.interpolate(r, other.r, amount),
                MathHelper.interpolate(g, other.g, amount),
                MathHelper.interpolate(b, other.b, amount),
                MathHelper.interpolate(a, other.a, amount));
    }

}
