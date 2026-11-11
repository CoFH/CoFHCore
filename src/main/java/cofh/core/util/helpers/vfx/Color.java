package cofh.core.util.helpers.vfx;

import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.recipes.RecipeJsonUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ExtraCodecs;

public class Color {

    protected static final Codec<Integer> CHANNEL = ExtraCodecs.intRange(0, 0xFF);
    public static final Codec<Color> CODEC = RecipeJsonUtils.merge(
            Codec.STRING.comapFlatMap((string) -> {
                String hex = string.startsWith("#") ? string.substring(1) : string;
                if (hex.matches("[0-9a-fA-F]{6}|[0-9a-fA-F]{8}")) {
                    int value = Integer.parseUnsignedInt(hex, 16);
                    return DataResult.success(hex.length() == 6 ? fromRGB(value) : fromRGBA(value));
                }
                return DataResult.error(() -> "Not a valid hex color: " + string);
            }, (color) -> color.a == 0xFF ? String.format("%06X", color.toRGB()) : String.format("%08X", color.toRGBA())),
            Codec.INT.xmap(Color::fromRGBA, Color::toRGBA),
            CHANNEL.listOf().comapFlatMap((values) -> values.size() == 3 || values.size() == 4 ? DataResult.success(
                            fromRGBA(values.get(0), values.get(1), values.get(2), values.size() == 4 ? values.get(3) : 0xFF))
                            : DataResult.error(() -> "Expected 3 or 4 channels: " + values),
                    (color) -> List.of(color.r, color.g, color.b, color.a)),
            RecordCodecBuilder.create((builder) -> builder.group(
                    CHANNEL.fieldOf("r").forGetter((color) -> color.r),
                    CHANNEL.fieldOf("g").forGetter((color) -> color.g),
                    CHANNEL.fieldOf("b").forGetter((color) -> color.b),
                    CHANNEL.optionalFieldOf("a", 0xFF).forGetter((color) -> color.a)
            ).apply(builder, Color::fromRGBA)));

    public static final float INT_TO_FLOAT = 0.003921569F;
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

    public void apply(GuiGraphics graphics) {

        graphics.setColor(redFloat(), greenFloat(), blueFloat(), alphaFloat());
    }

    public Color scaleRGB(float scale) {

        return scaleRGB(scale, scale, scale);
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

    public float redFloat() {

        return r * INT_TO_FLOAT;
    }

    public float greenFloat() {

        return g * INT_TO_FLOAT;
    }

    public float blueFloat() {

        return b * INT_TO_FLOAT;
    }

    public float alphaFloat() {

        return a * INT_TO_FLOAT;
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
