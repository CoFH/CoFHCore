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
public class FireParticle extends TextureParticleCoFH {

    protected float baseAlpha;

    private FireParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z);
        xd = dx;
        yd = dy;
        zd = dz;
        setPos(x + dx, y + dy, z + dz);
        gravity = -0.3F;
        friction = 0.9F;
        //this.fLifetime = this.lifetime = 6 + this.random.nextInt(4);
        this.setSpriteFromAge(this.sprites);
        setColor(0xFFFFFFFF);
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
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
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        float easeCos = MathHelper.cos(progress * 0.5F * MathHelper.F_PI);
        //int rgba = VFXHelper.mix(1.0F - easeCos, 0xe9fa50ff, 0xf2461bff, 0xcc0f02ff, 0x363534ff);
        //this.rCol = ((rgba >> 24) & 0xFF) * 0.0039215686F;
        //this.gCol = ((rgba >> 16) & 0xFF) * 0.0039215686F;
        //this.bCol = ((rgba >> 8) & 0xFF) * 0.0039215686F;
        alpha = baseAlpha * easeCub;
        super.render(consumer, info, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_ADDITIVE_MUTLIPLY;
    }

    @Override
    public int getLightColor(float partialTicks) {

        return 0x00F000F0;
    }

    @Override
    public void tick() {

        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new FireParticle(data, level, spriteSet, x, y, z, dx, dy, dz);
    }

}
