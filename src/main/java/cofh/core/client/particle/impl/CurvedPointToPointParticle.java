package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Vector3f;

public abstract class CurvedPointToPointParticle extends PointToPointParticle {

    public static final Vector3f[] UNITS = {new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(1, 0, 0)};

    protected Vector3f disp;
    protected Vector3f perp;
    protected float eccentricity;

    public CurvedPointToPointParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        this.disp = new Vector3f((float) (ex - sx), (float) (ey - sy), (float) (ez - sz));
        this.perp = dir(disp, random.nextFloat() * MathHelper.F_TAU);
        this.eccentricity = getEccentricity(disp);
    }

    protected float getEccentricity(Vector3f disp) {

        return (1 - Math.abs(random.nextFloat() - random.nextFloat())) * MathHelper.sqrt(disp.length()) * 0.3F;
    }

    public static Vector3f perp(Vector3f v) {

        float min = Float.MAX_VALUE;
        Vector3f out = UNITS[0];
        for (Vector3f unit : UNITS) {
            float dot = Math.abs(v.dot(unit));
            if (dot < min) {
                out = unit;
                min = dot;
            }
        }
        return new Vector3f(v).cross(out);
    }

    public static Vector3f dir(Vector3f disp, float roll) {

        float sin = MathHelper.sin(roll);
        float cos = MathHelper.cos(roll);
        Vector3f perp = perp(disp).normalize();
        return perp.mul(sin).add(new Vector3f(perp).cross(disp).normalize().mul(cos));
    }

    public static Vector3f pos(Vector3f disp, Vector3f perp, float eccentricity, float progress) {

        return new Vector3f(perp).mul(eccentricity * MathHelper.sin(progress * MathHelper.F_PI)).add(new Vector3f(disp).mul(progress));
    }

    public static Vector3f tangent(Vector3f disp, Vector3f perp, float eccentricity, float progress) {

        return new Vector3f(perp).mul(eccentricity * MathHelper.cos(progress * MathHelper.F_PI) * MathHelper.F_PI).add(disp);
    }

}
