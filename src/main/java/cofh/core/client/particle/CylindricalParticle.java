package cofh.core.client.particle;

import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class CylindricalParticle extends ColorParticle {

    public float height;
    public Quaternion rotation = Quaternion.ONE;

    public CylindricalParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z) {

        super(data, level, x, y, z, 0, 0, 0);
        setHeight(data.height);
        recalcBB();
        hasPhysics = false;
    }

    public CylindricalParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, 0, 0, 0);
        setHeight(data.height);
        setDirection(xDir, yDir, zDir);
        recalcBB(xDir, yDir, zDir);
        hasPhysics = false;
    }

    protected void setHeight(float height) {

        this.height = height;
    }

    protected void setDirection(double xDir, double yDir, double zDir) {

        setDirection(new Vec3(xDir, yDir, zDir));
    }

    protected void setDirection(Vec3 direction) {

        rotation = VFXHelper.alignVertical(new Vector3f(direction));
    }

    protected void recalcBB(double xDir, double yDir, double zDir) {

        float xc = (float) xDir;
        float yc = (float) yDir;
        float zc = (float) zDir;
        float distSqr = MathHelper.distSqr(xc, yc, zc);
        if (distSqr < 0.0001) {
            recalcBB();
            return;
        }
        float invLen = 1.0F / MathHelper.sqrt(distSqr);
        xc *= invLen;
        yc *= invLen;
        zc *= invLen;

        float xl = height * xc + size * MathHelper.sqrt(1 - xc * xc);
        float yl = height * yc + size * MathHelper.sqrt(1 - yc * yc);
        float zl = height * zc + size * MathHelper.sqrt(1 - zc * zc);
        Vec3 pos = new Vec3(x, y, z);
        setBoundingBox(new AABB(pos, pos).inflate(xl * 0.5F, yl * 0.5F, zl * 0.5F));
        bbWidth = Math.max(xl, zl);
        bbHeight = yl;
    }

    protected void recalcBB() {

        float radius = size * 0.5F;
        Vec3 pos = new Vec3(x, y, z);
        setBoundingBox(new AABB(pos, pos).inflate(radius, height * 0.5F, radius));
        bbWidth = size;
        bbHeight = height;
    }

}
