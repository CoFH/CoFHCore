package cofh.core.client.particle;

import cofh.core.client.particle.options.BiColorParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;

public abstract class PointToPointParticle extends ColorParticle {

    protected int rgba1 = 0xFFFFFFFF;

    public PointToPointParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, 0, 0, 0);
        hasPhysics = false;
        AABB bound = new AABB(sx, sy, sz, ex, ey, ez);
        this.setBoundingBox(bound);
        this.bbWidth = (float) Math.max(bound.getXsize(), bound.getZsize());
        this.bbHeight = (float) bound.getYsize();
        setSecColor(data.rgba1);
    }

    protected void setSecColor(int rgba) {

        this.rgba1 = rgba;
    }

    protected void setColors(int rgba0, int rgba1) {

        setPrimColor(rgba0);
        setSecColor(rgba1);
    }

}
