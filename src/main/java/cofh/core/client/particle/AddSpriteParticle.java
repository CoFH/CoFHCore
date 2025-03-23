package cofh.core.client.particle;

import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.RenderTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;

import javax.annotation.Nonnull;

public class AddSpriteParticle extends SpriteParticle{

    protected AddSpriteParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {
        super(data, level, sprites, x, y, z, dx, dy, dz);
    }

    public ParticleRenderType getRenderType() {
        return RenderTypes.PARTICLE_SHEET_ADDITIVE_TINTED;
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new AddSpriteParticle(data, level, spriteSet, x, y, z, dx, dy, dz);
    }
}
