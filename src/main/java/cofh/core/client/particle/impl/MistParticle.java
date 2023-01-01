package cofh.core.client.particle.impl;

import cofh.core.client.particle.SpriteParticle;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn (Dist.CLIENT)
public class MistParticle extends SpriteParticle {

    protected Color baseColor;

    protected MistParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z, dx, dy, dz);
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
        baseColor = new Color(data.rgba0);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        float progress = time / duration;
        float q = 2 * progress - 1;
        q *= q;
        setColor0(baseColor.scaleAlpha(Math.max((1 - q * q) * MathHelper.cos(0.25F * MathHelper.F_PI * progress), 0)));

        //Only set render size based off BB size
        this.size = this.bbWidth * MathHelper.sin(0.25F * MathHelper.F_PI * (progress + 1));
        super.render(stack, buffer, consumer, packedLight, time, pTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_OVER;
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new MistParticle(data, level, spriteSet, x, y, z, dx, dy, dz);
    }

    //@Nonnull
    //public static ParticleProvider<SimpleParticleType> ice(SpriteSet spriteSet) {
    //
    //    return (data, level, x, y, z, dx, dy, dz) -> {
    //
    //        MistParticle p = new MistParticle(level, x, y, z, dx, dy, dz);
    //        p.pickSprite(spriteSet);
    //        p.rCol = 0.7F - p.random.nextFloat(0.1F);
    //        p.gCol = 0.85F - p.random.nextFloat(0.1F);
    //        return p;
    //    };
    //}

}
