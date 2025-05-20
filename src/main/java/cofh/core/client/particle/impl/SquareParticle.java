package cofh.core.client.particle.impl;

import cofh.core.client.particle.SpriteParticle;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.Color;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Vector4f;

import javax.annotation.Nonnull;

public class SquareParticle extends SpriteParticle {

    private SquareParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z, dx, dy, dz);
        oRoll = roll = random.nextFloat() * MathHelper.F_PI;
        hasPhysics = false;
        friction = 1;
    }

    @Override
    protected Color getColor(float time, float pTicks) {

        return c0.scaleAlpha(1 - MathHelper.easeInCubic(time / duration));
    }

    @Override
    protected int getLightColor(float pTicks, double x, double y, double z) {

        return RenderHelper.FULL_BRIGHT;
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new SquareParticle(data, level, spriteSet, x, y, z, dx, dy, dz);
    }

}
