package cofh.core.client.particle;

import cofh.core.client.particle.options.ColorParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;

public abstract class ColorParticle extends CustomRenderParticle {

    public int rgba0;

    public ColorParticle(ColorParticleOptions data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, x, y, z, dx, dy, dz);
        setPrimColor(data.rgba0);
    }

    protected void setPrimColor(int rgba) {

        this.rgba0 = rgba;
    }

}
