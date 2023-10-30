package cofh.lib.util.helpers;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

/**
 * Contains various math-related helper functions. Often faster than conventional implementations.
 *
 * @author King Lemming
 */
public final class MathHelper {

    private MathHelper() {

    }

    public static final RandomSource RANDOM = RandomSource.create();
    public static final double PI = Math.PI;
    public static final double PI_2 = Math.PI * 2.0D;
    public static final double PHI = 1.618033988749894;
    public static final double TO_DEG = 57.29577951308232;
    public static final double TO_RAD = 0.017453292519943;
    public static final double SQRT_2 = 1.414213562373095;

    public static final float F_PI = (float) Math.PI;
    public static final float F_TAU = (float) Math.PI * 2.0F;
    public static final float F_TO_DEG = (float) TO_DEG;
    public static final float F_TO_RAD = (float) TO_RAD;

    public static final double[] SIN_TABLE = new double[65536];
    public static final float[] ASIN_TABLE = new float[65536];

    public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
    public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
    public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
    public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
    public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
    public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);
    public static Vector3f ZERO = new Vector3f(0.0F, 0.0F, 0.0F);

    static {
        for (int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = Math.sin(i / 65536D * 2 * Math.PI);
        }
        SIN_TABLE[0] = 0;
        SIN_TABLE[16384] = 1;
        SIN_TABLE[32768] = 0;
        SIN_TABLE[49152] = -1;

        for (int i = 0; i < 65536; ++i) {
            ASIN_TABLE[i] = (float) Math.asin((i - 32768) / 32768D);
        }
    }

    // region RANDOMS
    public static int nextInt(RandomSource rand, int min, int max) {

        return min >= max ? min : rand.nextInt(max - min + 1) + min;
    }

    public static int nextInt(int min, int max) {

        return nextInt(RANDOM, min, max);
    }

    public static float nextFloat(RandomSource rand, float min, float max) {

        return min >= max ? min : rand.nextFloat() * (max - min) + min;
    }

    public static float nextFloat(float min, float max) {

        return nextFloat(RANDOM, min, max);
    }

    public static double nextDouble(RandomSource rand, double min, double max) {

        return min >= max ? min : rand.nextDouble() * (max - min) + min;
    }

    public static double nextDouble(double min, double max) {

        return nextDouble(RANDOM, min, max);
    }

    public static int binomialDist(int trials, double success) {

        int ret = 0;
        for (int i = 0; i < trials; ++i) {
            if (RANDOM.nextDouble() < success) {
                ++ret;
            }
        }
        return ret;
    }

    public static int weightedRound(double d) {

        return weightedRound(d, RANDOM);
    }

    public static int weightedRound(double d, RandomSource source) {

        int base = floor(d);
        if (source.nextDouble() < d - base) {
            return base + 1;
        }
        return base;
    }
    // endregion

    // region TRIGONOMETRY
    public static double sin(double d) {

        return SIN_TABLE[(int) ((float) d * 10430.378F) & 65535];
    }

    public static float sin(float d) {

        return Mth.sin(d);
    }

    public static double cos(double d) {

        return SIN_TABLE[(int) ((float) d * 10430.378F + 16384.0F) & 65535];
    }

    public static float cos(float d) {

        return Mth.cos(d);
    }

    public static float asin(float d) {

        return ASIN_TABLE[(int) (d * 32768.0F + 32768.0F) & 65535];
    }

    public static float acos(float d) {

        return asin(-d) + F_PI * 0.5F;
    }
    // endregion

    // region EASING
    public static float easeInCubic(float a) {

        return a * a * a;
    }

    public static float easeOutCubic(float a) {

        a = 1.0F - a;
        return 1.0F - a * a * a;
    }

    public static float easeInOutCubic(float a) {

        return a < 0.5 ? 4 * easeInCubic(a) : 4 * easeInCubic(a - 1) + 1;
    }

    public static float easePlateau(float a) {

        return a <= 0.3333F ? sin(a * F_PI * 1.5F) : (a > 0.66666F ? sin((a * 1.5F - 0.5F) * F_PI) : 1);
    }
    // endregion

    public static int clamp(int a, int min, int max) {

        return a < min ? min : (a > max ? max : a);
    }

    public static float clamp(float a, float min, float max) {

        return a < min ? min : (a > max ? max : a);
    }

    public static double clamp(double a, double min, double max) {

        return a < min ? min : (a > max ? max : a);
    }

    public static float approachLinear(float a, float b, float max) {

        return a > b ? a - b < max ? b : a - max : b - a < max ? b : a + max;
    }

    public static double approachLinear(double a, double b, double max) {

        return a > b ? a - b < max ? b : a - max : b - a < max ? b : a + max;
    }

    public static int interpolate(int a, int b, float d) {

        return (int) (a + (b - a) * d);
    }

    public static float interpolate(float a, float b, float d) {

        return a + (b - a) * d;
    }

    public static double interpolate(double a, double b, double d) {

        return a + (b - a) * d;
    }

    public static double approachExp(double a, double b, double ratio) {

        return a + (b - a) * ratio;
    }

    public static double approachExp(double a, double b, double ratio, double cap) {

        double d = (b - a) * ratio;

        if (Math.abs(d) > cap) {
            d = Math.signum(d) * cap;
        }
        return a + d;
    }

    public static double retreatExp(double a, double b, double c, double ratio, double kick) {

        double d = (Math.abs(c - a) + kick) * ratio;

        if (d > Math.abs(b - a)) {
            return b;
        }
        return a + Math.signum(b - a) * d;
    }

    public static boolean between(double a, double x, double b) {

        return a <= x && x <= b;
    }

    public static int approachExpI(int a, int b, double ratio) {

        int r = (int) Math.round(approachExp(a, b, ratio));
        return r == a ? b : r;
    }

    public static int retreatExpI(int a, int b, int c, double ratio, int kick) {

        int r = (int) Math.round(retreatExp(a, b, c, ratio, kick));
        return r == a ? b : r;
    }

    public static float sqrt(float a) {

        return (float) Math.sqrt(a);
    }

    public static float invSqrt(float a) {

        return 1 / sqrt(a);
    }

    public static float distSqr(float... a) {

        float d = 0.0F;
        for (float f : a) {
            d += f * f;
        }
        return d;
    }

    public static double distSqr(double... a) {

        double d = 0.0F;
        for (double f : a) {
            d += f * f;
        }
        return d;
    }

    public static float dist(float... a) {

        return sqrt(distSqr(a));
    }

    public static double dist(double... a) {

        return Math.sqrt(distSqr(a));
    }

    public static float invDist(float... a) {

        return 1 / dist(a);
    }

    public static double invDist(double... a) {

        return 1 / dist(a);
    }

    // A cross between a sin and a square wave function. Has period 4, returns a value between -1 and 1.
    public static float bevel(float f) {

        int floor = floor(f);
        if (f - floor < 0.66667F && (floor & 1) == 0) {
            return -cos(F_PI * 1.5F * f);
        }
        return ((floor >> 1) & 1) == 0 ? 1 : -1;
    }

    public static float frac(float f) {

        return f - floor(f);
    }

    /**
     * Unchecked implementation to round a number. Parameter should be known to be valid in advance.
     */
    public static int round(double d) {

        return (int) (d + 0.5D);
    }

    /**
     * Unchecked implementation to round a number up. Parameter should be known to be valid in advance.
     */
    public static int ceil(double d) {

        return (int) (d + 0.9999D);
    }

    /**
     * Unchecked implementation to round a number down. Parameter should be known to be valid in advance.
     */
    public static int floor(double d) {

        int i = (int) d;
        return d < i ? i - 1 : i;
    }

    /**
     * Unchecked implementation to determine the smaller of two Floats. Parameters should be known to be valid in advance.
     */
    public static float minF(float a, float b) {

        return a < b ? a : b;
    }

    public static float minF(int a, float b) {

        return a < b ? a : b;
    }

    public static float minF(float a, int b) {

        return a < b ? a : b;
    }

    /**
     * Unchecked implementation to determine the larger of two Floats. Parameters should be known to be valid in advance.
     */
    public static float maxF(float a, float b) {

        return a > b ? a : b;
    }

    public static float maxF(int a, float b) {

        return a > b ? a : b;
    }

    public static float maxF(float a, int b) {

        return a > b ? a : b;
    }

    public static double maxAbs(double a, double b) {

        if (a < 0.0D) {
            a = -a;
        }
        if (b < 0.0D) {
            b = -b;
        }
        return a > b ? a : b;
    }

    public static int setBit(int mask, int bit, boolean value) {

        mask |= (value ? 1 : 0) << bit;
        return mask;
    }

    public static boolean isBitSet(int mask, int bit) {

        return (mask & 1 << bit) != 0;
    }

    /**
     * Perpendicular distance from a point to a line.
     */
    public static double pointToLineDist(Vec3 point, Vec3 lineStart, Vec3 lineEnd) {

        Vec3 center = point.subtract(lineStart);
        Vec3 disp = lineEnd.subtract(lineStart);
        return disp.cross(center).length() / disp.length();
    }

    public static Vec2 decomposeLookVector(Vec3 v) {

        float xRot = v.y <= -1 ? MathHelper.F_PI * 0.5F : asin((float) -v.y);
        if (Math.abs(-v.y) > 0.9999) {
            return new Vec2(xRot * F_TO_DEG, 0);
        }
        float yRot = asin((float) v.x / cos(xRot));
        if (v.z < 0) {
            yRot = MathHelper.F_PI - yRot;
        }
        return new Vec2(xRot * F_TO_DEG, -yRot * F_TO_DEG);
    }

    // 1.20 Hacks below this

    // TODO: Is there a more proper way to handle this now w/ new library?

    public static Vector3f transform(Vector3f vec, Matrix3f mat) {

        float f = vec.x;
        float f1 = vec.y;
        float f2 = vec.z;

        vec.x = mat.m00 * f + mat.m01 * f1 + mat.m02 * f2;
        vec.y = mat.m10 * f + mat.m11 * f1 + mat.m12 * f2;
        vec.z = mat.m20 * f + mat.m21 * f1 + mat.m22 * f2;

        return vec;
    }

    public static Vector4f transform(Vector4f vec, Matrix4f mat) {

        float f = vec.x;
        float f1 = vec.y;
        float f2 = vec.z;
        float f3 = vec.w;

        vec.x = mat.m00() * f + mat.m01() * f1 + mat.m02() * f2 + mat.m03() * f3;
        vec.y = mat.m10() * f + mat.m11() * f1 + mat.m12() * f2 + mat.m13() * f3;
        vec.z = mat.m20() * f + mat.m21() * f1 + mat.m22() * f2 + mat.m23() * f3;
        vec.w = mat.m30() * f + mat.m31() * f1 + mat.m32() * f2 + mat.m33() * f3;

        return vec;
    }

    public static Quaternionf quaternion(float x, float y, float z) {

        return new Quaternionf().rotationXYZ(x * ((float) Math.PI / 180F), y * ((float) Math.PI / 180F), z * ((float) Math.PI / 180F));
    }

    public static Vector4f toVector4f(Vector3f vec) {

        return new Vector4f(vec.x, vec.y, vec.z, 1.0F);
    }
}
