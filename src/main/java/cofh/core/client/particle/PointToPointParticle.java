package cofh.core.client.particle;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.vfx.Color;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;

public abstract class PointToPointParticle extends ColorParticle {

    protected Color c1 = Color.WHITE;

    public PointToPointParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, 0, 0, 0);
        hasPhysics = false;
        AABB bound = new AABB(sx, sy, sz, ex, ey, ez);
        this.setBoundingBox(bound);
        this.bbWidth = (float) Math.max(bound.getXsize(), bound.getZsize());
        this.bbHeight = (float) bound.getYsize();
        setColor1(data.rgba1);
    }

    protected void setColor1(int rgba) {

        this.c1 = new Color(rgba);
    }

    protected void setColor1(Color color) {

        this.c1 = color;
    }

}
