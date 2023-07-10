package cofh.core.client.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nonnull;

public class FrostParticle extends TextureSheetParticle {

    private FrostParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {

        super(level, x, y, z, dx, dy, dz);
        lifetime = 40 + random.nextInt(20);

        xd = dx;
        yd = dy;
        zd = dz;
        this.gravity = random.nextFloat() * 0.02F + 0.01F;

        bCol = 1.0F - (random.nextFloat() * 0.15F);
        rCol = gCol = bCol - (random.nextFloat() * 0.15F);
    }

    @Override
    public void tick() {

        super.tick();
        alpha *= .975F;
    }

    @Override
    public ParticleRenderType getRenderType() {

        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        //return RenderTypes.PARTICLE_SHEET_OVER;
    }

    @Nonnull
    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> {
            FrostParticle particle = new FrostParticle(level, x, y, z, dx, dy, dz);
            particle.pickSprite(spriteSet);
            particle.setAlpha(level.random.nextFloat() * 0.2F + 0.7F);
            return particle;
        };
    }

}
