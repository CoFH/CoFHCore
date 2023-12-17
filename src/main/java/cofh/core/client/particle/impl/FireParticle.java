package cofh.core.client.particle.impl;

import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.common.TransientLightManager;
import cofh.core.common.config.CoreClientConfig;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
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
            TransientLightManager.addLight(BlockPos.asLong(x, y, z), getDynamicLightLevel());
        }
    }

    protected int getDynamicLightLevel() {

        return Math.max(0, MathHelper.floor(10 - 10 * (age - delay) / duration)) + 1;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        float progress = time / duration;
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        //float easeCos = MathHelper.cos(progress * 0.5F * MathHelper.F_PI);
        //int rgba = VFXHelper.mix(1.0F - easeCos, 0xe9fa50ff, 0xf2461bff, 0xcc0f02ff, 0x363534ff);
        //this.rCol = ((rgba >> 24) & 0xFF) * 0.0039215686F;
        //this.gCol = ((rgba >> 16) & 0xFF) * 0.0039215686F;
        //this.bCol = ((rgba >> 8) & 0xFF) * 0.0039215686F;
        setColor0(baseColor.scaleAlpha(easeCub));

        //Only set render size based off BB size
        this.size = this.bbWidth * MathHelper.sin(0.25F * MathHelper.F_PI * (progress + 1));
        super.render(stack, buffer, consumer, packedLight, time, pTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_ADDITIVE_MULTIPLY;
    }

    @Override
    public int getLightColor(float partialTicks) {

        return 0x00F000F0;
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new FireParticle(data, level, spriteSet, x, y, z, dx, dy, dz);
    }

}
