package cofh.core.client.particle.impl;

import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.Color;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nonnull;

public class MistParticle extends GasParticle {

    protected MistParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z, dx, dy, dz);
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
        groundFriction = 0.3F;
    }

    @Override
    protected Color getColor(float time, float pTicks) {

        float progress = time / duration;
        float q = 2 * progress - 1;
        q *= q;
        return c0.scaleAlpha(Math.max((1 - q * q) * MathHelper.cos(0.25F * MathHelper.F_PI * progress), 0));
    }

    @Override
    protected float getSize(float time, float pTicks) {

        return size * MathHelper.sin(0.25F * MathHelper.F_PI * (time / duration + 1));
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new MistParticle(data, level, spriteSet, x, y, z, dx, dy, dz);
    }

}
