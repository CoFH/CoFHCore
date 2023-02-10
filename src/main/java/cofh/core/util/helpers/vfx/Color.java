package cofh.core.util.helpers.vfx;

import cofh.lib.util.helpers.MathHelper;

public class Color {

    public static final Color WHITE = fromRGBA(0xFFFFFFFF);
    public final int r;
    public final int g;
    public final int b;
    public final int a;

    protected Color(int r, int g, int b, int a) {

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public static Color fromRGB(int rgb) {

        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, 0xFF);
    }

    public static Color fromRGB(int r, int g, int b) {

        return new Color(r, g, b, 0xFF);
    }

    public static Color fromRGBA(int rgba) {

        return new Color((rgba >> 24) & 0xFF, (rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF);
    }

    public static Color fromRGBA(int r, int g, int b, int a) {

        return new Color(r, g, b, a);
    }

    public static Color fromFloat(float r, float g, float b, float a) {

        return new Color((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
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

    public int toRGBA() {

        return (r << 24) | (g << 16) | (b << 8) | a;
    }

    public int toRGB() {

        return (r << 16) | (g << 8) | b;
    }

    public boolean equals(Color other) {

        return other.r == r && other.g == g && other.b == b && other.a == a;
    }

    public boolean equals(int other) {

        return equals(fromRGBA(other));
    }

    public boolean sameRGB(Color other) {

        return other.r == r && other.g == g && other.b == b;
    }

    public boolean sameRGB(int other) {

        return sameRGB(fromRGBA(other));
    }

    //Linearly interpolate between colors
    public Color mix(Color other, float amount) {

        return new Color(MathHelper.interpolate(r, other.r, amount),
                MathHelper.interpolate(g, other.g, amount),
                MathHelper.interpolate(b, other.b, amount),
                MathHelper.interpolate(a, other.a, amount));
    }

    /**
     * Implementation of the color distance algorithm proposed in <a href="https://www.compuphase.com/cmetric.htm">this paper</a>.
     *
     * @return The approximate subjective difference between the colors.
     */
    public double dist(Color other) {

        return Math.sqrt(distSqr(other));
    }

    public double distSqr(Color other) {

        long rMean = (this.r + other.r) >> 1;
        long r = this.r - other.r;
        long g = this.g - other.g;
        long b = this.b - other.b;
        return (((512 + rMean) * r * r) >> 8) + 4 * g * g + (((767 - rMean) * b * b) >> 8);
    }

    @Override
    public String toString() {

        return r + "R " + g + "G " + b + "B " + a + "A";
    }

}
