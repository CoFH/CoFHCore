package cofh.core.client.particle.impl;

import cofh.core.client.particle.TextureParticleCoFH;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn (Dist.CLIENT)
public class BlastParticle extends TextureParticleCoFH {

    private BlastParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z);
        gravity = -0.1F;
        friction = 0.9F;
        float var = 0.05F;
        xd = dx + random.nextFloat(-var, var);
        yd = dy + random.nextFloat(-var, var);
        zd = dz + random.nextFloat(-var, var);
        //this.fLifetime = this.lifetime = 6 + this.random.nextInt(4);
        setSpriteFromAge(sprites);
        oRoll = roll = 0.5F * (float) random.nextGaussian();
    }

    @Override
    public ParticleRenderType getRenderType() {

        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTicks) {

        return 0x00F000F0; //TODO
    }

    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, sx, sy, sz, size, rgba, speed) -> new BlastParticle(data, level, spriteSet, sx, sy, sz, size, rgba, speed);
    }

}
