package cofh.core.client.particle.impl;

import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.client.TransientLightManager;
import cofh.core.common.config.CoreClientConfig;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;

public class FireParticle extends GasParticle {

    private FireParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z, dx, dy, dz);
        gravity = -0.3F;
        friction = 0.9F;
        groundFriction = 0.2F;
    }

    @Override
    public void tick() {

        super.tick();
        if (CoreClientConfig.particleDynamicLighting.get() && this.age >= this.delay) {
            int x = MathHelper.floor(this.x);
            int y = MathHelper.floor(this.y);
            int z = MathHelper.floor(this.z);
            TransientLightManager.addLight(level, BlockPos.asLong(x, y, z), getDynamicLightLevel());
        }
    }

    protected int getDynamicLightLevel() {

        return Math.max(0, MathHelper.floor(10 - 10 * (age - delay) / duration)) + 1;
    }

    @Override
    protected float getSize(float time, float pTicks) {

        float progress = time / duration;
        return size * MathHelper.sin(0.25F * MathHelper.F_PI * (progress + 1));
    }

    @Override
    public ParticleRenderType getRenderType() {

        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTicks) {

        return RenderHelper.FULL_BRIGHT;
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new FireParticle(data, level, spriteSet, x, y, z, dx, dy, dz);
    }

}
