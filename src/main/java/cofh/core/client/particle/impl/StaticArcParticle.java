package cofh.core.client.particle.impl;

import cofh.core.client.particle.options.BiColorParticleOptions;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Vector3f;

public class StaticArcParticle extends ArcParticle {

    protected LongList path;

    public StaticArcParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        recalcDisplacement(sx, sy, sz, ex, ey, ez);
        this.path = traversePath(sx, sy, sz, ex, ey, ez);
    }

    @Override
    protected LongList getPath() {

        return path;
    }

}
