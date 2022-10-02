package cofh.core.client.particle.impl;

import cofh.core.client.particle.TextureParticleCoFH;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn (Dist.CLIENT)
public class MistParticle extends TextureParticleCoFH {

    protected float baseAlpha = 1.0F;

    protected MistParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z);

        float var = 0.05F;
        xd = dx + rand.nextFloat(-var, var);
        yd = dy + rand.nextFloat(-var, var);
        zd = dz + rand.nextFloat(-var, var);

        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
        setSpriteFromAge(sprites);
    }

    @Override
    public void setColor(int rgba) {

        this.rCol = ((rgba >> 24) & 0xFF) * 0.0039215686F;
        this.gCol = ((rgba >> 16) & 0xFF) * 0.0039215686F;
        this.bCol = ((rgba >> 8) & 0xFF) * 0.0039215686F;
        this.baseAlpha = (rgba & 0xFF) * 0.0039215686F;
    }

    @Override
    public void render(VertexConsumer consumer, Camera info, float partialTicks) {

        float progress = (age + partialTicks) / lifetime;
        float q = 2 * progress - 1;
        q *= q;
        alpha = baseAlpha * Math.max((1 - q * q) * MathHelper.cos(0.25F * MathHelper.F_PI * progress), 0);
        this.quadSize = this.bbWidth * MathHelper.sin(0.25F * MathHelper.F_PI * (progress + 1));
        super.render(consumer, info, partialTicks);
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
