package cofh.core.client.particle;

import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.Color;
import net.minecraft.client.multiplayer.ClientLevel;

public abstract class ColorParticle extends CoFHParticle {

    public Color c0 = Color.WHITE;

    public ColorParticle(ColorParticleOptions data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, x, y, z, dx, dy, dz);
        setColor0(data.rgba0);
    }

    protected void setColor0(int rgba) {

        this.c0 = Color.fromRGBA(rgba);
    }

    protected void setColor0(Color color) {

        this.c0 = color;
    }

}
