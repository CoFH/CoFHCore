package cofh.core.client.particle;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.vfx.Color;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;

public abstract class PointToPointParticle extends ColorParticle {

    protected Color c1 = Color.WHITE;
    //The displacement, i.e. the start point subtracted from the end point.
    protected Vector3f disp;

    /**
     * A particle that goes from some point to another, such as a beam of light.
     * @param sx X coordinate of the starting point.
     * @param ex X coordinate of the ending point.
     */
    public PointToPointParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, 0, 0, 0);
        hasPhysics = false;
        AABB bound = new AABB(sx, sy, sz, ex, ey, ez);
        this.setBoundingBox(bound);
        this.bbWidth = (float) Math.max(bound.getXsize(), bound.getZsize());
        this.bbHeight = (float) bound.getYsize();
        setColor1(data.rgba1);
        this.disp = new Vector3f((float) (ex - sx), (float) (ey - sy), (float) (ez - sz));
    }

    protected void setColor1(int rgba) {

        this.c1 = Color.fromRGBA(rgba);
    }

    protected void setColor1(Color color) {

        this.c1 = color;
    }

}
